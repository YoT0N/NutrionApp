package com.example.myapp.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.example.myapp.database.User

@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var authRepository: AuthRepository
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mock()
        sharedViewModel = mock()
        viewModel = AuthViewModel(authRepository, sharedViewModel)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when login succeeds, uiState should be Success`() = runTest {
        // Arrange
        val testEmail = "test@example.com"
        val testPassword = "password123"
        val userId = 1L
        val user = User(id = userId, email = testEmail, passwordHash = "hashedPassword")

        whenever(authRepository.login(testEmail, testPassword)).thenReturn(user)

        // Act
        viewModel.onEvent(AuthEvent.Login(testEmail, testPassword))
        testScheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.first()
        assert(state is AuthUiState.Success)
        assert((state as AuthUiState.Success).email == testEmail)
        verify(sharedViewModel).setUser(testEmail, userId)
    }

    @Test
    fun `when login fails, uiState should be Error`() = runTest {
        // Arrange
        val testEmail = "invalid@example.com"
        val testPassword = "wrongPassword"

        whenever(authRepository.login(testEmail, testPassword)).thenReturn(null)

        // Act
        viewModel.onEvent(AuthEvent.Login(testEmail, testPassword))
        testScheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.first()
        assert(state is AuthUiState.Error)
        assert((state as AuthUiState.Error).message == "Невірний email або пароль")
    }

    @Test
    fun `when login throws exception, uiState should be Error with exception message`() = runTest {
        // Arrange
        val testEmail = "test@example.com"
        val testPassword = "password123"
        val exceptionMessage = "Connection error"

        whenever(authRepository.login(testEmail, testPassword)).thenThrow(RuntimeException(exceptionMessage))

        // Act
        viewModel.onEvent(AuthEvent.Login(testEmail, testPassword))
        testScheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.first()
        assert(state is AuthUiState.Error)
        assert((state as AuthUiState.Error).message == "Помилка авторизації: $exceptionMessage")
    }

    @Test
    fun `when navigateToRegistration event is triggered, uiState should be NavigateToRegistration`() = runTest {
        // Act
        viewModel.onEvent(AuthEvent.NavigateToRegistration)

        // Assert
        val state = viewModel.uiState.first()
        assert(state is AuthUiState.NavigateToRegistration)
    }

    @Test
    fun `when logout event is triggered, uiState should be Idle and user cleared`() = runTest {
        // Act
        viewModel.onEvent(AuthEvent.Logout)
        testScheduler.advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.first()
        assert(state is AuthUiState.Idle)
        verify(sharedViewModel).clearUser()
    }
}