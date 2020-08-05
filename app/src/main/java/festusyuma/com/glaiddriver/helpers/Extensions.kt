package festusyuma.com.glaiddriver.helpers

import com.google.firebase.Timestamp
import org.threeten.bp.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.round

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.first().toUpperCase() + it.substring(1) }

fun String.getFirst(): String = split(" ")[0]

fun String.addCountryCode(): String {
    return if (this.length >= 11 && this[0] == '0') {
        if (this[0] == '0') {
            COUNTRY_CODE + this.substring(1)
        }else COUNTRY_CODE + this
    }else this
}

fun Double.round(round: Int): Double {
    val multiplier = 10.0.pow(round)
    return round(this * multiplier) / multiplier
}

fun Timestamp.appFormat(): String {
    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return format.format(this.toDate())
}