package festusyuma.com.glaiddriver.controller

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.utilities.DashboardFragment
import festusyuma.com.glaiddriver.utilities.NewOrderFragment
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.models.Order
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.models.live.PendingOrder


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private val errorDialogRequest = 9001

    private val requestCode = 42
    private val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var locationPermissionsGranted = false

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userMarker: Marker
    private lateinit var userLocationBtn: ImageView

    private lateinit var authPref: SharedPreferences
    private lateinit var dataPref: SharedPreferences

    private lateinit var drawerHeader: View

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

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)!!
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)!!
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Construct a FusedLocationProviderClient.
        dataPref = getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        drawerHeader = findViewById(R.id.nav_header_driver_map)

        // Get the SupportMapFragment and register for the callback
        // when the map is ready for use.

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initUserDetails()
        startFragment()
    }

    private fun initUserDetails() {
        val userJson = dataPref.getString("userDetails", "null")

        if (userJson != null) {
            val fullNameTV: TextView = drawerHeader.findViewById(R.id.fullName)
            val emailTV: TextView = drawerHeader.findViewById(R.id.email)
            val rating: RatingBar = drawerHeader.findViewById(R.id.rating)
            val ratingTxt: TextView = drawerHeader.findViewById(R.id.ratingText)

            val user = gson.fromJson(userJson, User::class.java)

            fullNameTV.text = user.fullName
            emailTV.text = user.email
            rating.rating = 4.1F
            ratingTxt.text = "4.1"
        }
    }

    private fun startFragment() {
        if (dataPref.contains(getString(R.string.sh_pending_order))) {
            val orderJson = dataPref.getString(getString(R.string.sh_pending_order), null)
            if (orderJson != null) {
                val order = gson.fromJson(orderJson, Order::class.java)
                initiateLivePendingOrder(order)

                when(order.statusId) {
                    1L -> startPendingOrderFragment()
                    2L -> startPendingOrderFragment()
                    3L -> startPendingOrderFragment()
                }
            }else startRootFragment()
        }else startRootFragment()
    }

    private fun startRootFragment() {
        val rootFragment = DashboardFragment()

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
            .replace(R.id.frameLayoutId, rootFragment)
            .commit()
    }

    private fun startPendingOrderFragment() {
        val newOrderFragment = NewOrderFragment()

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down)
            .replace(R.id.frameLayoutId, newOrderFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun initiateLivePendingOrder(order: Order) {
        val livePendingOrder = ViewModelProviders.of(this).get(PendingOrder::class.java)
        livePendingOrder.amount.value = order.amount
        livePendingOrder.gasType.value = order.gasType
        livePendingOrder.gasUnit.value = order.gasUnit
        livePendingOrder.quantity.value = order.quantity
        livePendingOrder.statusId.value = order.statusId
        livePendingOrder.truck.value = order.truck
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
        AppCompatResources.getDrawable(this, R.drawable.ic_drivermapmarker)!!.toBitmap(50, 100)

        try {
            if (mLocationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = false
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

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

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

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun closeDrawer() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

    }

    fun toggleDrawerClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun orderHistoryClick(view: View) {
        view.startAnimation(buttonClickAnim)
        closeDrawer()
        val intent = Intent(this, OrderHistoryActivity::class.java)
        startActivity(intent)
    }

    fun inviteFriendsClick(view: View) {
        view.startAnimation(buttonClickAnim)
        closeDrawer()
        val intent = Intent(this, InviteFriendsActivity::class.java)
        startActivity(intent)
    }

    fun helpClick(view: View) {
        view.startAnimation(buttonClickAnim)
        closeDrawer()
        val intent = Intent(this, HelpSupportActivity::class.java)
        startActivity(intent)

    }

    fun editProfileClick(view: View) {
        view.startAnimation(buttonClickAnim)
        closeDrawer()
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }

    fun goToUserLocation(view: View) {
        view.startAnimation(buttonClickAnim)
        closeDrawer()// fragment switching
    }

}
