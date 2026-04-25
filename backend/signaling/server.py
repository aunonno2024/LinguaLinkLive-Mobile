"""
WebSocket Signaling Server
Handles: WebRTC signaling, audio chunk relay with translation, messages
"""

import json
import base64
import logging
import asyncio
from fastapi import WebSocket
from typing import Dict

logger = logging.getLogger(__name__)


class SignalingServer:

    def __init__(self):
        self.connections: Dict[str, WebSocket] = {}

    async def connect(self, websocket: WebSocket, user_id: str):
        await websocket.accept()
        self.connections[user_id] = websocket
        logger.info(f"User connected: {user_id} | Total: {len(self.connections)}")

    def disconnect(self, user_id: str):
        self.connections.pop(user_id, None)
        logger.info(f"User disconnected: {user_id} | Total: {len(self.connections)}")

    async def handle_message(self, from_user_id: str, message: dict):
        msg_type = message.get("type")
        target_id = message.get("target")

        if msg_type == "audio_chunk":
            await self._handle_audio_chunk(from_user_id, target_id, message)
        elif msg_type == "message":
            await self._handle_text_message(from_user_id, target_id, message)
        else:
            # Relay signaling messages directly
            await self._relay_to(target_id, {**message, "from": from_user_id})

    async def _handle_audio_chunk(self, from_id: str, target_id: str, message: dict):
        """
        Receive audio chunk from sender, translate it, send translated audio to receiver.
        The STT → Translate → TTS pipeline runs here on the server.
        """
        try:
            from translation.stt import transcribe_audio
            from translation.translate import translate_text
            from translation.tts import text_to_speech

            audio_base64 = message.get("audio", "")
            source_lang = message.get("sourceLang", "en")
            target_lang = message.get("targetLang", "en")

            if not audio_base64:
                return

            audio_bytes = base64.b64decode(audio_base64)

            # STT
            transcript = await transcribe_audio(audio_bytes, source_lang)
            if not transcript:
                return

            logger.info(f"Transcript [{source_lang}]: {transcript[:50]}")

            # Translate
            translated = await translate_text(transcript, source_lang, target_lang)
            if not translated:
                return

            logger.info(f"Translated [{target_lang}]: {translated[:50]}")

            # TTS
            speech_bytes = await text_to_speech(translated, target_lang)
            if not speech_bytes:
                return

            # Send translated audio to the target
            translated_audio_b64 = base64.b64encode(speech_bytes).decode("utf-8")
            await self._relay_to(target_id, {
                "type": "translated_audio",
                "from": from_id,
                "audio": translated_audio_b64,
                "transcript": transcript,
                "translated": translated
            })

        except Exception as e:
            logger.error(f"Audio chunk handling error: {e}")

    async def _handle_text_message(self, from_id: str, target_id: str, message: dict):
        """Translate text message and send to target"""
        try:
            from translation.translate import translate_text

            text = message.get("text", "")
            source_lang = message.get("sourceLang", "en")
            target_lang = message.get("targetLang", "en")

            translated = await translate_text(text, source_lang, target_lang)

            await self._relay_to(target_id, {
                "type": "message",
                "from": from_id,
                "text": translated,
                "originalText": text
            })
        except Exception as e:
            logger.error(f"Message handling error: {e}")

    async def _relay_to(self, target_id: str, message: dict):
        """Send a message to a specific user"""
        if target_id and target_id in self.connections:
            try:
                await self.connections[target_id].send_text(json.dumps(message))
            except Exception as e:
                logger.error(f"Relay error to {target_id}: {e}")
                self.disconnect(target_id)
        else:
            logger.warning(f"Target {target_id} not connected")