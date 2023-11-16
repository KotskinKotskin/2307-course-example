package bpmn.payment.component



interface EmailSender {
    fun findContact(email: String): Int?
    fun createContact(email: String): Int
    fun findOrInsertContact(email: String): Int
    fun addToSegment(contact: Int, segment: Int)
    fun sendEmail(id: Int, emailId: Int)
    fun sendEmail(email :String, emailId: Int)

}
