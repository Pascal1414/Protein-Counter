package com.pascalrieder.proteincounter.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class TodayViewModel (application: Application) : AndroidViewModel(application) {
    var onFloatingActionButtonClick: (() -> Unit)? = null

}