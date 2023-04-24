package com.devmasterteam.tasks.service.listener

interface APIListener<T> {

    fun onSuccess(result: T)
    fun onfailure(message: String)
}