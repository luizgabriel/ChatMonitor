package chat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.jini.core.lookup.ServiceTemplate
import net.jini.discovery.DiscoveryEvent
import net.jini.discovery.DiscoveryListener
import net.jini.discovery.LookupDiscovery
import net.jini.space.JavaSpace
import net.jini.space.JavaSpace05
import java.rmi.RemoteException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object TupleSpace {
    suspend fun lookup(timeout: Long = 60 * 1000): JavaSpace05? {
        val serviceTemplate = ServiceTemplate(null, arrayOf(JavaSpace::class.java), null)
        val startSignal = CountDownLatch(1)
        val discovery = LookupDiscovery(LookupDiscovery.ALL_GROUPS)
        var service: JavaSpace05? = null;

        discovery.addDiscoveryListener(object : DiscoveryListener {
            override fun discovered(p0: DiscoveryEvent?) {
                p0?.registrars?.forEach {
                    try {
                        val result = it.lookup(serviceTemplate) as JavaSpace05?
                        if (result != null) {
                            service = result
                            startSignal.countDown()
                        }
                    } catch (e: RemoteException) {
                        e.printStackTrace(System.err)
                    }
                }
            }

            override fun discarded(p0: DiscoveryEvent?) {
            }
        })

        return try {
            withContext(Dispatchers.IO) {
                startSignal.await(timeout, TimeUnit.MILLISECONDS)
            }

            return service
        } catch (e: InterruptedException) {
            null
        }
    }

}