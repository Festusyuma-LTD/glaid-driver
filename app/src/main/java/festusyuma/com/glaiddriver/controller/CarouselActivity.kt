package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.CarouselAdapter
import festusyuma.com.glaiddriver.utilities.PrefManager


class CarouselActivity : AppCompatActivity() {
    //    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var prefManager: PrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)// Checking for first time launch - before calling setContentView()
        prefManager = PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        setContentView(R.layout.activity_carousel)
        val w: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
//        tabLayout = findViewById<TabLayout>(R.id.tabsC)
        viewPager = findViewById(R.id.carouselviewpager)
        viewPager.adapter = CarouselAdapter(supportFragmentManager, lifecycle)
        //disable animation
        viewPager.apply {
            (getChildAt(0) as? RecyclerView)?.overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER
        }

    }

    fun getStartedBtnClick(view: View) {
        val getStartedIntent = Intent(this, GetStartedActivity::class.java)
        startActivity(getStartedIntent)

    }

    private fun launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false)
        startActivity(Intent(this, GetStartedActivity::class.java))
        finish()
    }

}