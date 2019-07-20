package curso.carlos.indrivefordriver.services

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import curso.carlos.indrivefordriver.model.Driver
import curso.carlos.indrivefordriver.model.History
import curso.carlos.indrivefordriver.repositories.RouteRepository

class DriverService {

    private var database = FirebaseDatabase.getInstance().reference
    private lateinit var driverInfoRef: ValueEventListener

    fun createDriver(uuid: String, driver: Driver) {
        database.child(PATH_DRIVER_GLOBAL).child("${PATH_DRIVER_INNER}_$uuid").setValue(driver)
    }

    fun listenDriverInfo(uuid: String, listener: ValueEventListener){
        driverInfoRef = database.child(PATH_DRIVER_GLOBAL).child("${PATH_DRIVER_INNER}_$uuid").addValueEventListener(listener)
    }

    fun changeDriverAvailability(uuid: String, availability: Boolean) {
        database.child(PATH_DRIVER_GLOBAL).child("${PATH_DRIVER_INNER}_$uuid").child("status").setValue(availability)
    }

    fun changeDriverLocation(uuid: String, lat: String, lon: String) {
        database.child(PATH_DRIVER_GLOBAL).child("${PATH_DRIVER_INNER}_$uuid").child("origin_lat").setValue(lat)
        database.child(PATH_DRIVER_GLOBAL).child("${PATH_DRIVER_INNER}_$uuid").child("origin_long").setValue(lon)
    }

    fun saveDriveHistory(userId: String, history: History) {
        val historyRepo = RouteRepository()
        historyRepo.saveHistory(userId, history)
    }

    fun dispose() {
        database.removeEventListener(driverInfoRef)
    }

    companion object {
        private const val PATH_DRIVER_GLOBAL = "drivers"
        private const val PATH_DRIVER_INNER = "driver"
    }

}