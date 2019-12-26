package bpmn.payment.service

import bpmn.payment.component.CamundaService
import bpmn.payment.model.StudentRequest
import org.springframework.stereotype.Service

@Service
class CallbackService(private val camundaService: CamundaService) {

    fun markCheckedProcessByBusinessKey(businessKey: String) {
        camundaService.checkedByArsenalPay(businessKey)
    }

    fun findVariablesByBusinessKey(businessKey: String): StudentRequest {
        val map = camundaService.findPriceCourseCodeEmailByBusinessKey(businessKey)
        return StudentRequest(
            email = map["email"] as String,
            courseCode = map["courseCode"] as String,
            price = map["price"] as Double)
    }

    fun markPayedProcessByBusinessKey(businessKey: String) {
        camundaService.paymentStatusArsenalPay(businessKey)
    }

    fun startOriginationProcess(email: String, courseCode: String, price:Double, promoCode: String? ) {
        camundaService.startOriginationProcess(email,courseCode, price, promoCode)
    }

}