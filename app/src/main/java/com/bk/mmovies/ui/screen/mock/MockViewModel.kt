package com.bk.mmovies.ui.screen.mock

import androidx.lifecycle.ViewModel
import com.bk.mmovies.connectivity.InternetMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MockViewModel @Inject constructor(internetMonitor: InternetMonitor) :
        ViewModel() {
}
