package com.example.myapp.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapp.database.User
import com.example.myapp.database.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: ProfileViewModel

    private val testUser = User(
        id = 1,
        email = "test@example.com",
        passwordHash = "hash123",
        name = "Test User",
        goal = "Lose weight",
        targetCalories = 1800,
        restrictions = listOf("Gluten", "Dairy")
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `loadUserProfile - successful case`() = testDispatcher.runBlockingTest {
        // Given
        val email = "test@example.com"
        `when`(userRepository.getUserByEmail(email)).thenReturn(testUser)

        // When
        viewModel.onEvent(ProfileEvent.LoadUserProfile(email))

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state is ProfileUiState.Success)
        val profile = (state as ProfileUiState.Success).profile
        assertEquals(testUser.name, profile.name)
        assertEquals(testUser.email, profile.email)
        assertEquals(testUser.goal, profile.goal)
        assertEquals(testUser.targetCalories, profile.targetCalories)
        assertEquals(testUser.restrictions, profile.restrictions)
    }

    @Test
    fun `loadUserProfile - user not found`() = testDispatcher.runBlockingTest {
        // Given
        val email = "nonexistent@example.com"
        `when`(userRepository.getUserByEmail(email)).thenReturn(null)

        // When
        viewModel.onEvent(ProfileEvent.LoadUserProfile(email))

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state is ProfileUiState.Error)
        assertEquals("Користувача не знайдено", (state as ProfileUiState.Error).message)
    }

    @Test
    fun `loadUserProfile - exception thrown`() = testDispatcher.runBlockingTest {
        // Given
        val email = "test@example.com"
        val errorMessage = "Database error"
        `when`(userRepository.getUserByEmail(email)).thenThrow(RuntimeException(errorMessage))

        // When
        viewModel.onEvent(ProfileEvent.LoadUserProfile(email))

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state is ProfileUiState.Error)
        assertTrue((state as ProfileUiState.Error).message.contains(errorMessage))
    }

    @Test
    fun `updateProfile - successful case`() = testDispatcher.runBlockingTest {
        // Given - load user first
        val email = "test@example.com"
        `when`(userRepository.getUserByEmail(email)).thenReturn(testUser)
        viewModel.onEvent(ProfileEvent.LoadUserProfile(email))

        // When - update the profile
        val newName = "Updated Name"
        val newGoal = "Gain muscle"
        val newCalories = 2200
        val newRestrictions = listOf("Peanuts")

        viewModel.onEvent(ProfileEvent.UpdateProfile(
            name = newName,
            goal = newGoal,
            targetCalories = newCalories,
            restrictions = newRestrictions
        ))

        // Then
        val updatedUser = testUser.copy(
            name = newName,
            goal = newGoal,
            targetCalories = newCalories,
            restrictions = newRestrictions
        )
        verify(userRepository).updateUser(updatedUser)

        val state = viewModel.uiState.first()
        assertTrue(state is ProfileUiState.Success)
        val profile = (state as ProfileUiState.Success).profile
        assertEquals(newName, profile.name)
        assertEquals(newGoal, profile.goal)
        assertEquals(newCalories, profile.targetCalories)
        assertEquals(newRestrictions, profile.restrictions)
    }

    @Test
    fun `logout - changes state to Logout`() = testDispatcher.runBlockingTest {
        // When
        viewModel.onEvent(ProfileEvent.Logout)

        // Then
        val state = viewModel.uiState.first()
        assertTrue(state is ProfileUiState.Logout)
    }
}