package mediator

import MessageText
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.activemq.ActiveMQConnection
import org.apache.activemq.ActiveMQConnectionFactory
import rmi.ForbiddenMessage
import javax.jms.MessageListener
import javax.jms.Session
import javax.jms.TextMessage

@Composable
@Preview
fun MediatorApp() {
    val messages = remember { mutableStateListOf<ForbiddenMessage>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val connectionFactory = ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
        val connection = connectionFactory.createConnection();
        connection.start()

        val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        val dest = session.createTopic("ForbiddenMessagesTopic")

        val subscriber = session.createConsumer(dest)
        subscriber.messageListener =
            MessageListener { message ->
                coroutineScope.launch {
                    val forbiddenMessage = Json.decodeFromString<ForbiddenMessage>((message as TextMessage).text)
                    println("Mensagem recebida no Tópico \"ForbiddenMessagesTopic\": $forbiddenMessage")
                    messages.add(forbiddenMessage)
                }
            }

        while (true) {
            delay(5000)
        }
    }

    DesktopMaterialTheme {
        Column(modifier = Modifier.padding(10.dp)) {
            Text("Mediador", fontSize = 20.sp, modifier = Modifier.padding(bottom = 10.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(messages) {
                    MessageText(
                        user = it.user,
                        message = it.message
                    )
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Mediador") {
        MediatorApp()
    }
}
