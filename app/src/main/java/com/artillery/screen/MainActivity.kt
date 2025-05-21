package com.artillery.screen

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.artillery.screen.ui.theme.SubtitleScreenTheme
import com.artillery.screen.weights.BaseFieldPlaceholder
import com.artillery.screen.weights.CaptionsCompose
import com.artillery.screen.weights.CaptionsDes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


sealed class ColorCombination(
    val frontColor: Color,
    val bgColor: Color,
    val des: String,
) {
    data object 警告: ColorCombination(frontColor = Color(0xFFFFFF00), bgColor = Color(0xFF000000), des = "警告")
    data object 信息屏: ColorCombination(frontColor = Color(0xFFFFFFFF), bgColor = Color(0xFF000080), des = "信息屏")
    data object 紧急: ColorCombination(frontColor = Color(0xFFFF0000), bgColor = Color(0xFFFFFFFF), des = "紧急")
    data object 安全: ColorCombination(frontColor = Color(0xFFFFA500), bgColor = Color(0xFF000000), des = "安全")
    data object 逃生: ColorCombination(frontColor = Color(0xFF00FF00), bgColor = Color(0xFF333333), des = "逃生")

}


data class MainState(

    val listColor:List<ColorCombination> = listOf(
        ColorCombination.警告,
        ColorCombination.信息屏,
        ColorCombination.紧急,
        ColorCombination.安全,
        ColorCombination.逃生
    ),
    val colorIndex: Int = 0,

    val model: CaptionsDes = CaptionsDes(
        text = "关闭远光灯",
        interval = 1000L,
        style = TextStyle(
            color = ColorCombination.警告.frontColor,
            fontSize = 150.sp,
            fontWeight = FontWeight.Bold
        ),
        bgColor = ColorCombination.警告.bgColor
    ),
    val show: Boolean = false,
    val requestedOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
    val showOver: Boolean = false,


    val showPreview: Boolean = false,
    val fontSize: Float = 150f

) : MavericksState


class MainViewModel(state: MainState) : MavericksViewModel<MainState>(state) {


    /**
     * 横屏显示
     */
    fun landscapeScreen() {
        setState {
            copy(requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }

    }

    /**
     * 竖屏显示
     */
    fun portraitScreen() {
        setState {
            copy(requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
    }

    fun inputText(text: String) {
        setState { copy(model = model.copy(text = text)) }
    }

    fun onTouch() {
        viewModelScope.launch(Dispatchers.IO) {

            setState {
                copy(
                    showOver = true
                )
            }

            delay(3000L)

            setState {
                copy(
                    showOver = false
                )
            }
        }
    }


    fun onStartShow() {
        withState { state ->
            if (state.model.text.isEmpty()){
                return@withState
            }
            setState { copy(show = true) }
        }
    }


    fun onEndShow() {
        setState { copy(show = false) }
    }

    fun selectColorByIndex(index: Int) {
        setState { copy(colorIndex = index) }

        withState { state ->
            val combination = state.listColor[state.colorIndex]

            val tempModel = state.model.copy(
                style = state.model.style.copy(
                    color = combination.frontColor
                ),
                bgColor = combination.bgColor,
            )
            setState {
                copy(model = tempModel)
            }

        }

    }

    fun onStartPreview() {
        setState { copy(showPreview = true)  }
    }

    fun onHidePreview() {
        setState { copy(showPreview = false)  }
    }

    fun onFontChange(value: Float) {
        setState {
            val tempModel = model.copy(
                style = model.style.copy(
                    fontSize = value.sp
                )
            )
            copy(fontSize = value, model = tempModel)
        }
    }


}


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val mViewModel: MainViewModel = mavericksViewModel()

            val context = LocalContext.current

            SubtitleScreenTheme(
                darkTheme = true
            ) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                ) { innerPadding ->

                    val showPreview by mViewModel.collectAsState(MainState::showPreview)
                    if (showPreview){

                        val boxWidth = with(LocalDensity.current){
                            (context.resources.displayMetrics.heightPixels).toDp()
                        }

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val model by mViewModel.collectAsState(MainState::model)
                            CaptionsCompose(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .wrapContentWidth(),
                                model = model
                            )

                            Text(
                                text = "结束预览",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        mViewModel.onHidePreview()
                                    }
                                    .background(Color.LightGray)
                                    .padding(vertical = 12.dp, horizontal = 24.dp)
                            )


                            val fontSize by mViewModel.collectAsState(MainState::fontSize)

                            Slider(
                                value = fontSize,
                                onValueChange = {
                                    mViewModel.onFontChange(value = it)
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.secondary,
                                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                                ),
                                steps = 200,
                                valueRange = 80f..500f,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .width(boxWidth)
                                    .rotate(-90f)
                            )
                        }

                        LaunchedEffect(showPreview) {
                            mViewModel.landscapeScreen()
                        }

                    }else {
                        val show by mViewModel.collectAsState(MainState::show)
                        if (show) {
                            val model by mViewModel.collectAsState(MainState::model)
                            Box(
                                modifier = Modifier
                                    .background(model.bgColor)
                                    .clickable {
                                        mViewModel.onTouch()
                                    }
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CaptionsCompose(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .wrapContentWidth(),
                                    model = model
                                )

                                val showOver by mViewModel.collectAsState(MainState::showOver)
                                if (showOver) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "结束",
                                            style = TextStyle(
                                                color = Color.White,
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Normal,
                                                textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth(0.5f)
                                                .clip(CircleShape)
                                                .background(Color.LightGray)
                                                .clickable {
                                                    mViewModel.onEndShow()
                                                }
                                                .padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .padding(vertical = 10.dp),
                            ) {

                                val model by mViewModel.collectAsState(MainState::model)

                                BaseFieldPlaceholder(
                                    value = model.text,
                                    onValueChange = { str ->
                                        mViewModel.inputText(text = str)
                                    },
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    placeholder = {
                                        Text(
                                            text = "请输入需要显示的文字",
                                            style = TextStyle(
                                                color = Color.White.copy(alpha = 0.3f),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .padding(vertical = 8.dp, horizontal = 10.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(
                                            width = 1.dp,
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                )

                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    maxItemsInEachRow = 3
                                ) {
                                    val listColor by mViewModel.collectAsState(MainState::listColor)
                                    val colorIndex by mViewModel.collectAsState(MainState::colorIndex)
                                    listColor.forEachIndexed { index, combination ->
                                        CheckTextCompose(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    mViewModel.selectColorByIndex(index = index)
                                                },
                                            selected = index == colorIndex,
                                            des = combination
                                        )
                                    }
                                    val size = listColor.size
                                    val repeatCount = size % 3
                                    repeat(repeatCount){
                                        Spacer(
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }


                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )


                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "预览",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Normal,
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth(0.5f)
                                            .clip(CircleShape)
                                            .background(Color.LightGray)
                                            .clickable {
                                                mViewModel.onStartPreview()
                                            }
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp)
                                    )
                                }

                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                )

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "开始",
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Normal,
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth(0.5f)
                                            .clip(CircleShape)
                                            .background(Color.LightGray)
                                            .clickable {
                                                mViewModel.onStartShow()
                                            }
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp)
                                    )
                                }


                            }
                        }

                        LaunchedEffect(show) {
                            if (show) {
                                mViewModel.landscapeScreen()
                            } else {
                                mViewModel.portraitScreen()
                            }
                        }
                    }
                }
            }

            val requestedOrientation by mViewModel.collectAsState(MainState::requestedOrientation)
            LaunchedEffect(requestedOrientation) {
                (context as Activity).requestedOrientation = requestedOrientation
            }
        }
    }
}


@Composable
fun CheckTextCompose(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    des: ColorCombination
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = if (selected) {
                painterResource(id = R.drawable.icon_checked_48)
            }else {
                painterResource(id = R.drawable.icon_unchecked_48)
            },
            contentDescription = ""
        )

        Text(
            text = des.des,
            style = TextStyle(
                color = des.frontColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(6.dp))
                .background(color = des.bgColor)
                .padding(vertical = 8.dp, horizontal = 4.dp)
        )
    }
}






