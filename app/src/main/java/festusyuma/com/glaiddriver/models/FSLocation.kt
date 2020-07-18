package festusyuma.com.glaiddriver.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.auth.User

data class FSLocation (
    val geoPoint: GeoPoint? = null,
    val user: User? = null,

    @ServerTimestamp
    val timestamp: Timestamp? = null
)