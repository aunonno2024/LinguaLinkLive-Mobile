"""
Speech-to-Text using OpenAI Whisper (runs locally, no GPU needed for small model)
"""

import asyncio
import io
import tempfile
import os
import logging

logger = logging.getLogger(__name__)

# Load whisper model once at startup (use 'tiny' or 'base' for CPU speed)
_model = None

def get_whisper_model():
    global _model
    if _model is None:
        try:
            import whisper
            logger.info("Loading Whisper 'base' model...")
            _model = whisper.load_model("base")  # Use "tiny" for faster CPU inference
            logger.info("Whisper model loaded!")
        except ImportError:
            logger.error("Whisper not installed. Run: pip install openai-whisper")
            _model = None
    return _model


async def transcribe_audio(audio_bytes: bytes, language_code: str) -> str:
    """
    Transcribe audio bytes to text.
    Runs in executor to avoid blocking the event loop.
    """
    loop = asyncio.get_event_loop()
    return await loop.run_in_executor(None, _transcribe_sync, audio_bytes, language_code)


def _transcribe_sync(audio_bytes: bytes, language_code: str) -> str:
    model = get_whisper_model()
    if model is None:
        return ""

    try:
        # Write audio bytes to temp file
        with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as tmp:
            tmp.write(audio_bytes)
            tmp_path = tmp.name

        # Map language code to whisper language name
        whisper_lang = _get_whisper_language(language_code)

        # Transcribe
        result = model.transcribe(
            tmp_path,
            language=whisper_lang,
            fp16=False,  # CPU mode
            task="transcribe"
        )
        os.unlink(tmp_path)
        return result.get("text", "").strip()

    except Exception as e:
        logger.error(f"Transcription error: {e}")
        return ""


def _get_whisper_language(code: str) -> str:
    """Map ISO language codes to Whisper language names"""
    mapping = {
        "bn": "bengali",
        "ar": "arabic",
        "zh": "chinese",
        "ne": "nepali",
        "hi": "hindi",
        "ur": "urdu",
        "fa": "persian",
        "ja": "japanese",
        "ko": "korean",
        "ru": "russian",
        "fr": "french",
        "de": "german",
        "es": "spanish",
        "pt": "portuguese",
        "it": "italian",
        "tr": "turkish",
        "vi": "vietnamese",
        "th": "thai",
        "id": "indonesian",
        "ms": "malay",
        "ta": "tamil",
        "te": "telugu",
        "ml": "malayalam",
        "en": "english",
    }
    return mapping.get(code, "english")