package com.hazratbilal.offlineaiassistant.ui.features.about

import androidx.lifecycle.ViewModel
import com.hazratbilal.offlineaiassistant.utils.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    appInfo: AppInfo
) : ViewModel() {
    val versionName: String = appInfo.versionName
}