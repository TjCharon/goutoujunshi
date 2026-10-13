package com.jev.probe.core

import org.junit.Assert.assertEquals
import org.junit.Test

class OpenAiEndpointTest {
    @Test
    fun appendsChatCompletionsToBaseUrl() {
        assertEquals(
            "https://example.com/v1/chat/completions",
            OpenAiEndpoint.resolve(" https://example.com/v1/ ")
        )
    }

    @Test
    fun preservesFullEndpoint() {
        assertEquals(
            "https://example.com/chat/completions",
            OpenAiEndpoint.resolve("https://example.com/chat/completions")
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsCleartextUrl() {
        OpenAiEndpoint.resolve("http://example.com/v1")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsBlankUrl() {
        OpenAiEndpoint.resolve("   ")
    }
}
