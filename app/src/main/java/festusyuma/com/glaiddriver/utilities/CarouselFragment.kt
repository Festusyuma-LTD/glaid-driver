package festusyuma.com.glaiddriver.utilities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import festusyuma.com.glaiddriver.R

class CarouselFragment(var page: String?) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("PAGE VALUE", page)
        return when (page) {
            "page1" -> inflater.inflate(R.layout.carousel_view_1, container, false)
            "page2" -> inflater.inflate(R.layout.carousel_view_2, container, false)
            "page3" -> inflater.inflate(R.layout.carousel_view_3, container, false)
            else -> null
        }
    }

}