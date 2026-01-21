package com.cozyprotect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.cozyprotect.ui.util.GlobalErrorHandler

class GameActivity : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packId = intent.getStringExtra(EXTRA_PACK_ID) ?: "pack_001"
        val levelId = intent.getStringExtra(EXTRA_LEVEL_ID) ?: "001"

        val repository = LevelRepository(this)
        val pack = repository.loadPackById(packId)

        val level = pack?.levels?.firstOrNull { it.id == levelId }
            ?: LevelDefinition(
                id = "001",
                timeToSurvive = 12,
                drawLimit = 1
            )

        val config = AndroidApplicationConfiguration().apply {
            useAccelerometer = false
            useCompass = false
            useImmersiveMode = true
        }

        try {
            initialize(CozyGame(level), config)
        } catch (t: Throwable) {
            GlobalErrorHandler.report(t)
            finish()
        }
    }

    companion object {
        private const val EXTRA_PACK_ID = "extra_pack_id"
        private const val EXTRA_LEVEL_ID = "extra_level_id"

        fun intentFor(context: Context, packId: String, levelId: String): Intent {
            return Intent(context, GameActivity::class.java).apply {
                putExtra(EXTRA_PACK_ID, packId)
                putExtra(EXTRA_LEVEL_ID, levelId)
            }
        }
    }
}
