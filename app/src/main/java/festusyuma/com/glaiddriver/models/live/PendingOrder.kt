package festusyuma.com.glaiddriver.models.live

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import festusyuma.com.glaiddriver.models.Truck

class PendingOrder: ViewModel() {
    val gasType: MutableLiveData<String> = MutableLiveData()
    val gasUnit: MutableLiveData<String> = MutableLiveData()
    val quantity: MutableLiveData<Double> = MutableLiveData()
    val amount: MutableLiveData<Double> = MutableLiveData()
    var statusId: MutableLiveData<Long> = MutableLiveData()
    var truck: MutableLiveData<Truck> = MutableLiveData()
}