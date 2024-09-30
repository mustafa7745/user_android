package com.yemen_restaurant.greenland.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.yemen_restaurant.greenland.CustomImageView
import com.yemen_restaurant.greenland.MainCompose2
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.ProductModel
import com.yemen_restaurant.greenland.shared.CartController3
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MultipartBody


class SearchActivity : ComponentActivity() {
    val stateController = StateController()
    private val requestServer = RequestServer(this)
    private val searchText = mutableStateOf("")
    private val isSearched = mutableStateOf(false)
    val products = mutableStateOf<List<ProductModel>>(listOf())
    val isShow = mutableStateOf(false)
    lateinit var groupId:String


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenlandRestaurantTheme {
                val topBarHeight = 70.dp
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                           cCompose.topBar(topBarHeight =topBarHeight ,this)
                    },
                    content = {
                        MainCompose2(padding = topBarHeight, stateController = stateController, activity =this ) {
                            Column (
                                modifier = Modifier
                                    .fillMaxWidth()
//                                    .animatedBorder(
//                                        borderColors = listOf(Color.Red, Color.Green, Color.Blue),
//                                        backgroundColor = Color.White,
//                                        shape = RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp),
//                                        borderWidth = 4.dp,
//                                        animationDurationInMillis = 5000
//                                    ),
                            ){
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                    , verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextField(
                                        label = {
                                          Text(text = "ابحث هنا")
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Search,
                                            keyboardType = KeyboardType.Text
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onSearch = {
                                                search()
                                            }
                                        ),
                                        trailingIcon = {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .clickable {
                                                        if (searchText.value.isNotEmpty()) search()
                                                    }
                                                    .size(50.dp)
                                                    .width(20.dp)
                                                    .padding(10.dp), model = R.drawable.search, contentDescription = null )
                                        },
                                        value = searchText.value, onValueChange = {
                                        searchText.value = it
                                    })
                                }
                            }
                            ProductsCompose(cart = cartController3)
                            if (isShow.value) {
                                modalList(cartController3)
                            }
                        }
                    },
                )
            }
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ProductsCompose(cart: CartController3) {
        val newList = products.value.groupBy { it.products_groupsName }

        val newList2 = arrayListOf<ProductModel>()
        newList.forEach {
            if (it.key == "الرئيسية") {
                newList2.addAll(it.value)
            } else {
                newList2.add(it.value.first())
            }
        }
        LazyVerticalGrid(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(),
            columns = GridCells.Fixed(2),
            content = {
                itemsIndexed(newList2) { index, s ->
                    Card(
                        Modifier
                            .width(200.dp)
                            .padding(5.dp)

                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
//                                .background(MaterialTheme.colorScheme.primary),
                        ) {
                            Text(
                                modifier = Modifier.padding(2.dp),
                                text = s.name,
                                fontSize = 12.sp,
                                color = Color.Black,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(1.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = s.postPrice + " ريال ", fontSize = 14.sp, color = Color.Black)
                                if (s.products_groupsName != "الرئيسية")
                                    Column(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(35.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {



                                        Card (
                                            Modifier
                                                .fillMaxWidth()

                                                .padding(5.dp)
                                        ){
                                            Box(modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.White)
                                                .clickable {
                                                    isShow.value = true
                                                    groupId = s.products_groupsId
                                                } ,contentAlignment = Alignment.Center,){
                                                Text(
                                                    text = "الانواع",
                                                    fontSize = 12.sp,
                                                )
                                            }


                                        }

//                                    LazyRow(
//                                        Modifier
//                                            .fillMaxWidth()
//                                            .padding(5.dp),
//                                        horizontalArrangement = Arrangement.Center,
//                                        content = {
//                                            items(s.productImages.size) {
//                                                if (pagerState.currentPage == it)
//                                                    Icon(
//                                                        modifier = Modifier.size(10.dp),
//                                                        painter = painterResource(R.drawable.baseline_filled_circle_24),
//                                                        contentDescription = "",
//                                                        tint = MaterialTheme.colorScheme.background
//                                                    )
//                                                else {
//                                                    Icon(
//                                                        modifier = Modifier.size(10.dp),
//                                                        painter = painterResource(R.drawable.outline_circle_24),
//                                                        contentDescription = "",
//                                                        tint = MaterialTheme.colorScheme.background
//                                                    )
//                                                }
//                                            }
//
//                                        })
                                    }
                            }
                            HorizontalDivider(Modifier.padding(5.dp))
                            Card(
                                Modifier
                                    .fillMaxWidth()
                                    .height(33.dp)
                                    .padding(5.dp)

                            ) {
                                if (s.isAvailable == "0") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()

                                            .background(Color.Red),
                                        contentAlignment = Alignment.Center,
                                    )

                                    {
                                        Text(
                                            text = "تم ايقافه مؤقتا",
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                    }
                                } else {
                                    val foundItem =
                                        cart.products.value.find { it.productsModel == s }
                                    if (foundItem == null) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.White)
                                                .clickable {
                                                    cartController3.addProduct((s))
                                                },
                                            contentAlignment = Alignment.Center,
                                        )

                                        {
                                            Text(text = "اضافة الى السلة", fontSize = 12.sp)
                                        }
                                    } else {
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .background(Color.White),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(onClick = {
                                                cartController3.incrementProductQuantity(s.id)
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Add,
                                                    contentDescription = ""
                                                )
                                            }
                                            Text(text = foundItem.productCount.value.toString())
                                            IconButton(onClick = {


                                                cartController3.decrementProductQuantity(s.id)
                                            }) {
                                                Icon(
                                                    painter = painterResource(
                                                        R.drawable.baseline_remove_24
                                                    ),
                                                    contentDescription = ""
                                                )

                                                //                                                                            Icon(imageVector = R.drawable.ic_launcher_background, contentDescription = "" )
                                            }
                                            IconButton(onClick = {


                                                cartController3.removeProduct(s.id)
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = ""
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            val pagerState =
                                rememberPagerState(pageCount = { s.productImages.size })
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                            ) {
                                if (s.productImages.isEmpty())
                                    Text(modifier = Modifier.align(Alignment.Center), text = "لايوجد صور لهذا الصنف" , fontSize = 8.sp)
                                else
                                HorizontalPager(
                                    pagerState,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) { i ->
                                    Card(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(5.dp),
                                        colors = CardColors(
                                            containerColor = Color.White,
                                            contentColor = Color.Black,
                                            disabledContainerColor = Color.Blue,
                                            disabledContentColor = Color.Cyan
                                        )
                                    ) {
                                        CustomImageView(
                                            context = this@SearchActivity,
                                            imageUrl = s.productImages[i].image,
                                            okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                        )
                                    }

                                }
                            }


                        }
                    }
                }

            })
    }
    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun modalList(
        cart: CartController3
    ) {
        val modalList =
            products.value.filter { it.products_groupsId == groupId }
        Dialog(onDismissRequest = {
            isShow.value = false
        }) {
            LazyVerticalGrid(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(2),
                content = {
                    itemsIndexed(modalList) { index1, s1 ->
                        Column {
                            Card(
                                Modifier
                                    .width(200.dp)
                                    .padding(5.dp)

                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(30.dp)
                                            .background(MaterialTheme.colorScheme.primary),
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                        ) {
                                            Text(text = "السعر: ")
                                            Text(text = s1.postPrice)

                                        }
                                    }

                                    val pagerState =
                                        rememberPagerState(pageCount = { s1.productImages.size })
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(170.dp)
                                    ) {
                                        if (s1.productImages.isEmpty())
                                            Text(modifier = Modifier.align(Alignment.Center), text = "لايوجد صور لهذا الصنف" , fontSize = 8.sp)
                                        else
                                        HorizontalPager(
                                            pagerState,
                                            modifier = Modifier.fillMaxSize()
                                        ) { i ->
                                            Card(
                                                Modifier
                                                    .fillMaxSize(),
                                                colors = CardColors(
                                                    containerColor = Color.White,
                                                    contentColor = Color.Black,
                                                    disabledContainerColor = Color.Blue,
                                                    disabledContentColor = Color.Cyan
                                                )
                                            ) {
                                                CustomImageView(
                                                    context = this@SearchActivity,
                                                    imageUrl = s1.productImages[i].image,
                                                    okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                                )
                                            }

                                        }
                                    }


                                    Column(
                                        Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.primary),

                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
//                                        LazyRow(
//                                            Modifier
//                                                .fillMaxWidth()
//                                                .padding(5.dp),
//                                            horizontalArrangement = Arrangement.Center,
//                                            content = {
//                                                items(s1.productImages.size) {
//
//
//                                                    if (pagerState.currentPage == it)
//                                                        Icon(
//                                                            modifier = Modifier.size(10.dp),
//                                                            painter = painterResource(R.drawable.baseline_filled_circle_24),
//                                                            contentDescription = "",
//                                                            tint = MaterialTheme.colorScheme.background
//                                                        )
//                                                    else {
//                                                        Icon(
//                                                            modifier = Modifier.size(10.dp),
//                                                            painter = painterResource(R.drawable.outline_circle_24),
//                                                            contentDescription = "",
//                                                            tint = MaterialTheme.colorScheme.background
//                                                        )
//                                                    }
//                                                }
//                                            })
                                        HorizontalDivider(
                                            Modifier.padding(5.dp)
                                        )
                                        Card(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(35.dp)

                                        ) {
                                            if (s1.isAvailable == "0") {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(Color.Red),
                                                    contentAlignment = Alignment.Center,
                                                )

                                                {
                                                    Text(
                                                        text = "تم ايقافه مؤقتا",
                                                        fontSize = 12.sp,
                                                        color = Color.White
                                                    )
                                                }
                                            } else {
                                                val foundItem =
                                                    cart.products.value.find { it.productsModel == s1 }
                                                if (foundItem == null) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(Color.White)
                                                            .padding(5.dp)
                                                            .clickable {
                                                                cartController3.addProduct((s1))
                                                            },
                                                        contentAlignment = Alignment.Center,
                                                    )

                                                    {
                                                        Text(
                                                            text = "اضافة الى السلة",
                                                            fontSize = 12.sp
                                                        )
                                                    }
                                                } else {
                                                    Row(
                                                        Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.Center,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        IconButton(onClick = {
                                                            cartController3.incrementProductQuantity(
                                                                s1.id
                                                            )
                                                        }) {
                                                            Icon(
                                                                imageVector = Icons.Outlined.Add,
                                                                contentDescription = ""
                                                            )
                                                        }
                                                        Text(text = foundItem.productCount.value.toString())
                                                        IconButton(onClick = {


                                                            cartController3.decrementProductQuantity(
                                                                s1.id
                                                            )
                                                        }) {
                                                            Icon(
                                                                painter = painterResource(
                                                                    R.drawable.baseline_remove_24
                                                                ),
                                                                contentDescription = ""
                                                            )

                                                            //                                                                            Icon(imageVector = R.drawable.ic_launcher_background, contentDescription = "" )
                                                        }
                                                        IconButton(onClick = {


                                                            cartController3.removeProduct(s1.id)
                                                        }) {
                                                            Icon(
                                                                imageVector = Icons.Outlined.Delete,
                                                                contentDescription = ""
                                                            )
                                                        }
                                                    }
                                                }
                                            }
//                                            val foundItem = cart.products.value .find { it.productsModel == s1 }
//                                            if (foundItem == null)
//                                                Box(modifier = Modifier
//                                                    .fillMaxSize()
//                                                    .clickable {
//                                                        cartController3.addProduct(s1)
//                                                    } ,contentAlignment = Alignment.Center,)
//
//                                                {
//                                                    Text(text = "اضافة الى السلة", fontSize = 12.sp)
//                                                }
//                                            else{
//                                                Row(
//                                                    Modifier.fillMaxWidth(),
//                                                    horizontalArrangement = Arrangement.Center,
//                                                    verticalAlignment = Alignment.CenterVertically
//                                                ) {
//                                                    IconButton(onClick = {
//                                                        cartController3.incrementProductQuantity(s1.id)
//                                                    }) {
//                                                        Icon(
//                                                            imageVector = Icons.Outlined.Add,
//                                                            contentDescription = ""
//                                                        )
//                                                    }
//                                                    Text(text = foundItem.productCount.value.toString())
//                                                    IconButton(onClick = {
//
//
//                                                        cartController3.decrementProductQuantity(s1.id)
//                                                    }) {
//                                                        Icon(
//                                                            painter = painterResource(
//                                                                R.drawable.baseline_remove_24
//                                                            ),
//                                                            contentDescription = ""
//                                                        )
//
//                                                        //                                                                            Icon(imageVector = R.drawable.ic_launcher_background, contentDescription = "" )
//                                                    }
//                                                    IconButton(onClick = {
//
//
//                                                        cartController3.removeProduct(s1.id)
//                                                    }) {
//                                                        Icon(
//                                                            imageVector = Icons.Outlined.Delete,
//                                                            contentDescription = ""
//                                                        )
//                                                    }
//                                                }
//                                            }

                                        }
                                        HorizontalDivider(Modifier.padding(5.dp))
                                        Text(
                                            modifier = Modifier.padding(5.dp),
                                            text = s1.name,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1
                                        )
                                    }
                                }

                            }
//                            Card(
//                                Modifier.padding(5.dp)
//                            ) {
//
//                                if (s1.productImages.isEmpty()) {
//
//                                    Card(
//                                        Modifier
//                                            .fillMaxSize()
//                                            .height(200.dp)
//                                            .padding(5.dp),
//                                        colors = CardColors(
//                                            containerColor = Color.White,
//                                            contentColor = Color.Black,
//                                            disabledContainerColor = Color.Blue,
//                                            disabledContentColor = Color.Cyan
//                                        )
//                                    ) {
//                                        Text(text = "لا يوجد صور")
//                                    }
//
//                                } else {
//                                    val pagerState =
//                                        rememberPagerState(
//                                            pageCount = { s1.productImages.size })
//                                    HorizontalPager(
//                                        pagerState,
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .height(200.dp),
//                                    ) { i ->
//                                        Card(
//                                            Modifier
//                                                .fillMaxSize()
//                                                .padding(5.dp),
//                                            colors = CardColors(
//                                                containerColor = Color.White,
//                                                contentColor = Color.Black,
//                                                disabledContainerColor = Color.Blue,
//                                                disabledContentColor = Color.Cyan
//                                            )
//                                        ) {
//                                            Column(
//                                                Modifier.fillMaxSize(),
//                                                verticalArrangement = Arrangement.Center,
//                                                horizontalAlignment = Alignment.CenterHorizontally
//
//                                            ) {
//                                                AsyncImage(
//                                                    modifier = Modifier.padding(
//                                                        10.dp
//                                                    ),
//                                                    model = s1.productImages[i].image,
//                                                    contentDescription = ""
//                                                )
//                                            }
//                                        }
//                                    }
//                                    LazyRow(
//                                        Modifier.fillMaxWidth(),
//                                        horizontalArrangement = Arrangement.Center,
//
//                                        content = {
//                                            items(s1.productImages.size) {
//                                                if (pagerState.currentPage == it)
//                                                    Icon(
//                                                        painter = painterResource(
//                                                            R.drawable.baseline_filled_circle_24
//                                                        ),
//                                                        contentDescription = "",
//                                                        tint = MaterialTheme.colorScheme.primary
//                                                    )
//                                                else {
//                                                    Icon(
//                                                        painter = painterResource(
//                                                            R.drawable.outline_circle_24
//                                                        ),
//                                                        contentDescription = "",
//                                                        tint = MaterialTheme.colorScheme.primary
//                                                    )
//                                                }
//                                            }
//                                        })
//                                }
//
//
//
//                                HorizontalDivider()
//                                Column(
//                                    verticalArrangement = Arrangement.Center
//                                ) {
//                                    Row(
//                                        Modifier.fillMaxWidth(),
//                                        horizontalArrangement = Arrangement.SpaceBetween,
//                                    ) {
//                                        Text(text = s1.name)
//                                    }
//
//
//                                    val foundItem1 =
//                                        cart.find { it.productsModel == s1 }
//                                    if (foundItem1 != null) {
//
//                                        Card(
//                                            Modifier.fillMaxWidth()
//                                        ) {
//                                            Row(
//                                                Modifier.fillMaxWidth(),
//                                                horizontalArrangement = Arrangement.Center,
//                                                verticalAlignment = Alignment.CenterVertically
//                                            ) {
//                                                IconButton(
//                                                    onClick = {
//                                                        cartController.increaseProductCountInCart(
//                                                            s1
//                                                        )
//                                                    }) {
//                                                    Icon(
//                                                        imageVector = Icons.Outlined.Add,
//                                                        contentDescription = ""
//                                                    )
//                                                }
//                                                Text(text = foundItem1.product_count.value.toString())
//                                                IconButton(
//                                                    onClick = {
//
//
//                                                        cartController.decreaseProductCountInCart(
//                                                            s1
//                                                        )
//                                                    }) {
//                                                    Icon(
//                                                        painter = painterResource(
//                                                            R.drawable.baseline_remove_24
//                                                        ),
//                                                        contentDescription = ""
//                                                    )
//
//    //                                                                            Icon(imageVector = R.drawable.ic_launcher_background, contentDescription = "" )
//                                                }
//                                                IconButton(
//                                                    onClick = {
//
//
//                                                        cartController.deleteItem(
//                                                            s1
//                                                        )
//                                                    }) {
//                                                    Icon(
//                                                        imageVector = Icons.Outlined.Delete,
//                                                        contentDescription = ""
//                                                    )
//                                                }
//                                            }
//                                        }
//
//
//                                    } else {
//                                        Button(
//                                            onClick = {
//                                                cartController.addToCart(
//                                                    s1
//                                                )
//                                            }) {
//                                            Text(text = "اضافة الى السلة")
//                                        }
//
//                                    }
//
//                                }
//
//
//                            }
                        }
                    }
                }
            )

        }
    }

    private fun search() {
        stateController.isErrorAUD.value = false
        stateController.isLoadingAUD.value = true
        stateController.errorAUD.value = ""
        val data3 = buildJsonObject {
            put("tag", "search")
            put("inputProductName", searchText.value)
        }
        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString())
            .addFormDataPart("data2", requestServer.getData2())
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.productsUrl, { code, it ->

            stateController.isLoadingAUD.value = false
            stateController.isErrorAUD.value = true
            stateController.errorAUD.value = it
        }) {
            try {
                stateController.isLoadingAUD.value = false
                products.value =  MyJson.IgnoreUnknownKeys.decodeFromString(it)
            } catch (e: Exception) {
                stateController.isLoadingAUD.value = false
                stateController.isErrorAUD.value = true
                stateController.errorAUD.value = e.message.toString()

            }
        }


    }

}