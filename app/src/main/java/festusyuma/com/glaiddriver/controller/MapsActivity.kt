package festusyuma.com.glaiddriver.controller

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.buttonClickAnim
import festusyuma.com.glaiddriver.helpers.FIRE_STORE_LOG_TAG
import festusyuma.com.glaiddriver.helpers.db
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.models.FSLocation
import festusyuma.com.glaiddriver.models.Order
import festusyuma.com.glaiddriver.models.User
import festusyuma.com.glaiddriver.models.live.PendingOrder
import festusyuma.com.glaiddriver.utilities.DashboardFragment
import festusyuma.com.glaiddriver.utilities.NewOrderFragment


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val errorDialogRequest = 9001

    private val requestCode = 42
    private val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var locationPermissionsGranted = false

    private lateinit var gMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userMarker: Marker
    private lateinit var userLocationBtn: ImageView

    private lateinit var authPref: SharedPreferences
    private lateinit var dataPref: SharedPreferences

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerHeader: View

    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Construct a FusedLocationProviderClient.
        dataPref = getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)
        drawerLayout = findViewById(R.id.drawer_layout)
        drawerHeader = findViewById(R.id.nav_header_driver_map)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (isServiceOk()) initMap()
        initUserDetails()
        startFragment()
    }

    private fun isServiceOk(): Boolean {
        // check google service
        val apiAvailabilityInstance = GoogleApiAvailability.getInstance()
        val availability = apiAvailabilityInstance.isGooglePlayServicesAvailable(this)

        if (availability == ConnectionResult.SUCCESS) {
            return true
        }else {
            if (apiAvailabilityInstance.isUserResolvableError(availability)) {
                val dialog = apiAvailabilityInstance.getErrorDialog(this, availability, errorDialogRequest)
                dialog.show()
            }else {
                Toast.makeText(this, "You device can't make map request", Toast.LENGTH_SHORT).show()
            }
        }

        return false
    }

    private fun initMap() {
        getLocationPermission()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getLocationPermission() {
        val deniedPermissions = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permission)
            }
        }

        if (deniedPermissions.size > 0) {
            ActivityCompat.requestPermissions(this, deniedPermissions.toTypedArray(), requestCode)
        }else {
            locationPermissionsGranted = true
            initUserLocationBtn()
        }
    }

    private fun initUserLocationBtn() {
        userLocationBtn = findViewById(R.id.userLocationBtn)
        userLocationBtn.setOnClickListener {
            getUserLocation {
                markUserLocation(it)
                saveUserLocation(it)
            }
        }
    }

    private fun getUserLocation(listener: (lc: Location) -> Unit): Task<Location>? {
        try {
            if (locationPermissionsGranted) {
                if (isLocationEnabled()) {
                    return fusedLocationClient.lastLocation
                        .addOnSuccessListener {lc ->
                            if (lc != null) {
                                listener(lc)
                            }else Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                        }
                }
            }else Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        }catch (e: SecurityException) {
            Log.v("ApiLog", "${e.message}")
        }

        return null
    }

    private fun markUserLocation(lc: Location) {
        val userLocation = LatLng(lc.latitude, lc.longitude)

        val mapIcon = AppCompatResources.getDrawable(this, R.drawable.customlocation)!!.toBitmap()
        if (!this::userMarker.isInitialized) {
            userMarker = gMap.addMarker(
                MarkerOptions()
                    .position(userLocation).title("Marker in Sydney")
                    .icon(BitmapDescriptorFactory.fromBitmap(mapIcon))
                    .rotation(lc.bearing)
            )
        }else {
            userMarker.position = userLocation
            userMarker.rotation = lc.bearing
        }

        moveCamera(userLocation)
    }

    private fun saveUserLocation(lc: Location) {
        val geoPoint = GeoPoint(lc.latitude, lc.longitude)
        val location = FSLocation(geoPoint)

        val locationRef = db.collection(getString(R.string.fs_user_locations))

        /*locationRef
            .add(location)
            .addOnSuccessListener {documentReference ->
                Log.d(FIRE_STORE_LOG_TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Log.d(FIRE_STORE_LOG_TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Log.d(FIRE_STORE_LOG_TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(FIRE_STORE_LOG_TAG, "Error adding document", e)
            }*/
    }

    private fun moveCamera(location: LatLng) {
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))
    }

    // This method is called when a user Allow or Deny our requested permissions. So it will help us to move forward if the permissions are granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == this.requestCode) {
            if (grantResults.isNotEmpty()) {
                var granted = true

                for (grants in grantResults) {
                    if (grants == PackageManager.PERMISSION_DENIED) {
                        granted = false
                    }
                }

                locationPermissionsGranted = granted
                if (locationPermissionsGranted) {
                    initUserLocationBtn()
                }
            }
        }
    }

    // Callback when map ready
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        if (locationPermissionsGranted) {
            getUserLocation {markUserLocation(it)}

            gMap.isMyLocationEnabled = true
            gMap.uiSettings.isMyLocationButtonEnabled = false
        }

        try {
            // Customise the styling
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.uber_map_style
                )
            )
        } catch (e: Resources.NotFoundException) {
            Log.e("FragmentActivity.TAG", "Error parsing style. Error: ", e)
        }
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
        val livePendingOrder = ViewModelProvider(this).get(PendingOrder::class.java)
        livePendingOrder.amount.value = order.amount
        livePendingOrder.gasType.value = order.gasType
        livePendingOrder.gasUnit.value = order.gasUnit
        livePendingOrder.quantity.value = order.quantity
        livePendingOrder.statusId.value = order.statusId
        livePendingOrder.truck.value = order.truck
    }

    // This will check if the user has turned on location from the setting
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            true
        }else {
            buildAlertMessageNoGps()
            false
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.enable_gps_msg)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer()
        } else super.onBackPressed()
    }

    private fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    fun toggleDrawerClick(view: View) {
        view.startAnimation(buttonClickAnim)

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else drawerLayout.openDrawer(GravityCompat.START)
    }

    fun orderHistoryClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, OrderHistoryActivity::class.java)
        startActivity(intent)
    }

    fun inviteFriendsClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, InviteFriendsActivity::class.java)
        startActivity(intent)
    }

    fun helpClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, HelpSupportActivity::class.java)
        startActivity(intent)

    }

    fun editProfileClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }
}
