package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.model.CourseAttendancePatchRequest
import com.jht.pointy.data.network.ApiService
import com.jht.pointy.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ScanViewModel : ViewModel() {
    private val api = RetrofitClient.instance.create(ApiService::class.java)

    private val _lastScannedUid = MutableStateFlow<String?>(null)
    val lastScannedUid: StateFlow<String?> = _lastScannedUid

    private val _activeCourseId = MutableStateFlow<String?>(null)
    val activeCourseId: StateFlow<String?> = _activeCourseId

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting

    private val _submitMessage = MutableStateFlow<String?>(null)
    val submitMessage: StateFlow<String?> = _submitMessage

    private val _activeCourseName = MutableStateFlow<String?>(null)
    val activeCourseName: StateFlow<String?> = _activeCourseName

    fun onCardScanned(uid: String) {
        _lastScannedUid.value = uid
    }

    fun startAttendanceScan(courseId: String, courseName: String) {
        _activeCourseId.value = courseId
        _activeCourseName.value = courseName
        _submitMessage.value = null
        _lastScannedUid.value = null
    }

    fun stopAttendanceScan() {
        _activeCourseId.value = null
        _activeCourseName.value = null
        _submitMessage.value = null
        _lastScannedUid.value = null
    }

    fun submitAttendanceFromCard(cardSerial: String) {
        val courseId = _activeCourseId.value ?: return

        viewModelScope.launch {
            _isSubmitting.value = true
            _submitMessage.value = null
            try {
                api.patchAttendance(
                    CourseAttendancePatchRequest(
                        courseId = courseId,
                        cardSerial = cardSerial,
                        attendance = "present"
                    )
                )
                _submitMessage.value = "Présence enregistrée"
            } catch (_: HttpException) {
                AuthStateViewModel.notifyHttpError()
                _submitMessage.value = "Session expirée, reconnectez-vous"
            } catch (_: Exception) {
                _submitMessage.value = "Échec de l'enregistrement de la présence"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    fun resetScan() {
        _lastScannedUid.value = null
        _submitMessage.value = null
    }
}