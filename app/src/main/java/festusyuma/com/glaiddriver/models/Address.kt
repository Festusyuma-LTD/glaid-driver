package festusyuma.com.glaiddriver.models

data class Address (
    var id: Long? = null,
    var address: String,
    var type: String = "home",
    var lng: Double? = null,
    var lat: Double? = null
)