package bpmn.payment.delegate

import bpmn.payment.component.EmailSender
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component

@Component("sendEmailDelegate")
class SendEmailDelegate(val emailSender: EmailSender): JavaDelegate {
    override fun execute(execution: DelegateExecution) {
        val email = execution.getVariable("email") as String
        val emailId = Integer.parseInt(execution.getVariable("emailId") as String)

        emailSender.sendEmail(email,emailId)


    }
}
