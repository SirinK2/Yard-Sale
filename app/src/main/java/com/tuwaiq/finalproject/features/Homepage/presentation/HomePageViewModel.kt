package com.tuwaiq.finalproject.features.Homepage.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.tuwaiq.finalproject.core.domain.model.Post
import com.tuwaiq.finalproject.core.domain.use_case.GetPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomePageViewModel"
@HiltViewModel
class HomePageViewModel @Inject constructor(private val getPostUseCase: GetPostUseCase) : ViewModel() {

    fun getPost(): LiveData<List<Post>> = liveData(Dispatchers.IO) {
          emit(getPostUseCase())


    }




}