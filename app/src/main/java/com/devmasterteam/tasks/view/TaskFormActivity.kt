package com.devmasterteam.tasks.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.databinding.ActivityRegisterBinding
import com.devmasterteam.tasks.databinding.ActivityTaskFormBinding
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.model.TaskModel
import com.devmasterteam.tasks.viewmodel.RegisterViewModel
import com.devmasterteam.tasks.viewmodel.TaskFormViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class TaskFormActivity : AppCompatActivity(), View.OnClickListener,
    DatePickerDialog.OnDateSetListener {

    private lateinit var viewModel: TaskFormViewModel
    private lateinit var binding: ActivityTaskFormBinding

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private var listPriority: List<PriorityModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Variáveis da classe
        viewModel = ViewModelProvider(this).get(TaskFormViewModel::class.java)
        binding = ActivityTaskFormBinding.inflate(layoutInflater)

        // Eventos
        binding.buttonSave.setOnClickListener(this)
        binding.buttonDate.setOnClickListener(this)

        viewModel.loadPriority()

        observe()

        // Layout
        setContentView(binding.root)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val duoDate = dateFormat.format(calendar.time)
        binding.buttonDate.text = duoDate
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_date -> handleDate()
            R.id.button_save -> handleSave()
        }
    }

    private fun observe() {
        viewModel.priorityList.observe(this) {
            listPriority = it
            val list = mutableListOf<String>()
            it.map { itens -> list.add(itens.description) }
            binding.spinnerPriority.adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
        }

        viewModel.taskSave.observe(this) {
            if (it.status()) {
                toast(getString(R.string.task_created))
                finish()
            } else {
                toast(it.message())
            }
        }
    }

    private fun toast(str: String){
        Toast.makeText(applicationContext, str, Toast.LENGTH_LONG).show()
    }

    private fun handleDate() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun handleSave() {
        val task = TaskModel().apply {
            this.id = 0
            this.description = binding.editDescription.text.toString()
            this.complete = binding.checkComplete.isChecked
            this.dueDate = binding.buttonDate.text.toString()

            val index = binding.spinnerPriority.selectedItemPosition
            this.priorityId = listPriority[index].id
        }
        viewModel.save(task)
    }
}