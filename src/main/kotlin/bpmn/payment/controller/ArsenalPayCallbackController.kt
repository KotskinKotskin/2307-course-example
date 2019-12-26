package bpmn.payment.controller

import bpmn.payment.component.PriceService
import bpmn.payment.model.*
import bpmn.payment.service.CallbackService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/callback/arsenalpay/")
class ArsenalPayCallBackController(
                                   val callbackService: CallbackService,
                                   val priceService: PriceService) {

    val mapper = ObjectMapper().registerModule(KotlinModule())

    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    fun parseCallbackRoot(@RequestParam allRequestParams: Map<String, String>): ResponseEntity<*>? {
        logger.info { "receive $allRequestParams" }
        val businessKey = allRequestParams["ACCOUNT"] ?: error("no such parameter")
        if (allRequestParams["FUNCTION"] == "check") {
            val studentRequest = callbackService.findVariablesByBusinessKey(businessKey)
            callbackService.markCheckedProcessByBusinessKey(businessKey)
            val response = Response(response = "YES",
                ofd = Ofd
                (id = allRequestParams["RRN"]!!,
                    type = "sell",
                    receipt = Receipt(
                        attributes = Attributes(
                            email = studentRequest.email),
                        items = mutableListOf<Item>()
                            .also {
                                it.add(Item(
                                    name = "Доступ к онлайн-курсу ${studentRequest.courseCode} на сайте BPMN2.ru",
                                    price = studentRequest.price,
                                    quantity = 1,
                                    sum = studentRequest.price))
                            }
                    )
                )
            )
            return ResponseEntity(response, HttpStatus.OK)
        }

        return if (allRequestParams["FUNCTION"] == "payment" || allRequestParams["FUNCTION"] == "hold") {
            callbackService.markPayedProcessByBusinessKey(businessKey)
            val mapResponse = mapOf(
                "response" to "ok"
            )
            ResponseEntity(mapResponse, HttpStatus.OK)
        } else null
    }

    @GetMapping("/prepareArsenalPay/{email}")
    @ResponseStatus(HttpStatus.OK)
    fun prepare(
        @PathVariable("email") email: String,
        @RequestParam("courseCode") courseCode: String,
        @RequestParam("promoCode") promoCode: String?): String {

        val moneyMap = priceService.getPrice(courseCode, promoCode)

        val money = if (moneyMap["discountPrice"] != 0.00) moneyMap["discountPrice"] else moneyMap["basePrice"]
        callbackService.startOriginationProcess(
            email = email,
            courseCode = courseCode,
            promoCode = promoCode,
            price = money!!)
        return "Ok"
    }

}
