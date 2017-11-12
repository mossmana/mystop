package net.killerandroid.heythatsmystop

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import net.killerandroid.heythatsmystop.notification.NotificationSettings
import net.killerandroid.heythatsmystop.notification.StopNotification
import java.util.*

class StopsAdapter(val stops : TreeSet<StopNotification>, val settings : NotificationSettings) : RecyclerView.Adapter<StopsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val stop = stops.toArray()[position] as StopNotification
        holder?.bindStop(stop, settings)
    }

    override fun getItemCount(): Int {
        return stops.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.stop_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bindStop(stop : StopNotification, settings: NotificationSettings) {
            val stopDesc = itemView.findViewById<TextView>(R.id.stop_desc)
            val onOffSwitch = itemView.findViewById<Switch>(R.id.on_off_switch)
            stopDesc.text = stop.name
            onOffSwitch.isChecked = stop.enabled
            onOffSwitch.setOnCheckedChangeListener({ _, isChecked ->
                settings.enableNotification(stop.name, isChecked)
            })
        }
    }
}