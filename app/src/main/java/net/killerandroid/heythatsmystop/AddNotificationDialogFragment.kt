package net.killerandroid.heythatsmystop

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import net.killerandroid.heythatsmystop.notification.NotificationSettings

class AddNotificationDialogFragment : DialogFragment() {

    var settings: NotificationSettings? = null
    var title: String? = null
    var position: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = NotificationSettings(context)
        val args = arguments;
        title = args.getString(TITLE)
        position = args.getParcelable(POSITION)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var message: String? = null
        if (settings!!.isNotificationSet(title!!)) {
            message = getString(R.string.remove_notification)
        } else {
            message = getString(R.string.add_notification)
        }
        return AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(getString(android.R.string.yes)) {
                    dialog, whichButton -> toggleNotification(); dismiss()
                }
                .setNegativeButton(getString(android.R.string.no)) {
                    dialog, whichButton -> dismiss()
                }
                .create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        val listener = activity as? DialogInterface.OnDismissListener
        listener?.onDismiss(dialog)
    }

    private fun toggleNotification() {
        if (settings!!.isNotificationSet(title!!))
            settings!!.removeNotification(title!!)
        else
            settings!!.addNotification(title!!, position!!)
    }

    companion object {
        val TAG = AddNotificationDialogFragment::class.java!!.simpleName
        val TITLE = "notificationId"
        val POSITION = "position"
    }
}