package com.example.data.model

import android.graphics.Bitmap

enum class KidScenario(val id: String, val titleDe: String, val emoji: String, val promptDesc: String) {
    BABY("baby", "Neugeborenes Baby", "👶", "a cute newborn baby wrapped in a soft warm blanket, sleeping peacefully."),
    TODDLER("toddler", "Kleinkind (2-3 Jahre)", "🧸", "a sweet playing toddler of age 2-3, holding a plush toy and smiling brightly."),
    PRESCHOOLER("preschooler", "Vorschulkind (4-5 Jahre)", "🎒", "a delightful preschooler child of age 4-5, sitting outdoors, smiling with curiosity."),
    SCHOOLKID("schoolkid", "Schulkind (6-8 Jahre)", "📚", "a happy school kid of age 6-8, carrying a small backpack, standing proudly in warm morning sun."),
    FAMILY_PORTRAIT("family", "Familienporträt", "👨‍👩‍👧‍👦", "a lovely stlyized family portrait showing both parents holding their happy smiling child warmly in a loving family hug.")
}

enum class KidStyle(val id: String, val titleDe: String, val emoji: String, val promptDesc: String) {
    ANIME_ROMANTIC("anime", "Anime Romantik", "🌸", "rendered in romantic anime illustration style, soft pastel color palette, beautiful warm glow, sparkling eye accents."),
    PHOTOREALISTIC("photo", "Fotorealistisch", "📸", "rendered in professional high-fidelity cinematic DSLR portrait photography style, soft background bokeh, natural studio lighting, ultra realistic."),
    WATERCOLOR("watercolor", "Künstlerisches Aquarell", "🎨", "rendered in artistic wet-on-wet watercolor painting style, soft splash textures, fine ink detail lines, highly elegant."),
    THREE_D_ANIME("3danime", "3D Anime", "🎮", "rendered in Pixar/Disney inspired modern 3D cartoon style, soft clay shaders, expressive lively eyes, warm volumetric lighting.")
}

enum class KidGender(val id: String, val titleDe: String, val emoji: String, val promptDesc: String) {
    SURPRISE("surprise", "Zufall / Überraschung", "🎲", "with natural beautiful gender traits as a wonderful surprise."),
    GIRL("girl", "Mädchen", "👧", "as a beautiful girl, with fine soft female facial features and cute expressions."),
    BOY("boy", "Junge", "👦", "as a handsome boy, with friendly soft male facial features and cheerful expressions."),
    TWINS("twins", "Zwillinge", "👬", "as two adorable twin children (brother and sister or identical twins) sitting side by side laughing together.")
}

data class KidGeneratorRequest(
    val userName: String?,
    val partnerName: String?,
    val userBase64: String?,          // Main photo
    val partnerBase64: String?,       // Main photo
    val additionalUserBase64: List<String>, // Additional photos for user
    val additionalPartnerBase64: List<String>, // Additional photos for partner
    val scenario: String,
    val style: String,
    val childOption: String,
    val wishes: String?,
    val locale: String? = "de"
)

data class KidGeneratorResponse(
    val ok: Boolean,
    val imageUrl: String? = null,
    val imageBase64: String? = null,
    val promptSummary: String? = null,
    val scenario: String? = null,
    val style: String? = null,
    val childOption: String? = null,
    val warnings: List<String> = emptyList(),
    val generationId: String? = null,
    val error: String? = null
)
