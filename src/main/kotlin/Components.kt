import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat

@Composable
fun MessageText(message: ChatMessage, formatter: SimpleDateFormat = SimpleDateFormat("HH:mm")) {
    Row(modifier = Modifier.padding(bottom = 5.dp).fillMaxWidth()) {
        Text("${message.user}:", fontWeight = FontWeight.Bold)
        Text(" ${message.message}")
        Text(
            " às ${formatter.format(message.createAt)}",
            fontSize = 10.sp,
            color = Color.LightGray,
            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 10.dp)
        )
    }
}

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