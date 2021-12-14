package spy

import MessageText
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import chat.ChatMessage
import chat.ChatService
import chat.TupleSpace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*


@Composable
@Preview
fun SpyApp() {
    val (chat, setChat) = remember { mutableStateOf<ChatService?>(null) }
    val (forbiddenList, setForbiddenList) = remember { mutableStateOf("") }
    val (forbiddenMessages, setForbiddenMessages) = remember { mutableStateOf(listOf<ChatMessage>()) }
    val focusRequester = remember { FocusRequester() }
    val (spy, setSpy) = remember { mutableStateOf<Spy?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            TupleSpace.lookup()?.let { ChatService(it) }?.let(setChat)
            runCatching { setSpy(Spy(lookupForbiddenMessagesService())) }.exceptionOrNull()
                ?.printStackTrace(System.err)
        }
    }

    LaunchedEffect(chat, forbiddenList) {
        val forbiddenSet =
            forbiddenList
                .split(Regex("[\\n,]"))
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .filter { it.length > 3 }
                .map { it.lowercase(Locale.getDefault()) }
                .toHashSet()

        while (chat != null && forbiddenSet.isNotEmpty()) {
            chat.readMessages()
                .filter { chatMessage -> forbiddenSet.any { chatMessage.message.lowercase(Locale.getDefault()).contains(it) } }
                .let { messages ->
                    setForbiddenMessages(messages)
                    spy?.sendMessages(messages)
                }
            delay(2000)
        }
    }

    LaunchedEffect(spy) {
        spy?.syncMessages()
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
                Text("Mensagens encontradas", fontSize = 20.sp, modifier = Modifier.padding(bottom = 10.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(forbiddenMessages) {
                        MessageText(
                            user = it.user,
                            message = it.message,
                            createdAt = it.createdAt
                        )
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
