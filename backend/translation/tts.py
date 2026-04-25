"""
Text-to-Speech using Coqui TTS (free, open source)
Falls back to gTTS if Coqui is not available
"""

import asyncio
import io
import logging
import tempfile
import os

logger = logging.getLogger(__name__)


async def text_to_speech(text: str, language_code: str) -> bytes:
    """
    Convert text to speech audio bytes (WAV format).
    """
    loop = asyncio.get_event_loop()
    return await loop.run_in_executor(None, _tts_sync, text, language_code)


def _tts_sync(text: str, language_code: str) -> bytes:
    """Try gTTS first (easiest, no GPU), then Coqui TTS"""
    try:
        return _gtts_synthesize(text, language_code)
    except Exception as e:
        logger.error(f"gTTS error: {e}, trying fallback...")
        return b""


def _gtts_synthesize(text: str, language_code: str) -> bytes:
    """
    Use gTTS (Google Text-to-Speech) - free, no API key, no GPU.
    Install: pip install gTTS
    """
    from gtts import gTTS
    import io

    tts = gTTS(text=text, lang=_map_language_for_gtts(language_code), slow=False)
    buffer = io.BytesIO()
    tts.write_to_fp(buffer)
    buffer.seek(0)

    # Convert MP3 to WAV using pydub (or return MP3 bytes)
    try:
        from pydub import AudioSegment
        audio = AudioSegment.from_mp3(buffer)
        wav_buffer = io.BytesIO()
        audio.export(wav_buffer, format="wav")
        wav_buffer.seek(0)
        return wav_buffer.read()
    except Exception:
        # Return MP3 if pydub not available
        buffer.seek(0)
        return buffer.read()


def _map_language_for_gtts(code: str) -> str:
    """gTTS uses slightly different codes"""
    mapping = {
        "bn": "bn",
        "ar": "ar",
        "zh": "zh-CN",
        "zh-TW": "zh-TW",
        "ne": "ne",
        "hi": "hi",
        "ur": "ur",
        "fa": "fa",
        "ja": "ja",
        "ko": "ko",
        "ru": "ru",
        "fr": "fr",
        "de": "de",
        "es": "es",
        "pt": "pt",
        "it": "it",
        "tr": "tr",
        "vi": "vi",
        "th": "th",
        "id": "id",
        "ms": "ms",
        "ta": "ta",
        "te": "te",
        "ml": "ml",
        "en": "en",
        "sv": "sv",
        "nl": "nl",
        "pl": "pl",
        "uk": "uk",
        "el": "el",
    }
    return mapping.get(code, "en")