package bpmn.payment.delegate



import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.util.*

@Component("SendSmsToCustomer")
class SendEmailDelegate(): JavaDelegate {
    override fun execute(execution: DelegateExecution) {

        var phoneNumber = execution.getVariable("customer_phone_number")
     //   this.sendSms(phoneNumber, YOUR_APPROVAL_TEMPLATE_ID)

      //  execution.setVariable("application", application)

      ///


    }
}
