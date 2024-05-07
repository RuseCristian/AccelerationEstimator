package com.example.accelerationestimator.ui
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText

class SharedViewModel : ViewModel() {
    private val _textInputEditTextMap = MutableLiveData<Map<Int, TextInputEditText>>()
    val textInputEditTextMap: LiveData<Map<Int, TextInputEditText>> = _textInputEditTextMap

    fun setTextInputEditTextMap(map: Map<Int, TextInputEditText>) {
        _textInputEditTextMap.value = map
    }
}