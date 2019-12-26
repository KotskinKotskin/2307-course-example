package bpmn.payment

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@EnableProcessApplication
@SpringBootApplication

class PaymentApplication

fun main(args: Array<String>) {
	runApplication<PaymentApplication>(*args)
}
