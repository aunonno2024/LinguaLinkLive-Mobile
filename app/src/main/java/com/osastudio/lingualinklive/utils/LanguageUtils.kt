package com.osastudio.lingualinklive.utils

object LanguageUtils {

    val SUPPORTED_LANGUAGES = listOf(
        "Afrikaans", "Albanian", "Amharic", "Arabic", "Armenian",
        "Azerbaijani", "Basque", "Belarusian", "Bengali", "Bosnian",
        "Bulgarian", "Catalan", "Cebuano", "Chinese (Simplified)",
        "Chinese (Traditional)", "Corsican", "Croatian", "Czech",
        "Danish", "Dutch", "English", "Esperanto", "Estonian",
        "Finnish", "French", "Frisian", "Galician", "Georgian",
        "German", "Greek", "Gujarati", "Haitian Creole", "Hausa",
        "Hawaiian", "Hebrew", "Hindi", "Hmong", "Hungarian",
        "Icelandic", "Igbo", "Indonesian", "Irish", "Italian",
        "Japanese", "Javanese", "Kannada", "Kazakh", "Khmer",
        "Kinyarwanda", "Korean", "Kurdish", "Kyrgyz", "Lao",
        "Latin", "Latvian", "Lithuanian", "Luxembourgish", "Macedonian",
        "Malagasy", "Malay", "Malayalam", "Maltese", "Maori",
        "Marathi", "Mongolian", "Myanmar (Burmese)", "Nepali",
        "Norwegian", "Nyanja (Chichewa)", "Odia (Oriya)", "Pashto",
        "Persian", "Polish", "Portuguese", "Punjabi", "Romanian",
        "Russian", "Samoan", "Scots Gaelic", "Serbian", "Sesotho",
        "Shona", "Sindhi", "Sinhala (Sinhalese)", "Slovak", "Slovenian",
        "Somali", "Spanish", "Sundanese", "Swahili", "Swedish",
        "Tagalog (Filipino)", "Tajik", "Tamil", "Tatar", "Telugu",
        "Thai", "Turkish", "Turkmen", "Ukrainian", "Urdu",
        "Uyghur", "Uzbek", "Vietnamese", "Welsh", "Xhosa",
        "Yiddish", "Yoruba", "Zulu"
    )

    fun getLanguageCode(languageName: String): String {
        return when (languageName) {
            "Afrikaans" -> "af"
            "Albanian" -> "sq"
            "Amharic" -> "am"
            "Arabic" -> "ar"
            "Armenian" -> "hy"
            "Azerbaijani" -> "az"
            "Basque" -> "eu"
            "Belarusian" -> "be"
            "Bengali" -> "bn"
            "Bosnian" -> "bs"
            "Bulgarian" -> "bg"
            "Catalan" -> "ca"
            "Chinese (Simplified)" -> "zh"
            "Chinese (Traditional)" -> "zh-TW"
            "Croatian" -> "hr"
            "Czech" -> "cs"
            "Danish" -> "da"
            "Dutch" -> "nl"
            "English" -> "en"
            "Estonian" -> "et"
            "Finnish" -> "fi"
            "French" -> "fr"
            "German" -> "de"
            "Greek" -> "el"
            "Gujarati" -> "gu"
            "Hebrew" -> "he"
            "Hindi" -> "hi"
            "Hungarian" -> "hu"
            "Icelandic" -> "is"
            "Indonesian" -> "id"
            "Irish" -> "ga"
            "Italian" -> "it"
            "Japanese" -> "ja"
            "Javanese" -> "jv"
            "Kannada" -> "kn"
            "Kazakh" -> "kk"
            "Khmer" -> "km"
            "Korean" -> "ko"
            "Lao" -> "lo"
            "Latvian" -> "lv"
            "Lithuanian" -> "lt"
            "Malay" -> "ms"
            "Malayalam" -> "ml"
            "Maltese" -> "mt"
            "Maori" -> "mi"
            "Marathi" -> "mr"
            "Mongolian" -> "mn"
            "Nepali" -> "ne"
            "Norwegian" -> "no"
            "Pashto" -> "ps"
            "Persian" -> "fa"
            "Polish" -> "pl"
            "Portuguese" -> "pt"
            "Punjabi" -> "pa"
            "Romanian" -> "ro"
            "Russian" -> "ru"
            "Serbian" -> "sr"
            "Sindhi" -> "sd"
            "Sinhala (Sinhalese)" -> "si"
            "Slovak" -> "sk"
            "Slovenian" -> "sl"
            "Somali" -> "so"
            "Spanish" -> "es"
            "Swahili" -> "sw"
            "Swedish" -> "sv"
            "Tagalog (Filipino)" -> "tl"
            "Tamil" -> "ta"
            "Telugu" -> "te"
            "Thai" -> "th"
            "Turkish" -> "tr"
            "Ukrainian" -> "uk"
            "Urdu" -> "ur"
            "Uzbek" -> "uz"
            "Vietnamese" -> "vi"
            "Welsh" -> "cy"
            "Xhosa" -> "xh"
            "Yoruba" -> "yo"
            "Zulu" -> "zu"
            else -> "en"
        }
    }
}