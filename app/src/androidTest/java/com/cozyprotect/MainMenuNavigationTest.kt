// MainMenuNavigationTest.kt  (fixed imports)
package com.cozyprotect

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun tappingStartAdventureShowsStageSelect() {
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodesWithText("Start Adventure").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Start Adventure")
            .assertIsDisplayed()
            .performClick()

        composeRule.onNodeWithText("Stage Select")
            .assertIsDisplayed()
    }
}
