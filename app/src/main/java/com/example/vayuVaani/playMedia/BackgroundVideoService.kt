import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.vayuVaani.MainActivity
import java.io.IOException


class BackgroundVideoService : Service(), MediaPlayer.OnPreparedListener {
    private lateinit var mediaPlayer: MediaPlayer
    private var currentPosition = 0
    private lateinit var mediaSession: MediaSession

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaSession = MediaSession(this, "video_session")
        mediaSession.setCallback(MediaSessionCallback())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val path = intent?.getStringExtra("video_path")
        currentPosition = intent?.getIntExtra("current_position", 0) ?: 0
        try {
            mediaPlayer.setDataSource(path)
            mediaPlayer.setOnPreparedListener(this)
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onPrepared(mp: MediaPlayer?) {
        // Seek to the saved position
        mp?.seekTo(currentPosition)
        // Set the MediaSession active
        mediaSession.isActive = true
        // Show a foreground notification
        createNotification()
    }

    private fun createNotification() {
        // Create a notification channel (if running on Android O or above)
        val channel = NotificationChannel(
            "video_channel",
            "Video Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)

        // Create a notification builder
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "video_channel")
            .setSmallIcon(R.drawable.ic_media_play)
            .setContentTitle("Video is playing in background")
            .setContentText("Tap to open the app")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Set the notification's click action to open the app and navigate to the desired fragment or activity
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragment_id", R.id.list_container)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        builder.setContentIntent(pendingIntent)

        // Show the notification
        startForeground(1, builder.build())
    }

    private inner class MediaSessionCallback : MediaSession.Callback() {
        override fun onPlay() {
            mediaPlayer.start()
        }

        override fun onPause() {
            mediaPlayer.pause()
        }

        override fun onSkipToNext() {
            // Skip to the next track (if available)
        }

        override fun onSkipToPrevious() {
            // Skip to the previous track (if available)
        }
    }
    // Other methods...
}
