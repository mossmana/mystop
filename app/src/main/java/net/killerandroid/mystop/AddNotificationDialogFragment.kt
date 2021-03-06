package net.killerandroid.mystop

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import net.killerandroid.mystop.notification.NotificationSettings

class AddNotificationDialogFragment : DialogFragment() {

    var settings: NotificationSettings? = null
    var name: String? = null
    var position: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = NotificationSettings(context)
        val args = arguments;
        name = args.getString(NAME)
        position = args.getParcelable(POSITION)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var message: String?
        if (settings!!.isNotificationSet(name!!)) {
            message = getString(R.string.remove_notification)
        } else {
            message = getString(R.string.add_notification)
        }
        return AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(getString(android.R.string.yes)) {
                    _, _ -> toggleNotification(); dismiss()
                }
                .setNegativeButton(getString(android.R.string.no)) {
                    _, _ -> dismiss()
                }
                .create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        val listener = activity as? DialogInterface.OnDismissListener
        listener?.onDismiss(dialog)
    }

    private fun toggleNotification() {
        if (settings!!.isNotificationSet(name!!))
            settings!!.removeNotification(name!!)
        else
            settings!!.addNotification(name!!, position!!)
    }

    companion object {
        val TAG = AddNotificationDialogFragment::class.java.simpleName!!
        val NAME = "name"
        val POSITION = "position"
    }
}