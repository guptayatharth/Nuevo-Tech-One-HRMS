package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Robust Direct REST Client for Gemini API as outlined in gemini-api/SKILL.md
 * Using direct JSON construction/parsing with JSONObject for maximum flexibility
 * and direct compatibility without complex serializable hierarchies.
 */
object GeminiClient {
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(
        prompt: String,
        systemInstruction: String? = null,
        isJsonResponse: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API Configuration Notice: Gemini API Key is missing. Please add a valid key in the AI Studio Secrets Panel. Running in simulated response mode for demonstration purposes."
        }

        val requestJson = JSONObject()
        
        // Contents
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()
        val partObj = JSONObject()
        partObj.put("text", prompt)
        partsArray.put(partObj)
        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        requestJson.put("contents", contentsArray)

        // System Instruction
        if (systemInstruction != null) {
            val systemObj = JSONObject()
            val sysPartsArray = JSONArray()
            val sysPartObj = JSONObject()
            sysPartObj.put("text", systemInstruction)
            sysPartsArray.put(sysPartObj)
            systemObj.put("parts", sysPartsArray)
            requestJson.put("systemInstruction", systemObj)
        }

        // Generation Config
        val configObj = JSONObject()
        if (isJsonResponse) {
            val formatObj = JSONObject()
            formatObj.put("mimeType", "application/json")
            configObj.put("responseMimeType", "application/json")
        }
        configObj.put("temperature", 0.4)
        requestJson.put("generationConfig", configObj)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)

        val urlWithKey = "$BASE_URL?key=$apiKey"
        val request = Request.Builder()
            .url(urlWithKey)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    return@withContext "API Error: ${response.code} - ${response.message}\n$errBody"
                }
                val rawResponse = response.body?.string() ?: return@withContext "Error: Empty response"
                
                val responseJson = JSONObject(rawResponse)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val candidateContent = firstCandidate.optJSONObject("content")
                    if (candidateContent != null) {
                        val parts = candidateContent.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "No text candidate returned")
                        }
                    }
                }
                return@withContext "Notice: Gemini didn't return text output inside 'candidates'."
            }
        } catch (e: Exception) {
            return@withContext "Network Exception calling Gemini: ${e.message}"
        }
    }
}
