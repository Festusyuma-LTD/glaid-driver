package festusyuma.com.glaiddriver.utilities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import festusyuma.com.glaiddriver.R
import festusyuma.com.glaiddriver.controller.ChatActivity
import festusyuma.com.glaiddriver.models.User


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewOrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mainOrderMessage: TextView
    private lateinit var deliverButton: Button
    private lateinit var callCustomerButton: Button
    private lateinit var textCustomerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_order, container, false)

        callCustomerButton = view.findViewById(R.id.callCustomerButton) as Button
        deliverButton = view.findViewById(R.id.deliver_button) as Button
        textCustomerButton = view.findViewById(R.id.textCustomerButton) as Button

        mainOrderMessage = view.findViewById(R.id.main_order_message) as TextView
        callCustomerButton.setOnClickListener(clickListener)
        textCustomerButton.setOnClickListener(clickListener)
        deliverButton.setOnClickListener(clickListener)
        return view
    }

    val clickListener = View.OnClickListener { view ->
        view.startAnimation(buttonClickAnim)
        when (view.id) {
            R.id.callCustomerButton -> {
                //run call intent
                val phone = "+2348127736889"
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                startActivity(intent)
            }
            R.id.textCustomerButton -> {
                //add user object and send as intent to chat
                val userObject =
                    User(
                        "slenzy001@gmail.com",
                        "okebugwu Chidozie Henry",
                        "08127736889",
                        "https://firebasestorage.googleapis.com/v0/b/glaid-driver.appspot.com/o/images%2F2cb91dd1-b041-4888-8891-d88467f123ec?alt=media&token=4f732e54-9db3-43ef-b991-ba53451875b7",
                        "nwMTvs8IzcMWmuKmYwDHZUUcuoB2",
                        "chidozie"
                    )
                val intent = Intent(activity, ChatActivity::class.java)
                intent.putExtra(USER_DATA, userObject)
                startActivity(intent)
            }
            R.id.deliver_button -> {
                when (deliverButton.text) {
                    getString(R.string.start_delivery) -> {
                        //Start delivery Api should be initiated here
                        deliverButton.text = getString(R.string.complete_delivery)
                        mainOrderMessage.text = getString(R.string.starting_delivery)
                    }
                    getString(R.string.complete_delivery) -> {
                        val toast = Toast.makeText(activity, deliverButton.text, Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        toast.show()
                        //Load the Genereate invoice page from here
                    }
                }

            }
        }
    }

    companion object {
        const val USER_DATA = "USER_DATA"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewOrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
