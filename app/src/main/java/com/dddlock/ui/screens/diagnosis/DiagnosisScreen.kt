package com.dddlock.ui.screens.diagnosis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dddlock.ui.components.StatusCard
import com.dddlock.ui.theme.StatusActive
import com.dddlock.ui.theme.StatusInactive
import com.dddlock.ui.theme.StatusWarning

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
@Composable
fun DiagnosisScreen(
    viewModel: DiagnosisViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .animateContentSize(animationSpec = tween(300)),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Diagnóstico do Sistema",
            style = MaterialTheme.typography.headlineSmall
        )

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300)) +
                    slideInVertically(animationSpec = tween(300)) { it / 2 }
        ) {
            StatusCard(
                title = "Serviço de bloqueio",
                value = if (uiState.isServiceActive) "Ativo" else "Inativo",
                icon = Icons.Default.Phone,
                iconTint = if (uiState.isServiceActive) StatusActive else StatusInactive
            )
        }

        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300, delayMillis = 50))
        ) {
            StatusCard(
                title = "Permissão concedida",
                value = if (uiState.isPermissionGranted) "Sim" else "Não",
                icon = if (uiState.isPermissionGranted) Icons.Default.CheckCircle else Icons.Default.Error,
                iconTint = if (uiState.isPermissionGranted) StatusActive else StatusInactive
            )
        }

        StatusCard(
            title = "Função de bloqueio",
            value = if (uiState.isRoleGranted) "Concedida" else "Não concedida",
            icon = Icons.Default.VerifiedUser,
            iconTint = if (uiState.isRoleGranted) StatusActive else StatusInactive
        )

        StatusCard(
            title = "DDDs bloqueados",
            value = uiState.blockedDDDCount.toString(),
            icon = Icons.Default.Block,
            iconTint = if (uiState.blockedDDDCount > 0) StatusActive else StatusInactive
        )

        StatusCard(
            title = "Versão do app",
            value = uiState.appVersion,
            icon = Icons.Default.Info
        )

        StatusCard(
            title = "Package",
            value = uiState.packageName,
            icon = Icons.Default.Info
        )

        if (uiState.lastUpdate.isNotEmpty()) {
            StatusCard(
                title = "Última atualização",
                value = uiState.lastUpdate,
                icon = Icons.Default.CalendarMonth,
                iconTint = StatusWarning
            )
        }

        Button(
            onClick = { viewModel.refresh() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text(
                if (uiState.isLoading) "Atualizando..." else "Atualizar diagnóstico"
            )
        }
    }
}
