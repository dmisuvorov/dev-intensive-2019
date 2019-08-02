package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.repositories.PreferencesRepository
import ru.skillbranch.devintensive.utils.Utils

class ProfileViewModel : ViewModel() {
    private val repository: PreferencesRepository = PreferencesRepository
    private val profileData = MutableLiveData<Profile>()
    private val appTheme = MutableLiveData<Int>()
    private val isValidRepository = MutableLiveData<Boolean>()
    private val repositoryErorWhenSaveData = MutableLiveData<Boolean>()

    init {
        Log.d("M_ProfileViewModel", "init view model")
        profileData.value = repository.getProfile()
        appTheme.value = repository.getAppTheme()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("M_ProfileViewModel", "view model cleared")
    }

    fun getProfileData(): LiveData<Profile> = profileData

    fun getTheme(): LiveData<Int> = appTheme

    fun getRepoValid(): LiveData<Boolean> = isValidRepository

    fun saveProfileData(profile: Profile) {
        isValidRepository.value = true
        repository.saveProfile(profile)
        profileData.value = profile
    }

    fun getRepositoryErrorWhenSaveData(): LiveData<Boolean> = repositoryErorWhenSaveData

    fun onRepoEditCompleted(isError: Boolean) {
        repositoryErorWhenSaveData.value = isError
    }

    fun switchTheme() {
        if (appTheme.value == AppCompatDelegate.MODE_NIGHT_YES) {
            appTheme.value = AppCompatDelegate.MODE_NIGHT_NO
        } else {
            appTheme.value = AppCompatDelegate.MODE_NIGHT_YES
        }

        repository.saveAppTheme(appTheme.value!!)
    }

    fun repositoryChange(repository: String) {
        isValidRepository.value = repository.isEmpty() || Utils.validateRepository(repository)
    }
}