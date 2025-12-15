package com.example.localappointmentmanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localappointmentmanager.data.local.AppointmentEntity
import com.example.localappointmentmanager.ui.AppointmentViewModel

/**
 * AppointmentDetailsScreen displays the full details of an appointment.
 *
 * @param appointmentId The ID of the appointment to show.
 * @param viewModel The ViewModel to fetch the appointment from.
 * @param onBackClicked Callback to go back.
 * @param onEditClicked Callback to navigate to edit screen.
 * @param onDeleteClicked Callback when the appointment is deleted (should navigate back).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailsScreen(
    appointmentId: Int,
    viewModel: AppointmentViewModel,
    onBackClicked: () -> Unit,
    onEditClicked: (Int) -> Unit,
    onDeleteClicked: () -> Unit
) {
    // Local state to hold the appointment
    var appointment by remember { mutableStateOf<AppointmentEntity?>(null) }

    // Fetch the appointment when the screen launches
    LaunchedEffect(appointmentId) {
        appointment = viewModel.getAppointmentById(appointmentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Edit Button
                    IconButton(onClick = { 
                        appointment?.let { onEditClicked(it.id) } 
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    // Delete Button
                    IconButton(onClick = {
                        appointment?.let { 
                            viewModel.deleteAppointment(it)
                            onDeleteClicked()
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val currentAppointment = appointment
            if (currentAppointment != null) {
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentAppointment.clientName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val dateFormatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        val formattedDate = dateFormatter.format(java.util.Date(currentAppointment.dateTimestamp))
                        LabelValueRow("Date", formattedDate)
                        LabelValueRow("Time", currentAppointment.timeString)
                        LabelValueRow("Status", currentAppointment.status)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Notes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentAppointment.notes.ifBlank { "No notes" },
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        if (currentAppointment.status != "Completed") {
                            androidx.compose.material3.Button(
                                onClick = { 
                                    viewModel.markAppointmentCompleted(currentAppointment)
                                    // We force a refresh or just rely on flow update. 
                                    // Since valid flow is collected in list, but here we fetched once.
                                    // We should re-fetch or observe.
                                    // Ideally details screen should observe flow of specific item, 
                                    // but current implementation fetches once on Launch.
                                    // To update UI immediately, we can trigger a refetch or navigate back.
                                    // Simpler: Navigate back with a result or just popping.
                                    // User said "set as completed", implying staying or moving.
                                    // I'll reload the data here by calling fetch again? 
                                    // Or simplified: Just Pop back to list which observes all.
                                    onBackClicked() 
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Mark as Completed")
                            }
                        }
                    }
                }
            } else {
                Text("Loading or Appointment not found...")
            }
        }
    }
}

@Composable
fun LabelValueRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
