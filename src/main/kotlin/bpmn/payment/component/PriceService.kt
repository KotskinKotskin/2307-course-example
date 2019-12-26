package bpmn.payment.component

interface PriceService {
    fun getPrice(courseCode: String, promoCode:String?): Map<String?,Double?>
}
