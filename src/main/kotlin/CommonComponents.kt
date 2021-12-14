
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageText(user: String, message: String, createdAt: Date? = null, formatter: SimpleDateFormat = SimpleDateFormat("HH:mm")) {
    Row(modifier = Modifier.padding(bottom = 5.dp).fillMaxWidth()) {
        Text("${user}:", fontWeight = FontWeight.Bold)
        Text(" ${message}")
        if (createdAt != null) {
            Text(
                " às ${formatter.format(createdAt)}",
                fontSize = 10.sp,
                color = Color.LightGray,
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 10.dp)
            )
        }
    }
}