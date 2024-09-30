package com.yemen_restaurant.greenland.shared

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.activities.CartActivity
import com.yemen_restaurant.greenland.activities.cartController3

class CCompose{

    @Composable
    fun topBar(topBarHeight: Dp,context: Context) {
        var size = cartController3.products.value.size + cartController3.offers.value.size
        Row (
            Modifier
                .fillMaxWidth()
                .height(topBarHeight)
                .background(MaterialTheme.colorScheme.primary),

            verticalAlignment= Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            BadgedBox(
                modifier = Modifier
                    .width(40.dp)
                    .clickable {
                        val intent = Intent(
                            context,
                            CartActivity::class.java
                        )
                        context.startActivity(intent)

                    },
                badge = {
                    Text(text = size.toString()) }) {
                Box(
                    Modifier.align(Alignment.BottomEnd)
                ) {

                    Icon(imageVector = Icons.Outlined.ShoppingCart, contentDescription = "" )
                }

            }


            Text(modifier =  Modifier.padding(5.dp) ,text = "مطعم الارض الخضراء",color = MaterialTheme.colorScheme.secondary)
            AsyncImage(modifier = Modifier.padding(14.dp) ,model = R.mipmap.ic_launcher_round, contentDescription ="" )
        }

    }


}