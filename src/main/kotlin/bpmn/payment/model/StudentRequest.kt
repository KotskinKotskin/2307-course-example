package bpmn.payment.model

data class StudentRequest(
    var email: String,
    var price: Double,
    var courseCode: String
) {
}