package com.hazratbilal.offlineaiassistant.utils

import android.app.ActivityManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RamChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getTotalRamGB(): Float {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return memInfo.totalMem / (1024f * 1024f * 1024f)
    }

    fun meetsRequirement(ramRequirementLabel: String): Boolean {
        val requiredGB = ramRequirementLabel
            .filter { it.isDigit() || it == '.' }
            .toFloatOrNull() ?: return true

        return getTotalRamGB() >= requiredGB
    }
}