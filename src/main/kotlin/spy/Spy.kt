package spy

import chat.ChatMessage
import kotlinx.coroutines.delay
import rmi.IMessageService
import java.rmi.registry.LocateRegistry
import java.util.concurrent.LinkedBlockingQueue

fun lookupForbiddenMessagesService(): IMessageService {
    return LocateRegistry.getRegistry(9090).lookup("rmi.ForbiddenMessagesService") as IMessageService
}

class Spy(private val service: IMessageService) {
    private val sentMessages = mutableSetOf<Int>()
    private val messagesQueue = LinkedBlockingQueue<ChatMessage>()

    fun sendMessages(newMessages: List<ChatMessage>) {
        val notSentMessages = newMessages.filterNot { sentMessages.contains(it.hashCode()) }
        messagesQueue.addAll(notSentMessages)
        sentMessages.addAll(notSentMessages.map { it.hashCode() })
    }

    suspend fun syncMessages() {
        while (true) {
            if (messagesQueue.isNotEmpty()) {
                val message = messagesQueue.poll()
                println("Sending message to RMI: $message")
                kotlin.runCatching { service.sendMessage(message.user, message.message, message.createdAt) }
                    .exceptionOrNull().let {
                        it?.printStackTrace(System.err)
                    }
            } else {
                delay(2000)
            }
        }
    }
}