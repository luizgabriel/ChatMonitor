package chat

import net.jini.space.JavaSpace05
import java.rmi.RemoteException
import java.time.Instant
import java.util.*

class ChatService(private val space: JavaSpace05, private val serviceTimeout: Long = 60 * 1000) {

    fun sendMessage(user: String, message: String, timeout: Long = serviceTimeout): ChatMessage? {
        if (message.isBlank())
            return null

        val tuple = ChatMessage()
        tuple.user = user.trim()
        tuple.message = message.trim()
        tuple.createdAt = Date.from(Instant.now())

        try {
            space.write(tuple, null, timeout)
        } catch (e: RemoteException) {
            e.printStackTrace(System.err)
        }

        return tuple
    }

    private fun readEntries(template: ChatMessage, timeout: Long): List<ChatMessage> {
        val match = space.contents(arrayListOf(template).toMutableList(), null, timeout, timeout)
        val result = arrayListOf<ChatMessage>()
        try {
            var cur = match.next()
            while (cur != null) {
                result.add(cur as ChatMessage)
                cur = match.next()
            }
        } catch (e: RemoteException) {
            e.printStackTrace(System.err)
        }

        return result
    }

    fun readMessages(timeout: Long = serviceTimeout): List<ChatMessage> {
        return readEntries(ChatMessage(), timeout)
    }

}