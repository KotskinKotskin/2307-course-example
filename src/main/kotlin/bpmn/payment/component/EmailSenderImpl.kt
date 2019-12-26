package bpmn.payment.component

import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class EmailSenderImpl : EmailSender {



    @Value("\${email.token}")
    private val TOKEN: String? =null

    @Value("\${email.baseurl}")
    private val BASEMAUTICURL: String?  = null

    private val rest = RestTemplate()
    var headers = HttpHeaders().also {
        it.add("Authorization", TOKEN)
        it.contentType = MediaType.APPLICATION_JSON
    }

    private fun createHTTPEntity(json: String?): HttpEntity<String> {
        return HttpEntity(json, headers)
    }

    override fun findContact(email: String): Int? {
        val result = rest
            .exchange(
                BASEMAUTICURL + "/contacts?search=email:${email}",
                HttpMethod.GET, createHTTPEntity(null),
                String::class.java).body
        val obj = JSONObject(result)
        if (obj.getInt("total") > 0) {
            val contacts = obj.getJSONObject("contacts")
            val keys = contacts.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                return Integer.parseInt(key)
            }
        }
        return null
    }

    override fun createContact(email: String): Int {
        val map = mapOf<String, String>("email" to email)
        val json = JSONObject(map)
        val result = rest
            .exchange(
                "$BASEMAUTICURL/contacts/new",
                HttpMethod.POST,
                createHTTPEntity(json.toString()),
                String::class.java).body
        val obj = JSONObject(result).getJSONObject("contact").getInt("id")
        return obj
    }

    override fun findOrInsertContact(email: String): Int {
        return findContact(email) ?: createContact(email)
    }

    override fun addToSegment(contact: Int, segment: Int) {
        rest.exchange(
            BASEMAUTICURL + "/segments/${segment}/contact/${contact}/add",
            HttpMethod.POST,
            createHTTPEntity(null), String::class.java)
    }

    override fun sendEmail(email: String, emailId: Int) {
        sendEmail(findOrInsertContact(email), emailId)
    }

    override fun sendEmail(id: Int, emailId: Int) {
        rest.exchange(BASEMAUTICURL + "/emails/${emailId}/contact/${id}/send",
            HttpMethod.POST,
            createHTTPEntity(null),
            String::class.java).body

    }


}
