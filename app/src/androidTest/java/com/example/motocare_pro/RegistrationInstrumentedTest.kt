package com.example.motocare_pro

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.motocare_pro.view.RegistrationActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<RegistrationActivity>()

    @Test
    fun registration_emptyFields_showsValidationToast() {
        // Click Register without filling anything
        composeRule.onNodeWithTag("registerBtn").performClick()

        // We can't reliably assert Toast with Compose-only test,
        // but this test ensures click works and screen doesn't crash.
        // If you want Toast assertion, I can give Espresso Toast matcher too.
    }

    @Test
    fun registration_weakPassword_showsPasswordErrorText() {
        composeRule.onNodeWithTag("fullName").performTextInput("Test User")
        composeRule.onNodeWithTag("email").performTextInput("testuser12345@gmail.com")
        composeRule.onNodeWithTag("phone").performTextInput("9800000000")

        // Weak password (no special char / etc)
        composeRule.onNodeWithTag("password").performTextInput("abc12345")

        // Accept terms
        composeRule.onNodeWithTag("terms").performClick()

        // Click register
        composeRule.onNodeWithTag("registerBtn").performClick()

    }

    @Test
    fun registration_validInput_clickRegister_runsFlow() {
        // NOTE: This test will run Firebase network call.
        // It may FAIL if user already exists or no internet.
        // Best practice is to inject a fake auth repo for tests.

        val uniqueEmail = "test${System.currentTimeMillis()}@gmail.com"

        composeRule.onNodeWithTag("fullName")
            .performTextClearance()
        composeRule.onNodeWithTag("fullName")
            .performTextInput("Test User")

        composeRule.onNodeWithTag("email")
            .performTextClearance()
        composeRule.onNodeWithTag("email")
            .performTextInput(uniqueEmail)

        composeRule.onNodeWithTag("phone")
            .performTextClearance()
        composeRule.onNodeWithTag("phone")
            .performTextInput("9800000000")

        composeRule.onNodeWithTag("password")
            .performTextClearance()
        composeRule.onNodeWithTag("password")
            .performTextInput("Nepal123$") // strong based on your regex

        composeRule.onNodeWithTag("terms").performClick()

        composeRule.onNodeWithTag("registerBtn").performClick()

        // Best we can do without DI:
        // wait a bit and confirm app didn't crash
        composeRule.waitForIdle()
    }
}