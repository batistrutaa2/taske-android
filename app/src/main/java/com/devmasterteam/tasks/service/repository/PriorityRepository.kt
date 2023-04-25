package com.devmasterteam.tasks.service.repository

import android.content.Context
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.service.constants.TaskConstants
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.repository.local.TaskDatabase
import com.devmasterteam.tasks.service.repository.remote.PriorityService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PriorityRepository(context: Context) : BaseRepository(context) {

    private val remote = RetrofitClient.getService(PriorityService::class.java)
    private val dataBase = TaskDatabase.getDatabase(context).priorityDAO()

    // esse companion retira a reponsabilidade da recicle view consulta o banco toda hora
    // nele e criado um cash onde os dados ficam salvos e retorna sempre que o indice existir na classe

    companion object {
        private val cache = mutableMapOf<Int, String>()
        fun getDescription(id: Int): String {
            return cache[id] ?: ""
        }

        fun setDescription(id: Int, str: String) {
            cache[id] = str
        }
    }

    fun getDescription(id: Int): String {
        return if (PriorityRepository.getDescription(id) == "") {
            val description = dataBase.getDescription(id)
            setDescription(id, description)
            description
        } else {
            PriorityRepository.getDescription(id)
        }
    }


    fun list(listener: APIListener<List<PriorityModel>>) {
        executeCall(remote.list(), listener)
    }

    fun list(): List<PriorityModel> {
        return dataBase.list()
    }

    fun save(list: List<PriorityModel>) {
        dataBase.clear()
        dataBase.save(list)
    }

}