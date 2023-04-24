package com.devmasterteam.tasks.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.service.model.ValidationModel
import com.devmasterteam.tasks.service.repository.PriorityRepository
import com.devmasterteam.tasks.service.repository.SecurityPreferences
import com.devmasterteam.tasks.service.repository.TaskRepository
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class TaskFormViewModel(application: Application) : AndroidViewModel(application) {

    private val priorityRepository = PriorityRepository(application.applicationContext)
    private val taskRepository = TaskRepository(application.applicationContext)
    private val securityPreferences = SecurityPreferences(application.applicationContext)


    private val _priorityList = MutableLiveData<List<PriorityModel>>()
    val priorityList: LiveData<List<PriorityModel>> = _priorityList

    private val _taskSave = MutableLiveData<ValidationModel>()
    val taskSave: LiveData<ValidationModel> = _taskSave

    fun loadPriority() {
        _priorityList.value = priorityRepository.list()
    }

    fun save(task: TaskModel) {

        taskRepository.create(task, object : APIListener<Boolean> {
            override fun onSuccess(result: Boolean) {
                _taskSave.value = ValidationModel()
            }

            override fun onfailure(message: String) {
                _taskSave.value = ValidationModel(message)
            }

        })
    }

}