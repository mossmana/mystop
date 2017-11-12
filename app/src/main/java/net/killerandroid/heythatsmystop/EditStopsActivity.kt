package net.killerandroid.heythatsmystop

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import net.killerandroid.heythatsmystop.notification.NotificationSettings

class EditStopsActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_stops)
        populateStops()
    }

    private fun populateStops() {
        val stopsList = findViewById<RecyclerView>(R.id.stops_list)
        val stops = NotificationSettings(this).getStops()
        stopsList.adapter = StopsAdapter(stops)
    }
}