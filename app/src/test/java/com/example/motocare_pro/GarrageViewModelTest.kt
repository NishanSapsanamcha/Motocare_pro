package com.example.motocare_pro

import android.content.Context
import android.net.Uri
import com.example.motocare_pro.model.GarrageModel
import com.example.motocare_pro.repository.GarrageRepo
import com.example.motocare_pro.viewmodel.GarrageViewModel
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.*

class GarrageViewModelTest {

    @Test
    fun addGarrage_success_test() {
        val repo = mock<GarrageRepo>()
        val viewModel = GarrageViewModel(repo)

        val model = GarrageModel(
            garrageId = "1",
            name = "Test Garage",
            location = "Kathmandu",
            contact = "9800000000",
            image = "img"
        )

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Added")
            null
        }.`when`(repo).addGarrage(eq(model), any())

        var successResult = false
        var messageResult = ""

        viewModel.addGarrage(model) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Added", messageResult)
        verify(repo).addGarrage(eq(model), any())
    }

    @Test
    fun deleteGarrage_success_test() {
        val repo = mock<GarrageRepo>()
        val viewModel = GarrageViewModel(repo)

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Deleted")
            null
        }.`when`(repo).deleteGarrage(eq("1"), any())

        var successResult = false
        var messageResult = ""

        viewModel.deleteGarrage("1") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Deleted", messageResult)
        verify(repo).deleteGarrage(eq("1"), any())
    }

    @Test
    fun editGarrage_success_test() {
        val repo = mock<GarrageRepo>()
        val viewModel = GarrageViewModel(repo)

        val updated = GarrageModel(
            garrageId = "1",
            name = "Updated Garage",
            location = "Lalitpur",
            contact = "9811111111",
            image = "img2"
        )

        doAnswer {
            val callback = it.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Updated")
            null
        }.`when`(repo).editGarrage(eq("1"), eq(updated), any())

        var successResult = false
        var messageResult = ""

        viewModel.editGarrage("1", updated) { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Updated", messageResult)
        verify(repo).editGarrage(eq("1"), eq(updated), any())
    }

    @Test
    fun uploadImage_success_test() {
        val repo = mock<GarrageRepo>()
        val viewModel = GarrageViewModel(repo)

        val context = mock<Context>()
        val uri = mock<Uri>()

        doAnswer {
            val callback = it.getArgument<(String?) -> Unit>(2)
            callback("https://img.url/test.png")
            null
        }.`when`(repo).uploadImage(eq(context), eq(uri), any())

        var urlResult: String? = null

        viewModel.uploadImage(context, uri) { url ->
            urlResult = url
        }

        assertEquals("https://img.url/test.png", urlResult)
        verify(repo).uploadImage(eq(context), eq(uri), any())
    }
}