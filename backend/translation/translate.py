"""
Translation using deep-translator (free, no API key needed)
Uses Google Translate under the hood for free
"""

import asyncio
import logging

logger = logging.getLogger(__name__)


async def translate_text(text: str, source_lang: str, target_lang: str) -> str:
    """
    Translate text from source language to target language.
    """
    if source_lang == target_lang or not text.strip():
        return text

    loop = asyncio.get_event_loop()
    return await loop.run_in_executor(None, _translate_sync, text, source_lang, target_lang)


def _translate_sync(text: str, source_lang: str, target_lang: str) -> str:
    try:
        from deep_translator import GoogleTranslator
        translator = GoogleTranslator(source=source_lang, target=target_lang)
        result = translator.translate(text)
        logger.info(f"Translated: '{text[:30]}...' [{source_lang}→{target_lang}]")
        return result or text
    except Exception as e:
        logger.error(f"Translation error: {e}")
        return text