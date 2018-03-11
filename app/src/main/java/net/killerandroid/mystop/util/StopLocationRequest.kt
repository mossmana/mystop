package net.killerandroid.mystop.util

import com.google.android.gms.location.LocationRequest

class StopLocationRequest(val interval: Long = 3000,
                          val fastestInterval: Long = 1000,
                          val priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY) {

    fun getRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = interval
        locationRequest.fastestInterval = fastestInterval
        locationRequest.priority = priority
        return locationRequest
    }
}