package festusyuma.com.glaiddriver.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp

data class FSLocation (
    val geoPoint: GeoPoint? = null,
    val userId: String? = null,

    @ServerTimestamp
    val timestamp: Timestamp? = null
)