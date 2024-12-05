package bpmn.payment.delegate


import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.springframework.stereotype.Component


@Component("setArray")
class AddToSegmentDelegate() : JavaDelegate {



    private val statusHolder = ThreadLocal<String>()

    override fun execute(execution: DelegateExecution) {


        statusHolder.set(execution.getVariableLocal("status") as String);


    }
}
