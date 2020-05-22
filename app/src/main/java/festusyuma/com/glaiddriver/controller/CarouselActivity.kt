package festusyuma.com.glaiddriver.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.adapters.CarouselAdapter

class CarouselActivity : AppCompatActivity() {
    //    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carousel)
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
}