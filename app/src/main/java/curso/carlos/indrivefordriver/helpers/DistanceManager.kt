package curso.carlos.indrivefordriver.helpers

import android.location.Location
import java.util.*

class DistanceManager {

    companion object {
        /**
         * calculate distance between two points
         */
        fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
            val results = FloatArray(1)
            Location.distanceBetween(lat1, lng1, lat2, lng2, results)
            // distance in meter
            return results[0]
        }

        /**
         * create distance text
         */
        fun distanceText(distance: Float): String {
            val distanceString: String

            if (distance < 1000)
                if (distance < 1)
                    distanceString = String.format(Locale.US, "%dm", 1)
                else
                    distanceString = String.format(Locale.US, "%dm", Math.round(distance))
            else if (distance > 10000)
                if (distance < 1000000)
                    distanceString = String.format(Locale.US, "%dkm", Math.round(distance / 1000))
                else
                    distanceString = "FAR"
            else
                distanceString = String.format(Locale.US, "%.2fkm", distance / 1000)

            return distanceString
        }
    }
}