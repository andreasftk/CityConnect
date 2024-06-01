package cityconnnect.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cityconnnect.app.data.Complain

class Notification(val complain: Complain, val userId: Int) {

    fun sendNotification(context: Context) {
        val channelId = "cityconnect_channel"
        val notificationId = complain.complainId

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CityConnect Notifications"
            val descriptionText = "Notifications for CityConnect"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Replace with your notification icon
            .setContentTitle("New Update on Your Complaint")
            .setContentText("There is a new update on your complaint: ${complain.title}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification
        with(NotificationManagerCompat.from(context)) {
            //notify(notificationId, builder.build())
        }
        Log.d("Notification", "Notification sent to userId $userId for complainId ${complain.complainId}")
    }
}
