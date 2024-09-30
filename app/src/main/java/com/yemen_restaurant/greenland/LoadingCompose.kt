package com.yemen_restaurant.greenland

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun LoadingCompose(
    modifier: Modifier = Modifier,
    circleSize: Dp = 12.dp,
    circleColor: Color = MaterialTheme.colorScheme.primary,
    spaceBetween: Dp = 10.dp,
    travelDistance: Dp = 20.dp
) {

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    Modifier
                        .height(170.dp)
                        .width(170.dp).padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "جاري المعالجة", fontSize = 10.sp)
                    HorizontalDivider(Modifier.padding(10.dp))
                    AsyncImage(
                        modifier = Modifier .clip(RoundedCornerShape(15.dp))
                            .shimmerEffect(),
                        model = R.drawable.logo, contentDescription = "loading image",
                        contentScale = ContentScale.Inside
                    )
                }
            }



//        ImageView2(model =  model)
    }
//    val circles = listOf(
//        remember { Animatable(initialValue = 0f) },
//        remember { Animatable(initialValue = 0f) },
//        remember { Animatable(initialValue = 0f) }
//    )
//
//    circles.forEachIndexed { index, animatable ->
//        LaunchedEffect(key1 = animatable) {
//            delay(index * 100L)
//            animatable.animateTo(
//                targetValue = 1f,
//                animationSpec = infiniteRepeatable(
//                    animation = keyframes {
//                        durationMillis = 1200
//                        0.0f at 0 with LinearOutSlowInEasing
//                        1.0f at 300 with LinearOutSlowInEasing
//                        0.0f at 600 with LinearOutSlowInEasing
//                        0.0f at 1200 with LinearOutSlowInEasing
//                    },
//                    repeatMode = RepeatMode.Restart
//                )
//            )
//        }
//    }
//
//    val circleValues = circles.map { it.value }
//    val distance = with(LocalDensity.current) { travelDistance.toPx() }
//
//    Row(
//        modifier = modifier,
//        horizontalArrangement = Arrangement.spacedBy(spaceBetween)
//    ) {
//        circleValues.forEach { value ->
//            Box(
//                modifier = Modifier
//                    .size(circleSize)
//                    .graphicsLayer {
//                        translationY = -value * distance
//                    }
//                    .background(
//                        color = circleColor,
//                        shape = CircleShape
//                    )
//            )
//        }
//    }

}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFEEEDED),
                Color(0xFFE6E4E4),
                Color(0xFFF7EEEE),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}