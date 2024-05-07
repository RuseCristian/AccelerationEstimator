package com.example.accelerationestimator.ui

import androidx.lifecycle.ViewModel
import androidx.fragment.app.Fragment

class TabViewModel : ViewModel() {
    val tabFragments: MutableList<Fragment> = mutableListOf()
    var selectedTabIndex: Int = 0
}