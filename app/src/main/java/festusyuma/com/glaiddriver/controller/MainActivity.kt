package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import festusyuma.com.glaiddriver.R

class MainActivity : AppCompatActivity() {
    // This is the loading time of the splash screen
    private val splashDelayTimeZone: Long = 2000 // 1 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            startActivity(Intent(this, CarouselActivity::class.java))
            // close this activity
            finish()
        }, splashDelayTimeZone)
    }
}
