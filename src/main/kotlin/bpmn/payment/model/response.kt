package bpmn.payment.model

data class Response(
    val ofd: Ofd,
    val response: String
)

data class Ofd(
    val id: String,
    val receipt: Receipt,
    val type: String
)

data class Receipt(
    val attributes: Attributes,
    val items: List<Item>
)

data class Attributes(
    val email: String
)

data class Item(
    val name: String,
    val price: Double,
    val quantity: Int,
    val sum: Double
)