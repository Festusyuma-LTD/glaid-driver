package festusyuma.com.glaiddriver.utilities

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.TextView

import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.helpers.getFirst
import festusyuma.com.glaiddriver.helpers.gson
import festusyuma.com.glaiddriver.models.User

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var greeting: TextView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        greeting = requireActivity().findViewById(R.id.greeting)
        val dataPref = requireActivity().getSharedPreferences(getString(R.string.cached_data), Context.MODE_PRIVATE)
        val userJson = dataPref?.getString(getString(R.string.sh_user_details), "null")

        if (userJson != null) {
            val user = gson.fromJson(userJson, User::class.java)
            greeting.text = getString(R.string.home_greeting_intro_text).format(user.fullName.getFirst())
        }
    }
}
