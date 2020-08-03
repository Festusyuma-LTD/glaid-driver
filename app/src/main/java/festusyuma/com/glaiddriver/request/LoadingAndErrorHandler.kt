package festusyuma.com.glaiddriver.request

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.wang.avi.AVLoadingIndicatorView
import festusyuma.com.glaiddriver.R

open class LoadingAndErrorHandler(private val c: Activity) {

    var operationRunning = false
    private val loadingCover: ConstraintLayout = c.findViewById(R.id.loadingCoverConstraint)
    val loadingAvi: AVLoadingIndicatorView = c.findViewById(R.id.avi)
    private val errorMsg: TextView = c.findViewById(R.id.errorMsg)

    init {
        errorMsg.setOnClickListener { hideError() }
    }

    fun setLoading(loading: Boolean) {
        if (loading) {
            loadingCover.visibility = View.VISIBLE
            operationRunning = true
        } else {
            loadingCover.visibility = View.GONE
            operationRunning = false
        }
    }

    fun showError(msg: String) {
        errorMsg.text = msg
        errorMsg.visibility = View.VISIBLE
    }

    private fun hideError() {
        errorMsg.visibility = View.INVISIBLE
    }
}