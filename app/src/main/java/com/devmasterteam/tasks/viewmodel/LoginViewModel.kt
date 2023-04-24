package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PersonModel
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PersonRepository
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val PersonRepository = PersonRepository(application.applicationContext)
    private val PriorityRepository = PriorityRepository(application.applicationContext)
    private val SecurityPreferences = SecurityPreferences(application.applicationContext)

    private val _login = MutableLiveData<ValidationModel>()
    val login: LiveData<ValidationModel> = _login

    private val _loggedUser = MutableLiveData<Boolean>()
    val loggedUser: LiveData<Boolean> = _loggedUser


    /**
     * Faz login usando API
     */
    fun doLogin(email: String, password: String) {
        PersonRepository.login(email, password, object : APIListener<PersonModel> {
            override fun onSuccess(result: PersonModel) {
                SecurityPreferences.store(TaskConstants.SHARED.PERSON_KEY, result.personKey)
                SecurityPreferences.store(TaskConstants.SHARED.PERSON_NAME, result.name)
                SecurityPreferences.store(TaskConstants.SHARED.TOKEN_KEY, result.token)

                RetrofitClient.addHeaders(result.token, result.personKey)

                _login.value = ValidationModel()
            }

            override fun onfailure(message: String) {
                _login.value = ValidationModel(message)
            }
        })
    }

    /**
     * Verifica se usuário está logado
     */
    fun verifyLoggedUser() {
        val token = SecurityPreferences.get(TaskConstants.SHARED.TOKEN_KEY)
        val person = SecurityPreferences.get(TaskConstants.SHARED.PERSON_KEY)

        RetrofitClient.addHeaders(token, person)

        val logged = (token != "" && person != "")
        _loggedUser.value = logged

        if (!logged) {
            PriorityRepository.list(object : APIListener<List<PriorityModel>>{
                override fun onSuccess(result: List<PriorityModel>) {
                    PriorityRepository.save(result)
                }

                override fun onfailure(message: String) {
                    val s = ""
                }
            })
        }

    }

}