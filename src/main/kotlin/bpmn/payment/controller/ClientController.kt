package bpmn.payment.controller


import bpmn.payment.component.CamundaService
import bpmn.payment.component.PriceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/")
class IncommingPaymentController(val camundaService: CamundaService,
                                 val priceService: PriceService) {



    @GetMapping("/checkPrice")
    fun checkPrice(
        @RequestParam("courseCode") courseCode: String,
        @RequestParam("promoCode") promoCode: String?): Map<String?, Double?> {
        return priceService.getPrice(courseCode, promoCode)

    }


    @GetMapping("/checkPayment/{email}")
    fun checkPayment(@PathVariable email: String): String {
        return camundaService.searchPayment(email)
    }

}
