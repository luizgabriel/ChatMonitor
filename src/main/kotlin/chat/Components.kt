package chat

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@Composable
fun MessageInput(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)?,
    onSendMessage: (message: String) -> Unit
) {
    var value by remember { mutableStateOf("") }

    fun sendMessage() {
        onSendMessage(value)
        value = ""
    }

    Row(modifier) {
        TextField(
            enabled = enabled,
            modifier = Modifier.fillMaxHeight().weight(1f),
            value = value,
            onValueChange = { value = it },
            placeholder = placeholder,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions { sendMessage() },
        )
        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = { sendMessage() },
            enabled = enabled && value.isNotBlank(),
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send"
            )
        }
    }
}