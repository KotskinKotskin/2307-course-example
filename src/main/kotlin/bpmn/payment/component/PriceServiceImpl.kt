package bpmn.payment.component

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import kotlin.math.round


@Component
@ConfigurationProperties("app")
class PriceServiceImpl : PriceService {

    lateinit var prices: Map<String, Double>
    lateinit var promocodes: Map<String, Double>

    override fun getPrice(courseCode: String, promoCode: String?): Map<String?,Double?> {
        val basePrice = prices[courseCode]
        var discountPrice = 0.00
        if (promoCode != null && basePrice != null && promocodes.containsKey(promoCode)) {
            discountPrice = (basePrice*(1- (promocodes[promoCode]!!))).round(2)
        }
        return mapOf("basePrice" to basePrice ,"discountPrice" to discountPrice)
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}
