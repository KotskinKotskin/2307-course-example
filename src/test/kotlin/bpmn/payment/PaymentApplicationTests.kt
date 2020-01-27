package bpmn.payment

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.camunda.bpm.engine.test.ProcessEngineRule
import org.junit.Rule
import org.springframework.dao.support.DataAccessUtils.singleResult
import org.camunda.bpm.engine.TaskService
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.test.Deployment
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareAssertions.assertThat
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.execute
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.job
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource


@RunWith(SpringRunner::class)
@SpringBootTest

class ProductionProcessTest {

	@Autowired
	internal var runtimeService: RuntimeService? = null

	@Test
	fun startProductionProcess() {
		val pi = runtimeService!!.startProcessInstanceByKey(PRODUCTION_PROCESS_KEY)
		assertThat(pi).isNotNull()
		assertThat(pi).isStarted
		assertThat(pi).isWaitingAt("Task_0w7obdg")
		execute(job());
		assertThat(pi).isWaitingAt("ServiceTask_1sz8j3l")
	}
	companion object {

		private val PRODUCTION_PROCESS_KEY = "CourseOrigination"
	}

}