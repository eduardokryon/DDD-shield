package com.dddlock.ui.screens.diagnosis

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dddlock.domain.usecase.GetBlockedDDDsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
data class DiagnosisUiState(
    val isServiceActive: Boolean = false,
    val isPermissionGranted: Boolean = false,
    val isContactsPermissionGranted: Boolean = false,
    val isRoleGranted: Boolean = false,
    val blockedDDDCount: Int = 0,
    val appVersion: String = "",
    val packageName: String = "",
    val lastUpdate: String = "",
    val isLoading: Boolean = false
)

class DiagnosisViewModel(
    application: Application,
    private val getBlockedDDDs: GetBlockedDDDsUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DiagnosisUiState())
    val uiState: StateFlow<DiagnosisUiState> = _uiState.asStateFlow()

    init {
        refresh()

        viewModelScope.launch {
            getBlockedDDDs().collect { blockedCodes ->
                _uiState.value = _uiState.value.copy(
                    blockedDDDCount = blockedCodes.size
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val appInfo = withContext(Dispatchers.IO) {
                val context = getApplication<Application>()
                val packageInfo = context.packageManager.getPackageInfo(
                    context.packageName, 0
                )
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                val hasPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED

                val hasContactsPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED

                val serviceActive = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val roleManager = context.getSystemService("role") as? android.app.role.RoleManager
                        roleManager?.isRoleHeld(android.app.role.RoleManager.ROLE_CALL_SCREENING) ?: false
                    } else false
                } catch (_: Exception) {
                    false
                }

                AppInfo(
                    serviceActive = serviceActive,
                    permissionGranted = hasPermission,
                    contactsPermissionGranted = hasContactsPermission,
                    roleGranted = hasPermission,
                    version = packageInfo.versionName ?: "1.0.0",
                    packageName = context.packageName,
                    lastUpdate = dateFormat.format(Date(packageInfo.lastUpdateTime))
                )
            }

            _uiState.value = _uiState.value.copy(
                isServiceActive = appInfo.serviceActive,
                isPermissionGranted = appInfo.permissionGranted,
                isContactsPermissionGranted = appInfo.contactsPermissionGranted,
                isRoleGranted = appInfo.roleGranted,
                appVersion = appInfo.version,
                packageName = appInfo.packageName,
                lastUpdate = appInfo.lastUpdate,
                isLoading = false
            )
        }
    }

    private data class AppInfo(
        val serviceActive: Boolean,
        val permissionGranted: Boolean,
        val contactsPermissionGranted: Boolean,
        val roleGranted: Boolean,
        val version: String,
        val packageName: String,
        val lastUpdate: String
    )
}
