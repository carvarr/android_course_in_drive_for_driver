package curso.carlos.indrivefordriver.repositories

import com.google.firebase.database.FirebaseDatabase
import curso.carlos.indrivefordriver.model.History

class RouteRepository {

    private val db = FirebaseDatabase.getInstance().reference

    fun saveHistory(userId: String, history: History) {
        db.child("service_history").child("$userId").setValue(history)
    }

}