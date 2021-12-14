package chat
import MessageText
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Preview
fun ChatApp() {
    val (chat, setChat) = remember { mutableStateOf<ChatService?>(null) }
    val (name, setName) = remember { mutableStateOf("") }
    val (messages, setMessages) = remember { mutableStateOf(listOf<ChatMessage>()) }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val nameInputFocusRequester = remember { FocusRequester() }

    val isChatEnabled = chat != null && name.isNotBlank();

    val onSendMessage = { message: String ->
        coroutineScope.launch {
            val newMessage = chat?.sendMessage(name, message)
            if (newMessage != null) {
                setMessages(messages.toMutableList().let {
                    it.add(newMessage); it
                })
            }
        }
        Unit
    }

    LaunchedEffect(Unit) {
        TupleSpace.lookup()?.let { ChatService(it) }?.let(setChat)
    }

    LaunchedEffect(chat) {
        while (chat != null) {
            chat.readMessages().sortedBy { it.createdAt }.let(setMessages)
            delay(2000)
        }
    }

    LaunchedEffect(Unit) {
        nameInputFocusRequester.requestFocus()
    }

    DesktopMaterialTheme {
        Column {
            LazyColumn(modifier = Modifier.padding(10.dp).fillMaxWidth().weight(1.0f)) {
                items(messages) { message ->
                    MessageText(
                        user = message.user,
                        message = message.message,
                        createdAt = message.createdAt
                    )
                }
            }
            Row(modifier = Modifier.padding(10.dp).height(50.dp)) {
                TextField(
                    value = name,
                    modifier = Modifier.focusRequester(nameInputFocusRequester).height(50.dp),
                    onValueChange = setName,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                    ),
                    placeholder = {
                        Text("Digite seu nome")
                    })
                MessageInput(
                    enabled = isChatEnabled,
                    modifier = Modifier.height(50.dp).padding(start = 5.dp),
                    placeholder = { Text("Digite uma mensagem") },
                    onSendMessage = onSendMessage
                )
            }
        }

    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Chat") {
        ChatApp()
    }
}
