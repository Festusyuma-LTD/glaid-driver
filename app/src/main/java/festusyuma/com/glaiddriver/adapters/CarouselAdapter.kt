package festusyuma.com.glaiddriver.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import festusyuma.com.glaiddriver.services.DataServices.fragmentPageDatas
import festusyuma.com.glaiddriver.utilities.CarouselFragment


/**
 * Created by Chidozie Henry on Tuesday, May 05, 2020.
 * Email: okebugwuchidozie@gmail.com
 */
class CarouselAdapter(fm: FragmentManager?, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm!!, lifecycle) {

    override fun createFragment(position: Int): Fragment {

        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = CarouselFragment("page1")
            1 -> fragment = CarouselFragment("page2")
            2 -> fragment = CarouselFragment("page3")
        }
        return fragment!!
    }

    override fun getItemCount(): Int {
        return fragmentPageDatas.count()
    }


}

