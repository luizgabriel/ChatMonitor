import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

object Spy {
    val messagesQueue = LinkedBlockingQueue<ChatMessage>()
}

@Composable
@Preview
fun SpyApp() {
    val (chat, setChat) = remember { mutableStateOf<ChatService?>(null) }
    val (forbiddenList, setForbiddenList) = remember { mutableStateOf("") }
    val (forbiddenMessages, setForbiddenMessages) = remember { mutableStateOf(listOf<ChatMessage>()) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        TupleSpace.lookup()?.let { ChatService(it) }?.let(setChat)
    }

    LaunchedEffect(chat, forbiddenList) {
        val forbiddenSet =
            forbiddenList
                .split(Regex("[\\n,]"))
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .map { it.lowercase(Locale.getDefault()) }
                .toHashSet()

        val forbiddenRegex = Regex("(" + forbiddenSet.joinToString("|") + ")")
        println(forbiddenRegex)

        while (chat != null && forbiddenSet.isNotEmpty()) {
            chat.readMessages()
                .filter { it.message.lowercase(Locale.getDefault()).contains(forbiddenRegex) }
                .let {
                    setForbiddenMessages(it)
                    Spy.messagesQueue.addAll(it);
                }
            delay(2000)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (Spy.messagesQueue.isNotEmpty()) {
                println(Spy.messagesQueue.poll())
            } else {
                delay(2000)
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    DesktopMaterialTheme {
        Row {
            Column(modifier = Modifier.padding(10.dp).weight(.5f)) {
                Text("Palavras Proibidas", fontSize = 20.sp, modifier = Modifier.padding(bottom = 10.dp))
                TextField(
                    value = forbiddenList,
                    modifier = Modifier.focusRequester(focusRequester).fillMaxSize(),
                    onValueChange = setForbiddenList,
                    singleLine = false,
                    placeholder = {
                        Text("Digite uma lista de palavras proibidas separando por vírgula ou espaço. Ex: avião, dinheiro, árvore")
                    })
            }
            Column(modifier = Modifier.weight(.5f).padding(10.dp)) {
                Text("Mensagens encontradas", fontSize =  20.sp, modifier = Modifier.padding(bottom = 10.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(forbiddenMessages) {
                        MessageText(it)
                    }
                }
            }

        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Espião") {
        SpyApp()
    }
}
