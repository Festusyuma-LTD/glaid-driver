package festusyuma.com.glaiddriver.controller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.CarouselAdapter
import festusyuma.com.glaiddriver.utilities.PrefManager
import festusyuma.com.glaiddriver.utilities.buttonClickAnim
import kotlinx.android.synthetic.main.activity_carousel.*


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
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    2 -> getStartedBtn.visibility = View.VISIBLE
                    else -> getStartedBtn.visibility = View.INVISIBLE
                }
            }
        })
    }

    fun getStartedBtnClick(view: View) {
        view.startAnimation(buttonClickAnim)
        val getStartedIntent = Intent(this, GetStartedActivity::class.java)
        startActivity(getStartedIntent)

    }

    private fun launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false)
        startActivity(Intent(this, GetStartedActivity::class.java))
        finish()
    }

}
