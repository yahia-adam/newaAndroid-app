package com.bythewayapp.ui.viewModels

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
    var bythewayUiSate: BythewayUiSate by mutableStateOf(BythewayUiSate.Loading)
        private set

    private val eventRepository: EventRepository = EventRepository()

    init {
        getEvents(
            classificationName = listOf("concert", "rap")
        )
    }

    private fun getEvents(
        keyword: String? = null,
        startDateTime: String? = null,
        endDateTime: String? = null,
        classificationName: List<String>? = emptyList(),
        size: String? = "100",
    ) {
        viewModelScope.launch {
            try {
                val response = eventRepository.getEvents(
                    keyword = keyword,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    size = size,
                    classificationName = classificationName
                )
                bythewayUiSate = BythewayUiSate.Success("${response.embedded?.events?.get(0)}")
            } catch (e: IOException) {
                //bythewayUiSate = BythewayUiSate.Error(context.getString(R.string.erreur_de_connexion))
            } catch (e: TimeoutException) {
                //bythewayUiSate = BythewayUiSate.Error(context.getString(R.string.error_connection_lent))
            } catch (e: Exception) {
                //bythewayUiSate = BythewayUiSate.Error(context.getString(R.string.error_inconue))
            }
        }
    }
}