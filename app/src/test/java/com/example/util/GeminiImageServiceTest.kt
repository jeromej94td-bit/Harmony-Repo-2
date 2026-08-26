package com.example.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiImageServiceTest {

    @Test
    fun parseGoogleError_handles429QuotaExhausted() {
        val jsonPayload = """
            {
              "error": {
                "code": 429,
                "message": "Resource has been exhausted (e.g. check quota).",
                "status": "RESOURCE_EXHAUSTED",
                "details": [
                  {
                    "reason": "RATE_LIMIT_EXCEEDED",
                    "domain": "googleapis.com"
                  }
                ]
              }
            }
        """.trimIndent()

        val parsed = GeminiImageService.parseGoogleError(429, jsonPayload)
        assertTrue(parsed is GeminiImageException.RateLimitExceeded)
        assertEquals(429, parsed.code)
        assertEquals("RESOURCE_EXHAUSTED", parsed.status)
        assertTrue(parsed.message.contains("Nutzungslimit oder die Bild-Quota"))
        assertTrue(parsed.technicalDetails.contains("RESOURCE_EXHAUSTED"))
    }

    @Test
    fun parseGoogleError_handles400InvalidArgument() {
        val jsonPayload = """
            {
              "error": {
                "code": 400,
                "message": "Invalid prompt formatting or image dimensions.",
                "status": "INVALID_ARGUMENT"
              }
            }
        """.trimIndent()

        val parsed = GeminiImageService.parseGoogleError(400, jsonPayload)
        assertTrue(parsed is GeminiImageException.InvalidRequest)
        assertEquals(400, parsed.code)
        assertEquals("INVALID_ARGUMENT", parsed.status)
        assertTrue(parsed.message.contains("Ungültige Bildgenerierungs-Anfrage"))
    }

    @Test
    fun parseGoogleError_handles401And403AuthFailure() {
        val jsonPayload401 = """
            {
              "error": {
                "code": 401,
                "message": "API key not valid. Please pass a valid API key.",
                "status": "UNAUTHENTICATED"
              }
            }
        """.trimIndent()

        val parsed401 = GeminiImageService.parseGoogleError(401, jsonPayload401)
        assertTrue(parsed401 is GeminiImageException.AuthenticationFailed)
        assertEquals(401, parsed401.code)
        assertEquals("UNAUTHENTICATED", parsed401.status)
        assertTrue(parsed401.message.contains("Ungültiger API-Schlüssel"))

        val jsonPayload403 = """
            {
              "error": {
                "code": 403,
                "message": "Method doesn't allow unregistered callers (callers without established identity).",
                "status": "PERMISSION_DENIED"
              }
            }
        """.trimIndent()

        val parsed403 = GeminiImageService.parseGoogleError(403, jsonPayload403)
        assertTrue(parsed403 is GeminiImageException.AuthenticationFailed)
        assertEquals(403, parsed403.code)
    }

    @Test
    fun parseGoogleError_handles404ModelNotFound() {
        val jsonPayload = """
            {
              "error": {
                "code": 404,
                "message": "models/gemini-legacy-model is not found for API version v1beta",
                "status": "NOT_FOUND"
              }
            }
        """.trimIndent()

        val parsed = GeminiImageService.parseGoogleError(404, jsonPayload)
        assertTrue(parsed is GeminiImageException.ModelUnavailable)
        assertEquals(404, parsed.code)
        assertEquals("NOT_FOUND", parsed.status)
        assertTrue(parsed.message.contains("Das angeforderte Bildmodell ist für dieses Projekt nicht verfügbar"))
    }

    @Test
    fun parseGoogleError_handles500And503ServerErrors() {
        val jsonPayload503 = """
            {
              "error": {
                "code": 503,
                "message": "The service is temporarily unavailable.",
                "status": "UNAVAILABLE"
              }
            }
        """.trimIndent()

        val parsed = GeminiImageService.parseGoogleError(503, jsonPayload503)
        assertTrue(parsed is GeminiImageException.ServerError)
        assertEquals(503, parsed.code)
        assertEquals("UNAVAILABLE", parsed.status)
        assertTrue(parsed.message.contains("vorübergehend nicht erreichbar"))
    }

    @Test
    fun parseGoogleError_handlesMalformedOrEmptyBodyGracefully() {
        val parsedEmpty = GeminiImageService.parseGoogleError(429, "")
        assertTrue(parsedEmpty is GeminiImageException.RateLimitExceeded)
        assertEquals(429, parsedEmpty.code)

        val parsedHtml = GeminiImageService.parseGoogleError(502, "<html>Bad Gateway</html>")
        assertTrue(parsedHtml is GeminiImageException.Unknown)
        assertEquals(502, parsedHtml.code)
    }
}
