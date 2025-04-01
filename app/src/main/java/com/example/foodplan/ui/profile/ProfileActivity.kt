package com.example.foodplan.ui.profile

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodplan.R
import com.example.foodplan.model.ActivityLevel
import com.example.foodplan.model.Gender
import com.example.foodplan.model.UserProfile
import com.example.foodplan.repository.UserProfileRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var nameEditText: TextInputEditText
    private lateinit var ageEditText: TextInputEditText
    private lateinit var weightEditText: TextInputEditText
    private lateinit var heightEditText: TextInputEditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var activityLevelRadioGroup: RadioGroup
    private lateinit var caloriesGoalEditText: TextInputEditText
    private lateinit var saveButton: MaterialButton
    private lateinit var userProfileRepository: UserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userProfileRepository = UserProfileRepository.getInstance(this)

        // Инициализация views
        nameEditText = findViewById(R.id.nameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        weightEditText = findViewById(R.id.weightEditText)
        heightEditText = findViewById(R.id.heightEditText)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)
        activityLevelRadioGroup = findViewById(R.id.activityLevelRadioGroup)
        caloriesGoalEditText = findViewById(R.id.caloriesGoalEditText)
        saveButton = findViewById(R.id.saveButton)

        // Загрузка данных профиля
        loadUserProfile()

        // Настройка кнопки сохранения
        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveUserProfile()
            }
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            userProfileRepository.getUserProfile().collectLatest { profile ->
                profile?.let { fillProfileData(it) }
            }
        }
    }

    private fun fillProfileData(profile: UserProfile) {
        nameEditText.setText(profile.name)
        ageEditText.setText(profile.age.toString())
        weightEditText.setText(profile.weight.toString())
        heightEditText.setText(profile.height.toString())
        caloriesGoalEditText.setText(profile.dailyCaloriesGoal.toString())

        when (profile.gender) {
            Gender.MALE -> genderRadioGroup.check(R.id.maleRadioButton)
            Gender.FEMALE -> genderRadioGroup.check(R.id.femaleRadioButton)
            Gender.OTHER -> genderRadioGroup.check(R.id.otherRadioButton)
        }

        when (profile.activityLevel) {
            ActivityLevel.SEDENTARY -> activityLevelRadioGroup.check(R.id.sedentaryRadioButton)
            ActivityLevel.LIGHTLY_ACTIVE -> activityLevelRadioGroup.check(R.id.lightlyActiveRadioButton)
            ActivityLevel.MODERATELY_ACTIVE -> activityLevelRadioGroup.check(R.id.moderatelyActiveRadioButton)
            ActivityLevel.VERY_ACTIVE -> activityLevelRadioGroup.check(R.id.veryActiveRadioButton)
            ActivityLevel.EXTRA_ACTIVE -> activityLevelRadioGroup.check(R.id.extraActiveRadioButton)
        }
    }

    private fun validateInputs(): Boolean {
        if (nameEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
            return false
        }

        if (ageEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите возраст", Toast.LENGTH_SHORT).show()
            return false
        }

        if (weightEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите вес", Toast.LENGTH_SHORT).show()
            return false
        }

        if (heightEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите рост", Toast.LENGTH_SHORT).show()
            return false
        }

        if (caloriesGoalEditText.text.isNullOrBlank()) {
            Toast.makeText(this, "Введите цель по калориям", Toast.LENGTH_SHORT).show()
            return false
        }

        if (genderRadioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Выберите пол", Toast.LENGTH_SHORT).show()
            return false
        }

        if (activityLevelRadioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Выберите уровень активности", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveUserProfile() {
        val gender = when (genderRadioGroup.checkedRadioButtonId) {
            R.id.maleRadioButton -> Gender.MALE
            R.id.femaleRadioButton -> Gender.FEMALE
            else -> Gender.OTHER
        }

        val activityLevel = when (activityLevelRadioGroup.checkedRadioButtonId) {
            R.id.sedentaryRadioButton -> ActivityLevel.SEDENTARY
            R.id.lightlyActiveRadioButton -> ActivityLevel.LIGHTLY_ACTIVE
            R.id.moderatelyActiveRadioButton -> ActivityLevel.MODERATELY_ACTIVE
            R.id.veryActiveRadioButton -> ActivityLevel.VERY_ACTIVE
            else -> ActivityLevel.EXTRA_ACTIVE
        }

        val profile = UserProfile(
            name = nameEditText.text.toString(),
            age = ageEditText.text.toString().toInt(),
            weight = weightEditText.text.toString().toFloat(),
            height = heightEditText.text.toString().toInt(),
            gender = gender,
            activityLevel = activityLevel,
            dailyCaloriesGoal = caloriesGoalEditText.text.toString().toInt()
        )

        lifecycleScope.launch {
            userProfileRepository.saveUserProfile(profile)
            Toast.makeText(this@ProfileActivity, "Профиль сохранен", Toast.LENGTH_SHORT).show()
        }
    }
} 