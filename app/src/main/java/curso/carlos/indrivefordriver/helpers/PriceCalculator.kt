package curso.carlos.indrivefordriver.helpers

class PriceCalculator {

    companion object {

        /**
         * get price value
         */
        fun calculateByDistance(distance: Float): Float {
            return distance * 10
        }

    }
}