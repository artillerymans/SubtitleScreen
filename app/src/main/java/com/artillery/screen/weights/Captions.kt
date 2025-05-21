package com.artillery.screen.weights

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.artillery.screen.state.rememberMutableStateOf
import kotlinx.coroutines.delay

/*
 * 日期: 2025年05月20日 17:42:36
 * 作者: zhiwei.zhu
 * 描述: 
 */

data class CaptionsDes(
    val text: String = "",
    val interval: Long = 2000L,
    val style: TextStyle = TextStyle(
        color = Color.White,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold
    ),
    val bgColor: Color = Color.Black
)


@Composable
fun CaptionsCompose(
    modifier: Modifier = Modifier,
    model: CaptionsDes
) {

    var textState by rememberMutableStateOf("")

    Text(
        text = textState,
        style = model.style,
        modifier = modifier
    )

    LaunchedEffect(model) {
        val list = model.text.chunked(2)
        if (list.isNotEmpty()){
            val lastIndex = list.lastIndex
            var index = 0
            while (true){
                if (index > lastIndex){
                    index = 0
                }
                textState = list[index]
                index +=1
                delay(model.interval)
            }
        }
    }

}