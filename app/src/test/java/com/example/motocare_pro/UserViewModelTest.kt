package com.example.motocare_pro

import com.example.motocare_pro.repository.UserRepo
import com.example.motocare_pro.viewmodel.userViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify


class UserViewModelTest {

    @Test
    fun login_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = userViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Login success")
            null
        }.`when`(repo).login(eq("testing@gmail.com"), eq("test123"), any())

        var successResult = false
        var messageResult = ""

        viewModel.login("testing@gmail.com", "test123") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Login success", messageResult)

        verify(repo).login(eq("testing@gmail.com"), eq("test123"), any())
    }
}