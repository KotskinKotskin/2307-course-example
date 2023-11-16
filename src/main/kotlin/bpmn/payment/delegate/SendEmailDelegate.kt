package bpmn.payment.delegate



import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component
import java.util.*

@Component("sendEmailDelegate")
class SendEmailDelegate(): JavaDelegate {
    override fun execute(execution: DelegateExecution) {

        var application = execution.getVariable("application")


        execution.setVariable("application", application)

      ///


    }
}
