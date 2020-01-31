package bpmn.payment

import bpmn.payment.component.CamundaService
import bpmn.payment.component.EmailSender
import bpmn.payment.delegate.AddToSegmentDelegate
import bpmn.payment.delegate.SendEmailDelegate
import bpmn.payment.service.CallbackService
import com.nhaarman.mockitokotlin2.mock
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration
import org.camunda.bpm.engine.runtime.Execution
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.test.ProcessEngineRule
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.historyService
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService
import org.camunda.bpm.engine.test.mock.Mocks
import org.camunda.bpm.engine.variable.Variables
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class ProductionProcessTest {

	@get:Rule
	var processEngineRule = ProcessEngineRule(ProcessTestConfig.processEngine)

	private val  emailSender: EmailSender = mock()
	private val camundaService = CamundaService(historyService(), runtimeService())
	private val callbackService = CallbackService(camundaService)

	val PRODUCTION_PROCESS_KEY = "CourseOrigination"
	val BUSINESS_KEY = "kotskin@bk.ru0704null"

	@Before

	fun setup() {
		Mocks.register("sendEmailDelegate", SendEmailDelegate(emailSender) )
		Mocks.register("addToSegmentDelegate", AddToSegmentDelegate(emailSender))
	}

	@After
	fun teardown() {
		Mocks.reset()
	}

	@Test
	@Deployment(resources = ["BPMN/Origination.bpmn"])
	fun TechErrorPath() {
	    //Given
		val pi = startProcess()
		assertThat(pi).isWaitingAt("Task_0w7obdg")
		//When
		execute(job())
        //Then
		assertThat(pi).isWaitingAt("ServiceTask_1sz8j3l")
		execute(job())
		assertThat(pi).isEnded

	}

	@Test
	@Deployment(resources = ["BPMN/Origination.bpmn"])
	fun PromoCodePath() {
		//given
		val pi = startProcess()
		assertThat(pi).isWaitingAt("Task_0w7obdg")
		//When
		callbackService.markCheckedProcessByBusinessKey(BUSINESS_KEY)
		//Then
		assertThat(pi).isWaitingAt("Task_0relnh9")
		execute(job())
		execute(job())
		assertThat(pi).isEnded

	}

	@Test
	@Deployment(resources = ["BPMN/Origination.bpmn"])
	fun ActivatePath() {
		//given
		val pi = startProcess()
		assertThat(pi).isWaitingAt("Task_0w7obdg")
		//When
		callbackService.markCheckedProcessByBusinessKey(BUSINESS_KEY)
		assertThat(pi).isWaitingAt("Task_0relnh9")
		execute(job())
		callbackService.markPayedProcessByBusinessKey(BUSINESS_KEY)
		//Then
		assertThat(pi).isWaitingAt("Task_14fmn7v")
		execute(job())
		assertThat(pi).isEnded

	}




	private fun startProcess(): ProcessInstance {
		val pi = processEngineRule
			.runtimeService
			.startProcessInstanceByKey(PRODUCTION_PROCESS_KEY, BUSINESS_KEY,
				withVariables(
					"email", "kotskin@bk.ru",
					"courseCode", "0704",
					"promoCode", "somePromo",
					"price", 9999.00
				))
		assertThat(pi).isNotNull()
		assertThat(pi).isStarted
		return  pi
	}



}

class ProcessTestConfig {
	companion object {
		val processEngine = StandaloneInMemProcessEngineConfiguration
			.createStandaloneInMemProcessEngineConfiguration()
			.buildProcessEngine()
	}
}

