package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.LocationPreferences
import com.example.service.LocationPrivacyService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val preferences = LocationPreferences(context)
            // If the user enabled persist on boot and privacy protection was active:
            if (preferences.isPersistOnBoot() && preferences.isPreviouslyActive()) {
                val serviceIntent = Intent(context, LocationPrivacyService::class.java).apply {
                    this.action = LocationPrivacyService.ACTION_START
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}
