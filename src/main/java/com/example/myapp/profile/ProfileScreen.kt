package com.example.myapp.profile
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapp.BottomNavigationBar
import com.example.myapp.Screen
import com.example.myapp.auth.SharedViewModel
import com.example.myapp.database.AppDatabase
import com.example.myapp.database.UserRepository
import com.example.myapp.settings.SettingsEvent
import com.example.myapp.settings.SettingsViewModel
import com.example.myapp.settings.SettingsViewModelFactory
import com.example.myapp.settings.StringResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    email: String? = null,
    profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            UserRepository(AppDatabase.getDatabase(LocalContext.current).userDao())
        )
    ),
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val currentEmail by sharedViewModel.currentUserEmail.collectAsState()
    val emailToUse = email ?: currentEmail
    val strings = StringResource.strings

    val uiState by profileViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.settingsState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(emailToUse) {
        emailToUse?.let {
            profileViewModel.onEvent(ProfileEvent.LoadUserProfile(it))
        } ?: run {
            navController.navigate(Screen.Auth.route)
        }
        // Load settings
        settingsViewModel.onEvent(SettingsEvent.LoadSettings)
    }

    // State variables for the edit mode
    var isEditMode by remember { mutableStateOf(false) }
    var isSettingsMode by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedGoal by remember { mutableStateOf("") }
    var editedTargetCalories by remember { mutableStateOf("") }
    var editedRestrictions by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isSettingsMode) strings.settings else strings.myProfile) },
                actions = {
                    if (uiState is ProfileUiState.Success) {
                        if (isSettingsMode) {
                            IconButton(onClick = { isSettingsMode = false }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = strings.myProfile)
                            }
                        } else if (isEditMode) {
                            IconButton(onClick = {
                                // Save changes
                                scope.launch {
                                    profileViewModel.onEvent(
                                        ProfileEvent.UpdateProfile(
                                            name = editedName,
                                            goal = editedGoal,
                                            targetCalories = editedTargetCalories.toIntOrNull() ?: 2000,
                                            restrictions = editedRestrictions.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                        )
                                    )
                                }
                                isEditMode = false
                            }) {
                                Icon(Icons.Default.Check, contentDescription = strings.saveChanges)
                            }
                        } else {
                            // Add Settings button
                            IconButton(onClick = { isSettingsMode = true }) {
                                Icon(Icons.Default.Settings, contentDescription = strings.settings)
                            }
                            // Edit Profile button
                            IconButton(onClick = {
                                val profile = (uiState as ProfileUiState.Success).profile
                                editedName = profile.name
                                editedGoal = profile.goal
                                editedTargetCalories = profile.targetCalories.toString()
                                editedRestrictions = profile.restrictions.joinToString(", ")
                                isEditMode = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = strings.editProfile)
                            }
                        }
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController, sharedViewModel) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ProfileUiState.Success -> {
                    val profile = (uiState as ProfileUiState.Success).profile

                    if (isSettingsMode) {
                        // Show Settings UI
                        SettingsContent(
                            settingsState = settingsState,
                            onToggleDarkTheme = { enabled ->
                                settingsViewModel.onEvent(SettingsEvent.ToggleDarkTheme(enabled))
                            },
                            onChangeLanguage = { language ->
                                settingsViewModel.onEvent(SettingsEvent.ChangeLanguage(language))
                            },
                            onChangeUiDensity = { density ->
                                settingsViewModel.onEvent(SettingsEvent.ChangeUiDensity(density))
                            }
                        )
                    } else if (isEditMode) {
                        // Show Edit Profile UI
                        EditProfileContent(
                            name = editedName,
                            onNameChange = { editedName = it },
                            goal = editedGoal,
                            onGoalChange = { editedGoal = it },
                            targetCalories = editedTargetCalories,
                            onTargetCaloriesChange = { editedTargetCalories = it },
                            restrictions = editedRestrictions,
                            onRestrictionsChange = { editedRestrictions = it }
                        )
                    } else {
                        // Show Profile UI
                        ProfileContent(
                            profile = profile,
                            onLogout = {
                                profileViewModel.onEvent(ProfileEvent.Logout)
                                // Clear SharedViewModel when logging out
                                sharedViewModel.clearUser()
                            }
                        )
                    }
                }

                is ProfileUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (uiState as ProfileUiState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            emailToUse?.let {
                                profileViewModel.onEvent(ProfileEvent.LoadUserProfile(it))
                            }
                        }) {
                            Text(strings.tryAgain)
                        }
                    }
                }

                is ProfileUiState.Logout -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsContent(
    settingsState: com.example.myapp.settings.UserSettings,
    onToggleDarkTheme: (Boolean) -> Unit,
    onChangeLanguage: (String) -> Unit,
    onChangeUiDensity: (String) -> Unit
) {
    val strings = StringResource.strings

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Settings Card for Theme
        SettingsCard(
            title = strings.darkTheme,
            icon = Icons.Default.Favorite
        ) {
            Switch(
                checked = settingsState.darkThemeEnabled,
                onCheckedChange = { onToggleDarkTheme(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings Card for Language
        SettingsCard(
            title = strings.language,
            icon = Icons.Default.LocationOn
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(strings.ukrainian)
                    RadioButton(
                        selected = settingsState.language == "ua",
                        onClick = { onChangeLanguage("ua") }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(strings.english)
                    RadioButton(
                        selected = settingsState.language == "en",
                        onClick = { onChangeLanguage("en") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Settings Card for UI Density
        SettingsCard(
            title = strings.uiDensity,
            icon = Icons.Default.Refresh
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(strings.comfortable)
                    RadioButton(
                        selected = settingsState.uiDensity == "comfortable",
                        onClick = { onChangeUiDensity("comfortable") }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(strings.compact)
                    RadioButton(
                        selected = settingsState.uiDensity == "compact",
                        onClick = { onChangeUiDensity("compact") }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun ProfileContent(
    profile: UserProfile,
    onLogout: () -> Unit
) {
    val strings = StringResource.strings

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(profile)

        Spacer(modifier = Modifier.height(24.dp))

        NutritionGoals(profile)

        Spacer(modifier = Modifier.height(16.dp))

        DietaryRestrictions(profile)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(strings.logOut)
        }
    }
}

@Composable
fun EditProfileContent(
    name: String,
    onNameChange: (String) -> Unit,
    goal: String,
    onGoalChange: (String) -> Unit,
    targetCalories: String,
    onTargetCaloriesChange: (String) -> Unit,
    restrictions: String,
    onRestrictionsChange: (String) -> Unit
) {
    val strings = StringResource.strings

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(strings.name) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = goal,
            onValueChange = onGoalChange,
            label = { Text(strings.goal) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = targetCalories,
            onValueChange = onTargetCaloriesChange,
            label = { Text(strings.targetCalories) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = restrictions,
            onValueChange = onRestrictionsChange,
            label = { Text(strings.dietaryRestrictions) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ProfileHeader(profile: UserProfile) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(profile.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(profile.email, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun NutritionGoals(profile: UserProfile) {
    val strings = StringResource.strings

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(strings.myGoals, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = strings.goal)
                Spacer(modifier = Modifier.width(8.dp))
                Text(profile.goal)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = strings.targetCalories)
                Spacer(modifier = Modifier.width(8.dp))
                Text("${profile.targetCalories} ${strings.caloriesPerDay}")
            }
        }
    }
}

@Composable
fun DietaryRestrictions(profile: UserProfile) {
    val strings = StringResource.strings

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(strings.dietaryRestrictions, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (profile.restrictions.isEmpty()) {
                Text(strings.noRestrictions)
            } else {
                profile.restrictions.forEach { restriction ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Restriction")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(restriction)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}