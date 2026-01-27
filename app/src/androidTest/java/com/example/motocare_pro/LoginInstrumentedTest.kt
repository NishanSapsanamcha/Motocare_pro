package com.example.motocare_pro

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.motocare_pro.view.DashboardActivity
import com.example.motocare_pro.view.LoginActivity
import com.example.motocare_pro.view.RegistrationActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    private fun waitForIntent(componentName: String, timeoutMillis: Long = 8000) {
        val start = System.currentTimeMillis()
        var lastError: AssertionError? = null

        while (System.currentTimeMillis() - start < timeoutMillis) {
            try {
                Intents.intended(hasComponent(componentName))
                return
            } catch (e: AssertionError) {
                lastError = e
                Thread.sleep(100)
            }
        }
        if (lastError != null) throw lastError
    }

    @Test
    fun registerClick_navigatesToRegistration() {
        composeRule.onNodeWithTag("register")
            .assertIsDisplayed()
            .performClick()

        // Wait until RegistrationActivity intent is fired
        waitForIntent(RegistrationActivity::class.java.name)
    }

    @Test
    fun loginClick_navigatesToDashboard_ifCredentialsValid() {
        composeRule.onNodeWithTag("email")
            .assertExists()
            .performTextClearance()
        composeRule.onNodeWithTag("email")
            .performTextInput("jeson@gmail.com")

        composeRule.onNodeWithTag("password")
            .assertExists()
            .performTextClearance()
        composeRule.onNodeWithTag("password")
            .performTextInput("Nepal123$")

        composeRule.onNodeWithTag("login")
            .assertIsDisplayed()
            .performClick()

        // Firebase login is async + network => give it more time
        waitForIntent(DashboardActivity::class.java.name, timeoutMillis = 15000)
    }
}