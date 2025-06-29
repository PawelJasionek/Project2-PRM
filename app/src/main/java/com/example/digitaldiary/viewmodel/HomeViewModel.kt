package com.example.digitaldiary.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitaldiary.data.DiaryEntry
import com.example.digitaldiary.data.FirebaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn



class HomeViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    init {
        repository.addSampleEntries()
    }


    val entries: StateFlow<List<DiaryEntry>> = repository.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


}
