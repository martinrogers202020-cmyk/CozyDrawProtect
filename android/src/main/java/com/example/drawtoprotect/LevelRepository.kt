package com.cozyprotect

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class LevelRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient()

    suspend fun loadAllPacks(): List<LevelPack> = withContext(Dispatchers.IO) {
        val packs = mutableListOf<LevelPack>()
        packs += loadPackFromAssets("levels/pack_001.json")
        packs += loadDownloadedPacks()
        packs
    }

    fun loadPackById(packId: String): LevelPack? {
        val assetPath = "levels/$packId.json"
        return kotlin.runCatching { loadPackFromAssets(assetPath) }.getOrNull()
            ?: loadDownloadedPacks().firstOrNull { it.packId == packId }
    }

    private fun loadPackFromAssets(path: String): LevelPack {
        val jsonString = context.assets.open(path).bufferedReader().use { it.readText() }
        return json.decodeFromString(jsonString)
    }

    private fun loadDownloadedPacks(): List<LevelPack> {
        val folder = getDownloadedPacksDir()
        if (!folder.exists()) return emptyList()
        return folder.listFiles { file -> file.extension == "json" }?.mapNotNull { file ->
            kotlin.runCatching {
                json.decodeFromString<LevelPack>(file.readText())
            }.getOrNull()
        } ?: emptyList()
    }

    fun downloadRemotePack(url: String, onComplete: () -> Unit) {
        Thread {
            try {
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@use
                    val body = response.body?.string() ?: return@use
                    val pack = json.decodeFromString<LevelPack>(body)
                    val file = File(getDownloadedPacksDir(), "${'$'}{pack.packId}.json")
                    file.writeText(body)
                }
            } finally {
                onComplete()
            }
        }.start()
    }

    private fun getDownloadedPacksDir(): File {
        val folder = File(context.filesDir, "level_packs")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }
}
