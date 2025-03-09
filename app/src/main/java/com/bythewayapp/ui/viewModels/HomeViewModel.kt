package com.bythewayapp.ui.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bythewayapp.data.EventRepository
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.TimeoutException

sealed interface  BythewayUiSate {
    data class Success(val events: String): BythewayUiSate
    data class Error(val message: String) : BythewayUiSate
    data object Loading: BythewayUiSate
}

class HomeViewModel : ViewModel() {
    companion object {
        const val DEFAULT_SIZE = "200"
        const val DEFAULT_CITY = "Paris"
    }

    var bythewayUiSate: BythewayUiSate

    by mutableStateOf(BythewayUiSate.Loading)
        private set

    private val eventRepository: EventRepository = EventRepository()

    init {
        getSuggestion("concert")
        getEvents(size = DEFAULT_SIZE,
            city = DEFAULT_CITY,
            startDateTime = "",
            endDateTime = "",
            )
    }

    private fun getSuggestion(
        keyword: String
    ) {
        viewModelScope.launch {
            try {
                val response = eventRepository.getSuggestion(
                    keyword = keyword,
                )
                Log.d("SUGGESTION", "$response")
            } catch (e: IOException) {
                bythewayUiSate = BythewayUiSate.Error("context.getString(R.string.erreur_de_connexion) $e")
            } catch (e: TimeoutException) {
                bythewayUiSate = BythewayUiSate.Error("context.getString(R.string.error_connection_lent) $e")
            } catch (e: Exception) {
                bythewayUiSate = BythewayUiSate.Error("context.getString(R.string.error_inconue) $e")
            }
        }
    }

    private fun getEvents(
        keyword: String? = null,
        id: List<String>? = null,
        startDateTime: String? = null,
        endDateTime: String? = null,
        classificationName: List<String>? = null,
        classificationId: List<String>? = null,
        size: String? = null,
        city: String? = null,
    ) {
        viewModelScope.launch {
            try {
                val response = eventRepository.getEvents(
                    keyword = keyword,
                    id = id,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    size = size,
                    classificationName = classificationName,
                    classificationId = classificationId,
                    city = city
                )
                bythewayUiSate = BythewayUiSate.Success("${response.embedded?.events?.get(0)?.getTags()}")

            } catch (e: IOException) {
                bythewayUiSate = BythewayUiSate.Error("context.getString(R.string.erreur_de_connexion) $e")
            } catch (e: TimeoutException) {
                bythewayUiSate = BythewayUiSate.Error("context.getString(R.string.error_connection_lent) $e")
            } catch (e: Exception) {
                bythewayUiSate = BythewayUiSate.Error("context.getString(R.string.error_inconue) $e")
            }
        }
    }
}