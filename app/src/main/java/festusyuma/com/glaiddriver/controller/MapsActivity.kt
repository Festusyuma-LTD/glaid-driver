package festusyuma.com.glaiddriver.controller

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import festusyuma.com.glaiddriver.R


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    LocationListener {

    private lateinit var mMap: GoogleMap
    private val TAG: String = MapsActivity::class.java.simpleName

    private lateinit var mCameraPosition: CameraPosition

    // The entry point to the Fused Location Provider.
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
    private val mDefaultLocation = LatLng(-33.8523341, 151.2106085)
    private val DEFAULT_ZOOM = 15
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var mLocationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private lateinit var mLastKnownLocation: Location

    // Keys for storing activity state.
    private val KEY_CAMERA_POSITION = "camera_position"
    private val KEY_LOCATION = "location"

    override fun onCreate(savedInstanceState: Bundle?) {
        val w: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            )
        }
        w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        w.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)!!
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)!!
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Get the SupportMapFragment and register for the callback
        // when the map is ready for use.

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(KEY_CAMERA_POSITION, mMap.cameraPosition)
        outState.putParcelable(KEY_LOCATION, mLastKnownLocation)
        super.onSaveInstanceState(outState)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
//        mMap.setOnMyLocationButtonClickListener(this)
//        mMap.setOnMyLocationClickListener(this)

        try {
            // Customise the styling of the base map using a JSON object defined  in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.uber_map_style
                )
            )
            if (!success) {
                Log.e("FragmentActivity.TAG", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("FragmentActivity.TAG", "Can't find style. Error: ", e)
        }

        // Prompt the user for permission.
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private fun getDeviceLocation() {

        val mapIcon =
            AppCompatResources.getDrawable(this, R.drawable.ic_drivermapmarker)!!.toBitmap(50, 100)

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                val locationResult: Task<Location> =
                    mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.result!!
                        val uLocation = LatLng(
                            mLastKnownLocation.latitude,
                            mLastKnownLocation.longitude
                        )
//                        mMap.addMarker(
//                            MarkerOptions().position(uLocation).title("Your Location")
//                                .icon(BitmapDescriptorFactory.fromBitmap(mapIcon))
//                        )
                        mMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                uLocation, DEFAULT_ZOOM.toFloat()
                            )
                        )
                        mMap.addCircle(
                            CircleOptions().center(uLocation).radius(500.0)
                                .strokeWidth(1f)
                                .strokeColor(R.color.colorPrimaryDark)
                                .fillColor(Color.argb(50, 78, 0, 124))
                        )
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        mMap.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM.toFloat())
                        )

//                        mMap.addMarker(
//                            MarkerOptions().position(mDefaultLocation).title("Your Location")
//                                .icon(BitmapDescriptorFactory.fromBitmap(mapIcon))
//                        )
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private fun updateLocationUI() {
        val mapIcon =
            AppCompatResources.getDrawable(this, R.drawable.ic_drivermapmarker)!!.toBitmap(50, 100)

        try {
            if (mLocationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false

//                mLastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    override fun onLocationChanged(location: Location?) {
        TODO("Not yet implemented")
    }

    /**
     * This callback will never be invoked and providers can be considers as always in the
     * [LocationProvider.AVAILABLE] state.
     *
     */
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     * update.
     */
    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")
    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     * update.
     */
    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }

    private fun henryCloseDrawer() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

    }

    fun toggleDrawerClick(view: View) {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun orderHistoryClick(view: View) {
        henryCloseDrawer()
        val intent = Intent(this, OrderHistoryActivity::class.java)
        startActivity(intent)
    }

    fun inviteFriendsClick(view: View) {}
    fun helpClick(view: View) {
        henryCloseDrawer()
        val intent = Intent(this, HelpSupportActivity::class.java)
        startActivity(intent)

    }

    fun editProfileClick(view: View) {
        henryCloseDrawer()
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }


}
