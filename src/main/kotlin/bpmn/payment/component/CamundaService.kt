package bpmn.payment.component

import mu.KotlinLogging
import org.camunda.bpm.engine.HistoryService
import org.camunda.bpm.engine.RuntimeService
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CamundaService(
    private val historyService: HistoryService,
    private val runtimeService: RuntimeService) {

    fun startOriginationProcess(email: String, courseCode: String, price: Double, promoCode: String?) {
        logger.info{"Start origination processs for email=${email}, " +
            "courseCode=${courseCode}, price=${price}, promocode=${promoCode}"}
        val businessKey = email+courseCode+promoCode
        val countOfRuntimeProcesses = runtimeService
            .createProcessInstanceQuery()
            .active()
            .processInstanceBusinessKey(businessKey).list().count()
        logger.info {"count of current same process is ${countOfRuntimeProcesses}"}
        if (countOfRuntimeProcesses == 0) {
        runtimeService
            .createMessageCorrelation("TryPayment")
            .processInstanceBusinessKey(businessKey)
            .setVariable("email", email)
            .setVariable("paymentOk", false)
            .setVariable("courseCode", courseCode)
            .setVariable("promoCode", promoCode)
            .setVariable("price", price)
            .correlateStartMessage()
            logger.info {"process started"}
        }
    }

    fun searchPayment(email: String): String {

        val id =historyService
            .createHistoricProcessInstanceQuery()
            .variableValueEquals("email", email)
            .orderByProcessInstanceStartTime()
            .desc()
            .list()
            .firstOrNull()
        if (id != null) {
            val value = historyService.createHistoricVariableInstanceQuery()
                     .variableName("paymentStatus")
                     .processInstanceId(id.id).list().firstOrNull()
            if (value != null) {
                return  value.value as String

            }

            return "not_payed"
        }
        else return "no_info"
    }

    fun checkedByArsenalPay(businessKey: String) {
        runtimeService.createMessageCorrelation("checkedByArsenalPay")
                .processInstanceBusinessKey(businessKey)
                .correlateAll()
    }

    fun paymentStatusArsenalPay(businessKey: String) {
        runtimeService.createMessageCorrelation("PaymentStatus")
                .processInstanceBusinessKey(businessKey)
                .correlateAll()
    }

    fun findEmailByBusinessKey(businessKey: String): String {
        val processInstance = historyService.createHistoricProcessInstanceQuery().
                processInstanceBusinessKey(businessKey)

                .orderByProcessInstanceStartTime()
                .desc()
                .list()
                .firstOrNull()?.id

        return historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance)
                .variableName("email")
                .list()
                .firstOrNull()!!.value as String


    }
    fun findCourseCode(businessKey: String): String {
        val processInstance = historyService.createHistoricProcessInstanceQuery().
                processInstanceBusinessKey(businessKey)
                .processDefinitionKey("CourseOrigination")
                .orderByProcessInstanceStartTime()
                .desc()
                .list()
                .firstOrNull()?.id

        return historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance)
                .variableName("courseCode")
                .list()
                .firstOrNull()!!.value as String


    }
    fun findPriceByBusinessKey(businessKey: String): Double {
        val processInstance = historyService.createHistoricProcessInstanceQuery().
                processInstanceBusinessKey(businessKey)
                .processDefinitionKey("CourseOrigination")
                .orderByProcessInstanceStartTime()
                .desc()
                .list()
                .firstOrNull()?.id

        return historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance)
                .variableName("price")
                .list()
                .firstOrNull()!!.value as Double


    }

    fun findPriceCourseCodeEmailByBusinessKey(businessKey: String): Map<String, *> {
        return mutableMapOf(
            "price" to this.findPriceByBusinessKey(businessKey),
            "email" to this.findEmailByBusinessKey(businessKey),
            "courseCode" to this.findCourseCode(businessKey)
        )

    }
}
