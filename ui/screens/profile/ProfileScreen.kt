package com.example.lab5.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lab5.R
import com.example.lab5.ui.components.ProfileHeader
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,  // ViewModel передається явно
    onBackClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(ProfileEvent.LoadUserData)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.handleEvent(ProfileEvent.Logout)
                        onLogout()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = stringResource(R.string.logout)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Profile header with avatar
            ProfileHeader(
                name = state.user?.name ?: "",
                email = state.user?.email ?: "",
                modifier = Modifier.padding(16.dp)
            )

            // Personal info section
            SectionTitle(text = stringResource(R.string.personal_info))
            ProfileTextField(
                label = stringResource(R.string.name),
                value = state.name,
                onValueChange = { viewModel.handleEvent(ProfileEvent.NameChanged(it)) },
                isError = !state.isNameValid
            )
            ProfileTextField(
                label = stringResource(R.string.email),
                value = state.email,
                onValueChange = { viewModel.handleEvent(ProfileEvent.EmailChanged(it)) },
                isError = !state.isEmailValid
            )
            ProfileDateField(
                label = stringResource(R.string.birth_date),
                value = state.birthDate?.let { dateFormatter.format(Date(it)) } ?: "",
                onClick = { viewModel.handleEvent(ProfileEvent.ShowDatePicker) }
            )
            ProfileTextField(
                label = stringResource(R.string.height),
                value = state.height,
                onValueChange = { viewModel.handleEvent(ProfileEvent.HeightChanged(it.toIntOrNull() ?: 0)) },
                isError = !state.isHeightValid,
                suffix = " cm"
            )
            ProfileTextField(
                label = stringResource(R.string.weight),
                value = state.weight,
                onValueChange = { viewModel.handleEvent(ProfileEvent.WeightChanged(it.toFloatOrNull() ?: 0f)) },
                isError = !state.isWeightValid,
                suffix = " kg"
            )

            // Save button
            Button(
                onClick = { viewModel.handleEvent(ProfileEvent.SaveProfile) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = state.isProfileValid()
            ) {
                Text(stringResource(R.string.save_changes))
            }

            // Change password section
            SectionTitle(text = stringResource(R.string.change_password))
            Button(
                onClick = { viewModel.handleEvent(ProfileEvent.ShowPasswordDialog) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(stringResource(R.string.change_password))
            }

            // Danger zone section
            SectionTitle(text = stringResource(R.string.danger_zone))
            Button(
                onClick = { viewModel.handleEvent(ProfileEvent.ShowDeleteConfirmation) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(R.string.delete_account))
            }

            // Loading and error states
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    // Date picker dialog
    if (state.showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { viewModel.handleEvent(ProfileEvent.DismissDatePicker) },
            onDateSelected = { date ->
                viewModel.handleEvent(ProfileEvent.BirthDateChanged(date.time))
            },
            initialDate = state.birthDate?.let { Date(it) } ?: Calendar.getInstance().apply {
                add(Calendar.YEAR, -18)
            }.time
        )
    }

    // Change password dialog
    if (state.showPasswordDialog) {
        ChangePasswordDialog(
            currentPassword = state.currentPassword,
            newPassword = state.newPassword,
            confirmPassword = state.confirmPassword,
            onCurrentPasswordChange = { viewModel.handleEvent(ProfileEvent.CurrentPasswordChanged(it)) },
            onNewPasswordChange = { viewModel.handleEvent(ProfileEvent.NewPasswordChanged(it)) },
            onConfirmPasswordChange = { viewModel.handleEvent(ProfileEvent.ConfirmPasswordChanged(it)) },
            onDismiss = { viewModel.handleEvent(ProfileEvent.DismissPasswordDialog) },
            onConfirm = { viewModel.handleEvent(ProfileEvent.ChangePassword) },
            isCurrentPasswordValid = state.isCurrentPasswordValid,
            isNewPasswordValid = state.isNewPasswordValid,
            isConfirmPasswordValid = state.isConfirmPasswordValid
        )
    }

    // Delete account confirmation dialog
    if (state.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.handleEvent(ProfileEvent.DismissDeleteConfirmation) },
            title = { Text(stringResource(R.string.delete_account_title)) },
            text = { Text(stringResource(R.string.delete_account_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.handleEvent(ProfileEvent.DeleteAccount)
                        onLogout()
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.handleEvent(ProfileEvent.DismissDeleteConfirmation) }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    suffix: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        suffix = { Text(suffix) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ProfileDateField(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = if (value.isNotEmpty()) value else label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (value.isNotEmpty()) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit,
    initialDate: Date = Date()
) {
    // Implement your date picker dialog here
    // For API 24+ compatibility, consider using DatePicker from Material Components
}

@Composable
private fun ChangePasswordDialog(
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isCurrentPasswordValid: Boolean,
    isNewPasswordValid: Boolean,
    isConfirmPasswordValid: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.change_password)) },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = { Text(stringResource(R.string.current_password)) },
                    isError = !isCurrentPasswordValid,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    label = { Text(stringResource(R.string.new_password)) },
                    isError = !isNewPasswordValid,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = { Text(stringResource(R.string.confirm_password)) },
                    isError = !isConfirmPasswordValid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = isCurrentPasswordValid && isNewPasswordValid && isConfirmPasswordValid
            ) {
                Text(stringResource(R.string.change))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}