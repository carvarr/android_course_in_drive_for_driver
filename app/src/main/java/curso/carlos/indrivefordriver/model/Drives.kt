package curso.carlos.indrivefordriver.model

class Drive {

    var destination_lat: String = ""
    var destination_long: String = ""
    var origin_lat: String = ""
    var origin_long: String = ""
    var status: Boolean = false
    var service_demand = MountDemand()
}

class DriveItem {
    var id: String = ""
    var distance: String = ""
    var price: String = ""
    var status: Boolean = false
    var demand: String = ""
    var drivernameDemand: String = ""
}

class MountDemand {
    private var drivername = "nil"
    var service_mount = 0

    fun getDriverName(): String {
        if(drivername.equals("nil")) return "user defined"

        return drivername
    }
}