package rmi

import java.rmi.Remote
import java.rmi.RemoteException
import java.util.*

interface IMessageService : Remote {
    @Throws(RemoteException::class)
    fun sendMessage(user: String, message: String, createdAt: Date);
}

