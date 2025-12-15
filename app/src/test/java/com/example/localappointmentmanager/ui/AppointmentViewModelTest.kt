package com.example.localappointmentmanager.ui

import com.example.localappointmentmanager.data.local.AppointmentEntity
import com.example.localappointmentmanager.data.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * AppointmentViewModelTest verifies the logic in the ViewModel.
 * logic:
 * - Loading data on init.
 * - Validation logic in input methods.
 * - Updating UI state based on repository emissions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppointmentViewModelTest {

    private lateinit var viewModel: AppointmentViewModel
    private lateinit var fakeRepository: FakeAppointmentRepository
    private val testDispatcher = StandardTestDispatcher()

    /**
     * Set up the Main dispatcher to verify viewModelScope.launch.
     */
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeAppointmentRepository()
        // We delay init of viewModel so we can set up the fake repo data if needed first,
        // or just init it here.
    }

    /**
     * Clean up dispatcher.
     */
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initially_loads_data() = runTest(testDispatcher) {
        // Given
        val testEntity = AppointmentEntity(1, "Alice", 100L, "10:00")
        fakeRepository.emit(listOf(testEntity))

        // When
        viewModel = AppointmentViewModel(fakeRepository)
        
        // Advance coroutines to execute init block
        testScheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is AppointmentUiState.Success)
        assertEquals(1, (state as AppointmentUiState.Success).appointments.size)
        assertEquals("Alice", state.appointments[0].clientName)
    }

    @Test
    fun uiState_shows_empty_when_no_data() = runTest(testDispatcher) {
        // Given
        fakeRepository.emit(emptyList())

        // When
        viewModel = AppointmentViewModel(fakeRepository)
        testScheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.uiState.value is AppointmentUiState.Empty)
    }

    @Test
    fun addAppointment_validates_empty_name() = runTest(testDispatcher) {
        // Given
        fakeRepository.emit(emptyList())
        viewModel = AppointmentViewModel(fakeRepository)
        testScheduler.advanceUntilIdle()

        // When
        viewModel.addAppointment("", 123L, "10:00", "notes")
        testScheduler.advanceUntilIdle()

        // Then
        // Repository should not have received any insert
        assertEquals(0, fakeRepository.insertedItems.size)
    }

    @Test
    fun addAppointment_calls_repository_on_valid_input() = runTest(testDispatcher) {
         // Given
        fakeRepository.emit(emptyList())
        viewModel = AppointmentViewModel(fakeRepository)
        testScheduler.advanceUntilIdle()

        // When
        viewModel.addAppointment("Bob", 123L, "10:00", "notes")
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(1, fakeRepository.insertedItems.size)
        assertEquals("Bob", fakeRepository.insertedItems[0].clientName)
    }

    // --- Fake Repository ---
    class FakeAppointmentRepository : AppointmentRepository {
        
        private val flow = MutableStateFlow<List<AppointmentEntity>>(emptyList())
        val insertedItems = mutableListOf<AppointmentEntity>()

        suspend fun emit(list: List<AppointmentEntity>) {
            flow.emit(list)
        }

        override fun getAllAppointments(): Flow<List<AppointmentEntity>> {
            return flow
        }

        override suspend fun getAppointmentById(id: Int): AppointmentEntity? {
            return flow.value.find { it.id == id }
        }

        override suspend fun insertAppointment(appointment: AppointmentEntity) {
            insertedItems.add(appointment)
            // In a real DB, this would update the Flow. We can simulate that:
            val newList = flow.value.toMutableList()
            newList.add(appointment)
            flow.emit(newList)
        }

        override suspend fun deleteAppointment(appointment: AppointmentEntity) {
            val newList = flow.value.toMutableList()
            newList.remove(appointment)
            flow.emit(newList)
        }

        override suspend fun updateAppointment(appointment: AppointmentEntity) {
             // Not used in this test
        }
    }
}
