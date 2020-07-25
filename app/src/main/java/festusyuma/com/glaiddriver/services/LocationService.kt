package festusyuma.com.glaiddriver.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.firestore.GeoPoint
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.controller.MapsActivity
import festusyuma.com.glaiddriver.helpers.*
import festusyuma.com.glaiddriver.models.FSLocation


class LocationService: Service() {

    private val ongoingNotificationId = 1
    private val updateInterval =  1000L
    private val fastestInterval = 500L
    private val channelId = "locationServiceChannel"
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
            val pendingIntent = Intent(this, MapsActivity::class.java)
                .let {
                    PendingIntent.getActivity(this, 0, it, 0)
                }

            val notification = Notification.Builder(this, channelId)
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.ic_icon)
                .setContentIntent(pendingIntent)
                .build()

            startForeground(ongoingNotificationId, notification)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getLocation()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelId, importance)

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = updateInterval
        locationRequest.fastestInterval = fastestInterval

        if (hasPermissions()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback(),
                Looper.myLooper()
            )
        }else {
            Log.d(SERVICE_LOG_TAG, "stopping the location service.");
            stopSelf();
            return;
        }
    }

    private fun locationCallback(): LocationCallback {

        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?: return
                saveUserLocation(locationResult.lastLocation)
            }
        }
    }

    private fun saveUserLocation(lc: Location) {
        val geoPoint = GeoPoint(lc.latitude, lc.longitude)
        val location = FSLocation(geoPoint, auth.uid, lc.bearing)

        if (location.userId != null) {
            val locationRef = db.collection(getString(R.string.fs_user_locations)).document(location.userId)

            locationRef
                .set(location)
                .addOnFailureListener { e ->
                    Log.w(FIRE_STORE_LOG_TAG, "Error saving location", e)
                }
        }else {
            Log.v(SERVICE_LOG_TAG, "user logged out")
            stopSelf()
        }
    }

    private fun hasPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}