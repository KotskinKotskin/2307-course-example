package bpmn.payment.delegate

import bpmn.payment.component.EmailSender
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("addToSegmentDelegate")
class AddToSegmentDelegate(val emailSender: EmailSender) : JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        execution.setVariable("paymentStatus", "ok")
        val contactId = emailSender.findOrInsertContact(execution.getVariable("email") as String)
        val segmentId = Integer.parseInt(execution.getVariable("segmentId") as String)
        emailSender.addToSegment(contactId, segmentId)
        execution.setVariable("paymentOk", true)
    }
}
