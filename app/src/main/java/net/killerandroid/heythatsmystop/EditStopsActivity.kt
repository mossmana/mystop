package net.killerandroid.heythatsmystop

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toolbar
import net.killerandroid.heythatsmystop.notification.NotificationSettings

class EditStopsActivity: Activity() {

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_stops)
        toolbar = findViewById(R.id.toolbar2)
        toolbar?.title = getString(R.string.title_activity_stops)
        setActionBar(toolbar)
        actionBar.setDisplayHomeAsUpEnabled(true)
        populateStops()
    }

    private fun populateStops() {
        val stopsList = findViewById<RecyclerView>(R.id.stops_list)
        stopsList.layoutManager = LinearLayoutManager(this)
        val settings = NotificationSettings(this)
        val stops = settings.getStops()
        stopsList.adapter = StopsAdapter(stops, settings)
    }
}