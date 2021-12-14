package rmi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject
import java.util.*
import javax.jms.JMSException
import javax.jms.Session

@Serializable
data class ForbiddenMessage(val user: String, val message: String)

private class ForbiddenMessagesService(private val session: Session) : UnicastRemoteObject(), IMessageService {

    private val destination = session.createTopic("ForbiddenMessagesTopic")
    private val producer = session.createProducer(destination)

    override fun sendMessage(user: String, message: String, createdAt: Date) {
        try {
            val forbiddenMessage = ForbiddenMessage(user, message)
            println("Publishing message on \"ForbiddenMessagesTopic\": $forbiddenMessage")

            val textMessage = session.createTextMessage(Json.encodeToString(forbiddenMessage))
            producer.send(textMessage)
        } catch (e: JMSException) {
            e.printStackTrace(System.err)
        }
    }

}

suspend fun main() {
    val connectionFactory = ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL)
    val connection = connectionFactory.createConnection()
    connection.start()

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val service = ForbiddenMessagesService(session)

    println("rmi.ForbiddenMessagesService RMI started on port 9090")
    println("Pressione qualquer tecla para finalizar o processo")

    withContext(Dispatchers.IO) {
        val registry = LocateRegistry.createRegistry(9090)
        registry.bind("rmi.ForbiddenMessagesService", service)

        readLine()
    }
    println("Bye!")
}