package com.example.myapp.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

data class StringResources(
    // Profile strings
    val myProfile: String = "",
    val editProfile: String = "",
    val saveChanges: String = "",
    val name: String = "",
    val goal: String = "",
    val targetCalories: String = "",
    val dietaryRestrictions: String = "",
    val logOut: String = "",
    val tryAgain: String = "",

    // Settings strings
    val settings: String = "",
    val darkTheme: String = "",
    val language: String = "",
    val ukrainian: String = "",
    val english: String = "",
    val uiDensity: String = "",
    val compact: String = "",
    val comfortable: String = "",
    val caloriesPerDay: String = "",
    val noRestrictions: String = "",
    val myGoals: String = "",
    val enableDarkTheme: String = "",
    val disableDarkTheme: String = "",
    val appLanguage: String = "",
    val interfaceDensity: String = "",
    val settingsSaved: String = "",
    val settingsApplied: String = "",

    // Auth strings
    val auth: String = "",
    val login: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val register: String = "",
    val alreadyHaveAccount: String = "",
    val registration: String = "",
    val back: String = "",
    val registrationError: String = "",
    val dontHaveAccount: String = "",

    // Main screen strings
    val myDiet: String = "",
    val statistics: String = "",
    val addMeal: String = "",
    val meals: String = "",
    val dailyBalance: String = "",
    val goal_: String = "",
    val previousDay: String = "",
    val nextDay: String = "",

    // Additional Main screen strings
    val nutritionStatistics: String = "",
    val home: String = "",
    val mealPlan: String = "",
    val profile: String = "",
    val kcal: String = "",
    val targetKcal: String = "",

    // MealPlan screen strings
    val addNewMeal: String = "",
    val mealName: String = "",
    val calories: String = "",
    val ingredients: String = "",
    val ingredientsSeparator: String = "",
    val ok: String = "",
    val cancel: String = "",
    val save: String = "",
    val delete: String = "",
    val edit: String = ""

)

// Define Ukrainian strings
val UkrainianStrings = StringResources(
    // Profile strings
    myProfile = "Мій профіль",
    editProfile = "Редагувати",
    saveChanges = "Зберегти",
    name = "Ім'я",
    goal = "Ціль",
    targetCalories = "Цільові калорії (ккал/день)",
    dietaryRestrictions = "Харчові обмеження (розділені комами)",
    logOut = "Вийти з акаунту",
    tryAgain = "Спробувати ще раз",

    // Settings strings
    settings = "Налаштування",
    darkTheme = "Темна тема",
    language = "Мова",
    ukrainian = "Українська",
    english = "Англійська",
    uiDensity = "Щільність інтерфейсу",
    compact = "Компактна",
    comfortable = "Комфортна",
    caloriesPerDay = "ккал/день",
    noRestrictions = "Обмеження не вказані",
    myGoals = "Мої цілі",
    enableDarkTheme = "Увімкнути темну тему",
    disableDarkTheme = "Вимкнути темну тему",
    appLanguage = "Мова додатку",
    interfaceDensity = "Щільність інтерфейсу",
    settingsSaved = "Налаштування збережено",
    settingsApplied = "Налаштування застосовано",

    // Auth strings
    auth = "Авторизація",
    login = "Увійти",
    email = "Email",
    password = "Пароль",
    confirmPassword = "Підтвердіть пароль",
    register = "Зареєструватися",
    alreadyHaveAccount = "Вже маєте акаунт? Увійти",
    registration = "Реєстрація",
    back = "Назад",
    registrationError = "Помилка реєстрації",
    dontHaveAccount = "Не маєте акаунту? Реєстрація",

    // Main screen strings
    myDiet = "Мій раціон",
    statistics = "Статистика",
    addMeal = "Додати прийом їжі",
    meals = "Прийоми їжі:",
    dailyBalance = "Денний баланс:",
    goal_ = "Ціль:",
    previousDay = "Попередній день",
    nextDay = "Наступний день",

    // Additional Main screen strings
    nutritionStatistics = "Статистика харчування",
    home = "Головна",
    mealPlan = "Харчування",
    profile = "Профіль",
    kcal = "ккал",
    targetKcal = "Ціль: %d ккал",

    // MealPlan screen strings
    addNewMeal = "Додати прийом їжі",
    mealName = "Назва прийому їжі",
    calories = "Калорії",
    ingredients = "Інгредієнти",
    ingredientsSeparator = "Інгредієнти (через кому)",
    ok = "OK",
    cancel = "Скасувати",
    save = "Зберегти",
    delete = "Видалити",
    edit = "Редагувати"
)

// Define English strings
val EnglishStrings = StringResources(
    // Profile strings
    myProfile = "My Profile",
    editProfile = "Edit",
    saveChanges = "Save",
    name = "Name",
    goal = "Goal",
    targetCalories = "Target Calories (kcal/day)",
    dietaryRestrictions = "Dietary Restrictions (comma separated)",
    logOut = "Log Out",
    tryAgain = "Try Again",

    // Settings strings
    settings = "Settings",
    darkTheme = "Dark Theme",
    language = "Language",
    ukrainian = "Ukrainian",
    english = "English",
    uiDensity = "UI Density",
    compact = "Compact",
    comfortable = "Comfortable",
    caloriesPerDay = "kcal/day",
    noRestrictions = "No restrictions specified",
    myGoals = "My Goals",
    enableDarkTheme = "Enable Dark Theme",
    disableDarkTheme = "Disable Dark Theme",
    appLanguage = "App Language",
    interfaceDensity = "Interface Density",
    settingsSaved = "Settings Saved",
    settingsApplied = "Settings Applied",

    // Auth strings
    auth = "Authentication",
    login = "Login",
    email = "Email",
    password = "Password",
    confirmPassword = "Confirm Password",
    register = "Register",
    alreadyHaveAccount = "Already have an account? Login",
    registration = "Registration",
    back = "Back",
    registrationError = "Registration Error",
    dontHaveAccount = "Don't have an account? Register",

    // Main screen strings
    myDiet = "My Diet",
    statistics = "Statistics",
    addMeal = "Add Meal",
    meals = "Meals:",
    dailyBalance = "Daily Balance:",
    goal_ = "Goal:",
    previousDay = "Previous Day",
    nextDay = "Next Day",

    // Additional Main screen strings
    nutritionStatistics = "Nutrition Statistics",
    home = "Home",
    mealPlan = "Meal Plan",
    profile = "Profile",
    kcal = "kcal",
    targetKcal = "Goal: %d kcal",

    // MealPlan screen strings
    addNewMeal = "Add New Meal",
    mealName = "Meal Name",
    calories = "Calories",
    ingredients = "Ingredients",
    ingredientsSeparator = "Ingredients (comma separated)",
    ok = "OK",
    cancel = "Cancel",
    save = "Save",
    delete = "Delete",
    edit = "Edit"
)

// Create a CompositionLocal to hold string resources
val LocalStringResources = staticCompositionLocalOf { UkrainianStrings }

// Create a helper function to access string resources
object StringResource {
    val strings: StringResources
        @Composable
        @ReadOnlyComposable
        get() = LocalStringResources.current
}