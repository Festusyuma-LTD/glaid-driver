package festusyuma.com.glaiddriver.helpers

class Api {
    companion object {
        private const val API_BASE_URL: String = "https://glaid.herokuapp.com/"
        const val LOGIN: String = "${API_BASE_URL}login"
        const val REGISTER: String = "${API_BASE_URL}driver/register"
        const val RESET_PASSWORD: String = "${API_BASE_URL}reset_password"
        const val VALIDATE_OTP: String = "${API_BASE_URL}validate_otp"
        const val DASHBOARD: String = "${API_BASE_URL}driver/dashboard"
        const val VALIDATE_TOKEN: String = "${API_BASE_URL}driver/dashboard"
        const val START_TRIP: String = "${API_BASE_URL}driver/booking/start_trip"
        const val COMPLETE_TRIP: String = "${API_BASE_URL}driver/booking/complete_trip"
        const val RATE_CUSTOMER: String = "${API_BASE_URL}driver/booking/rate_customer"
        const val CONFIRM_PAYMENT: String = "${API_BASE_URL}driver/booking/confirm_payment"
        const val PAYMENT_FAILED: String = "${API_BASE_URL}driver/booking/payment_failed"
        fun orderDetails(orderId: Long): String {
            return "${API_BASE_URL}driver/booking/$orderId"
        }
    }
}