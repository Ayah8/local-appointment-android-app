package com.example.localappointmentmanager.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.localappointmentmanager.ui.AppointmentViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * AddEditAppointmentScreen allows users to create or edit an appointment.
 * 
 * @param appointmentId The ID of the appointment to edit, or -1 for new.
 * @param viewModel The ViewModel to handle data operations.
 * @param onBackClicked Callback to navigate back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAppointmentScreen(
    appointmentId: Int = -1,
    viewModel: AppointmentViewModel,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    
    // Form State
    var name by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(false) }

    // Date/Time State
    val calendar = remember { Calendar.getInstance() }
    var dateTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedTime by remember { mutableStateOf("10:00") }

    // Fetch existing data if editing
    LaunchedEffect(appointmentId) {
        if (appointmentId != -1) {
            val appointment = viewModel.getAppointmentById(appointmentId)
            if (appointment != null) {
                name = appointment.clientName
                notes = appointment.notes
                dateTimestamp = appointment.dateTimestamp
                selectedTime = appointment.timeString
                // Sync calendar
                calendar.timeInMillis = appointment.dateTimestamp
            }
        }
    }

    // Helper formatter
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayDate = dateFormatter.format(Date(dateTimestamp))

    // Dialogs
    // We recreate dialogs when the data changes so they open with the correct current value
    val datePickerDialog = remember(dateTimestamp) {
        calendar.timeInMillis = dateTimestamp
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                dateTimestamp = calendar.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val timePickerDialog = remember(selectedTime) {
        val parts = selectedTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 10
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
        
        TimePickerDialog(
            context,
            { _, hourOfDay, m ->
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, m)
            },
            hour,
            minute,
            true // 24 hour
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (appointmentId == -1) "New Appointment" else "Edit Appointment") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Client Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    isNameError = false
                },
                label = { Text("Client Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = isNameError,
                supportingText = { if (isNameError) Text("Name is required") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date Field
            OutlinedTextField(
                value = displayDate,
                onValueChange = { },
                label = { Text("Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                enabled = false, 
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time Field
            OutlinedTextField(
                value = selectedTime,
                onValueChange = { },
                label = { Text("Time") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { timePickerDialog.show() },
                enabled = false,
                trailingIcon = {
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Select Time")
                    }
                },
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledIndicatorColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Notes Field
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        isNameError = true
                    } else {
                        if (appointmentId == -1) {
                            viewModel.addAppointment(name, dateTimestamp, selectedTime, notes)
                        } else {
                            viewModel.updateAppointment(appointmentId, name, dateTimestamp, selectedTime, notes)
                        }
                        onBackClicked()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (appointmentId == -1) "Save Appointment" else "Update Appointment")
            }
        }
    }
}
