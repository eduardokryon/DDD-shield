package com.dddlock.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dddlock.ui.components.DDDListItem
import com.dddlock.ui.components.DDDSearchBar
import com.dddlock.ui.components.StatusCard
import com.dddlock.ui.theme.StatusActive
import com.dddlock.ui.theme.StatusInactive

/**
 * DDD Shield
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .animateContentSize(animationSpec = tween(300))
    ) {
        // Blocker Status Card with animation
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(300)) +
                    slideInVertically(animationSpec = tween(300)) { it / 2 }
        ) {
            StatusCard(
                title = if (uiState.blockerStatus.isEnabled) "Bloqueador ativo"
                else "Bloqueador inativo",
                value = "${uiState.blockerStatus.blockedCount} DDDs bloqueados",
                icon = Icons.Default.Shield,
                iconTint = if (uiState.blockerStatus.isEnabled) StatusActive
                else StatusInactive
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Button with animation
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(400, delayMillis = 100)) +
                    slideInVertically(animationSpec = tween(400, delayMillis = 100)) { it }
        ) {
            Button(
                onClick = { viewModel.onToggleBlocker() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.blockerStatus.isEnabled)
                        MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (uiState.blockerStatus.isEnabled) "Desativar bloqueio"
                    else "Ativar bloqueio"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search
        DDDSearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = "Buscar DDD, cidade ou estado"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Select All
        val allSelected = uiState.filteredDDDs.isNotEmpty() &&
                uiState.filteredDDDs.all { it.code in uiState.blockedCodes }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = allSelected,
                onCheckedChange = { viewModel.onToggleAll(it) }
            )
            Text(
                text = if (allSelected) "Desmarcar todos" else "Selecionar todos",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // DDD List with item animations
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = uiState.filteredDDDs,
                key = { it.code }
            ) { ddd ->
                DDDListItem(
                    code = ddd.code,
                    city = ddd.city,
                    state = ddd.state,
                    isBlocked = ddd.code in uiState.blockedCodes,
                    onToggle = { viewModel.onToggleDDD(ddd.code) }
                )
            }
        }
    }
}
