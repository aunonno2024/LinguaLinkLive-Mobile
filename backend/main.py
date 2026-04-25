"""
LinguaLinkLive Backend Server
GPL-2.0 License
OSA Studio
"""

import asyncio
import json
import base64
import logging
import uuid
from fastapi import FastAPI, WebSocket, WebSocketDisconnect, UploadFile, File, Form
from fastapi.responses import Response
import uvicorn

from translation.stt import transcribe_audio
from translation.translate import translate_text
from translation.tts import text_to_speech
from signaling.server import SignalingServer

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="LinguaLinkLive Backend", version="1.0.0")
signaling_server = SignalingServer()


@app.get("/")
async def root():
    return {"status": "LinguaLinkLive Backend Running", "version": "1.0.0"}


@app.post("/register")
async def register_user(data: dict):
    user_id = data.get("userId")
    username = data.get("username")
    language = data.get("language")
    logger.info(f"User registered: {user_id} ({username}) - {language}")
    return {"success": True, "message": f"User {username} registered"}


@app.post("/stt")
async def speech_to_text(
    audio: UploadFile = File(...),
    language: str = Form(...)
):
    """Convert speech audio to text"""
    try:
        audio_bytes = await audio.read()
        text = await transcribe_audio(audio_bytes, language)
        return {"text": text, "language": language}
    except Exception as e:
        logger.error(f"STT error: {e}")
        return {"text": "", "language": language}


@app.post("/translate")
async def translate(data: dict):
    """Translate text between languages"""
    try:
        text = data.get("text", "")
        source_lang = data.get("sourceLang", "en")
        target_lang = data.get("targetLang", "en")
        translated = await translate_text(text, source_lang, target_lang)
        return {"translatedText": translated}
    except Exception as e:
        logger.error(f"Translation error: {e}")
        return {"translatedText": data.get("text", "")}


@app.post("/tts")
async def text_to_speech_endpoint(data: dict):
    """Convert text to speech audio"""
    try:
        text = data.get("text", "")
        language = data.get("language", "en")
        audio_bytes = await text_to_speech(text, language)
        return Response(content=audio_bytes, media_type="audio/wav")
    except Exception as e:
        logger.error(f"TTS error: {e}")
        return Response(content=b"", media_type="audio/wav")


@app.websocket("/ws/{user_id}")
async def websocket_endpoint(websocket: WebSocket, user_id: str):
    """WebSocket for signaling and real-time translation"""
    await signaling_server.connect(websocket, user_id)
    try:
        while True:
            data = await websocket.receive_text()
            message = json.loads(data)
            await signaling_server.handle_message(user_id, message)
    except WebSocketDisconnect:
        signaling_server.disconnect(user_id)
        logger.info(f"User disconnected: {user_id}")
    except Exception as e:
        logger.error(f"WebSocket error for {user_id}: {e}")
        signaling_server.disconnect(user_id)


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)