package com.dddlock.ui.screens.blocked

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dddlock.ui.components.DDDSearchBar

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
@Composable
fun BlockedNumbersScreen(
    viewModel: BlockedNumbersViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Números",
                style = MaterialTheme.typography.headlineSmall
            )

            IconButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar número"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tabs
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { viewModel.onTabSelected(0) },
                text = {
                    Text("Bloqueados (${uiState.blockedNumbers.size})")
                }
            )
            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { viewModel.onTabSelected(1) },
                text = {
                    Text("Exceções (${uiState.whitelistedNumbers.size})")
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        DDDSearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = "Buscar contato ou número"
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading) {
            Text(
                text = "Carregando contatos...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = uiState.filteredContacts,
                    key = { it.normalizedNumber }
                ) { contact ->
                    val isBlocked = isNumberInList(contact.normalizedNumber, uiState.blockedNumbers)
                    val isWhitelisted = isNumberInList(contact.normalizedNumber, uiState.whitelistedNumbers)

                    ContactListItem(
                        name = contact.name,
                        number = contact.number,
                        isBlocked = isBlocked,
                        isWhitelisted = isWhitelisted,
                        selectedTab = uiState.selectedTab,
                        onToggleBlock = { viewModel.onToggleNumber(contact.normalizedNumber) },
                        onToggleWhitelist = { viewModel.onToggleWhitelist(contact.normalizedNumber) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddNumberDialog(
            title = if (uiState.selectedTab == 0) "Adicionar número bloqueado" else "Adicionar exceção",
            onDismiss = { showAddDialog = false },
            onConfirm = { number ->
                viewModel.onAddNumberManually(number)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ContactListItem(
    name: String,
    number: String,
    isBlocked: Boolean,
    isWhitelisted: Boolean,
    selectedTab: Int,
    onToggleBlock: () -> Unit,
    onToggleWhitelist: () -> Unit
) {
    val isChecked = if (selectedTab == 0) isBlocked else isWhitelisted
    val onToggle = if (selectedTab == 0) onToggleBlock else onToggleWhitelist

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggle() }
        )
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = number,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (selectedTab == 0 && isBlocked) {
            Icon(
                imageVector = Icons.Default.Block,
                contentDescription = "Bloqueado",
                tint = MaterialTheme.colorScheme.error
            )
        } else if (selectedTab == 1 && isWhitelisted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Exceção",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AddNumberDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var number by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            OutlinedTextField(
                value = number,
                onValueChange = { number = it },
                label = { Text("Número de telefone") },
                placeholder = { Text("(88) 99999-9999") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(number) },
                enabled = number.isNotBlank()
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun isNumberInList(number: String, list: Set<String>): Boolean {
    return list.any { item ->
        item == number ||
        (item.length >= 8 && number.length >= 8 &&
         item.takeLast(8) == number.takeLast(8))
    }
}
