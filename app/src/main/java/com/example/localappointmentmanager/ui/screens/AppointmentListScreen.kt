package com.example.localappointmentmanager.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localappointmentmanager.data.local.AppointmentEntity
import com.example.localappointmentmanager.ui.AppointmentUiState

/**
 * AppointmentListScreen displays a list of appointments.
 *
 * @param uiState The current state of the UI (Loading, Success, Empty, Error).
 * @param onAddAppointmentClicked Callback when FAB is clicked.
 * @param onAppointmentClicked Callback when an item is clicked.
 */
@Composable
fun AppointmentListScreen(
    uiState: AppointmentUiState,
    onAddAppointmentClicked: () -> Unit,
    onAppointmentClicked: (Int) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAppointmentClicked) {
                Icon(Icons.Default.Add, contentDescription = "Add Appointment")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header / Date Selector Placeholder
            // For now, simple text, but could be expanded to a horizontal row.
            Text(
                text = "Appointments",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (uiState) {
                is AppointmentUiState.Loading -> {
                    Text("Loading dates...")
                }
                is AppointmentUiState.Empty -> {
                    Text("No appointments yet. Click + to add one.")
                }
                is AppointmentUiState.Error -> {
                    Text("Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                }
                is AppointmentUiState.Success -> {
                    AppointmentList(
                        appointments = uiState.appointments,
                        onItemClick = onAppointmentClicked
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentList(
    appointments: List<AppointmentEntity>,
    onItemClick: (Int) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(appointments) { appointment ->
            AppointmentItem(appointment, onItemClick)
        }
    }
}

@Composable
fun AppointmentItem(
    appointment: AppointmentEntity,
    onClick: (Int) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(appointment.id) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = appointment.clientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${appointment.timeString} • ${appointment.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Date can be shown here or separated by section
        }
    }
}
