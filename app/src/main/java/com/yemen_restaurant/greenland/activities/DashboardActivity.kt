package com.yemen_restaurant.greenland.activities

import GetStorage
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.yemen_restaurant.greenland.CustomImageView
import com.yemen_restaurant.greenland.LoadingCompose
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.CategoryModel
import com.yemen_restaurant.greenland.models.CategoryUDModel
import com.yemen_restaurant.greenland.models.HomeComponent
import com.yemen_restaurant.greenland.models.ProjectAdsModel
import com.yemen_restaurant.greenland.shared.CartController3
import com.yemen_restaurant.greenland.shared.Login
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.ProductCategoryDBController
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.HomeComponentStorage
import com.yemen_restaurant.greenland.storage.UserStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.Duration
import java.time.LocalDateTime

val cartController3 = CartController3()

class DashboardActivity : ComponentActivity() {
    lateinit var homeComponent: HomeComponent
    val stateController = StateController()
    val currenctDate: LocalDateTime = LocalDateTime.now()
    val homeComponentStorage = HomeComponentStorage()

    val itemView = mutableStateOf(false)
    val itemType = mutableStateOf(0)


    val categories = mutableStateOf<List<CategoryModel>>(listOf())
    val projectAds = mutableStateOf<List<ProjectAdsModel>>(listOf())
    val isLoading = mutableStateOf(false)
    val isSuccess1 = mutableStateOf(false)
    val isSuccess2 = mutableStateOf(false)
    val requestServer = RequestServer(this)
    val catProController = ProductCategoryDBController(this)

    val isError = mutableStateOf(false)
    val error = mutableStateOf("")

    private fun read() {
        val userStorage = UserStorage()
        stateController.errorRead.value = ""
        stateController.isLoadingRead.value = true
        var data3: JsonObject

        val userName = GetStorage("user").getData("name")

        if (userStorage.isSetUser()) {
            data3 = buildJsonObject {
                put("tag", "read")
            }

//            val tokenInfo = requestServer.login.getLoginTokenWithDate();
//            Log.e("dateee", tokenInfo.expire_at)
//            val tokenDateExpireAt =
//                (LocalDateTime.parse(tokenInfo.expire_at.replace("\\s".toRegex(), "T")))
//            data3 = if (tokenDateExpireAt.isBefore(LocalDateTime.now())) {
//                buildJsonObject {
//                    put("tag", "read")
//                }
//            } else {
//                buildJsonObject {
//                    put("tag", "readWithUser")
//                }
//            }
        } else {

            data3 = buildJsonObject {
                put("tag", "readWithUser")
            }
        }
        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data1", requestServer.getData1().toString())
            .addFormDataPart("data2", requestServer.getData2())
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.homeUrl, { code, it ->
            stateController.isLoadingRead.value = false
            stateController.isErrorRead.value = true
            stateController.errorRead.value = it
        }) {
            try {

                val data = MyJson.IgnoreUnknownKeys.decodeFromString<HomeComponent>(it)
                if (data.user != null) {
                    userStorage.setUser(MyJson.MyJson.encodeToString(data.user))
                }else{
                    data.user = userStorage.getUser()
                }
                homeComponent = data
                homeComponentStorage.setHomeComponent(MyJson.MyJson.encodeToString(data))
                successState()
            } catch (e: Exception) {
                stateController.isLoadingRead.value = false
                stateController.isErrorRead.value = true
                stateController.errorRead.value = e.message.toString()
            }
        }
    }

    private fun successState() {
        stateController.isLoadingRead.value = false
        stateController.isSuccessRead.value = true
        stateController.isErrorRead.value = false
    }

    //    private fun read() {
//        error.value = ""
//        isLoading.value = true
//        val data3 =  buildJsonObject {
//            put("tag", "read")
//        }
//
//        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart("data3",data3.toString())
//            .build()
//
////        Log.e("data2",requestServer.getData2().toString())
//        Log.e("data3",data3.toString())
//        requestServer.request2(body1, Urls.categoryUrl,{ code, fail ->
//            isLoading.value = false
//            isError.value = true
//            error.value = code.toString()
//        },{
//            try {
//                val categories =  MyJson.MyJson.decodeFromString<List<CategoryModel>>(it)
//                Log.e("ddd", LocalDateTime.now().toString())
//                catProController.setCategoryTime(LocalDateTime.now().toString())
//                catProController.setCategories(categories)
//                this@DashboardActivity.categories.value = categories
//                isLoading.value = false
//                isSuccess2.value = true
//                Log.e("listt", categories.toString())
//            }
//            catch (e:Exception){
//                isLoading.value = false
//                isError.value = true
//                error.value = e.message.toString()
//            }
//        })
//    }
    private fun checkChanges() {

        val login = Login()
        error.value = ""
        isLoading.value = true
        val data3 = buildJsonObject {
            put("tag", "check")
            put("inputProjectId", login.getProjectId())
            put("ids", MyJson.MyJson.encodeToJsonElement(catProController.getCategoriesIds()))
            put("fromDate", catProController.getCategoryTime())
        }
        Log.e("pid", login.getProjectId())

        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data3", data3.toString())
            .build()

        Log.e("data3", data3.toString())
        GlobalScope.launch {
            val response = requestServer.request(body1, Urls.categoryUrl)
            if (requestServer.isHaveResponse(response)) {
                when (requestServer.getRequestCode(response)) {
                    200 -> {
                        val data = requestServer.getResponseBody(response)
                        try {
                            val categoriesUD = MyJson.MyJson.decodeFromString<CategoryUDModel>(data)
                            val cats = catProController.getCategories().toMutableList()
                            if (categoriesUD.updated.isNotEmpty()) {
                                categoriesUD.updated.forEach { c ->
                                    val item = cats.find { it.id == c.id }
                                    if (item != null) {
                                        cats[cats.indexOf(item)] = c
                                    } else {
                                        cats.add(c)
                                    }
                                }
                            }
                            if (categoriesUD.deleted.isNotEmpty()) {
                                categoriesUD.deleted.forEach { c ->
                                    cats.remove(cats.find { it.id == c })
                                }
                            }

                            Log.e("ddd", LocalDateTime.now().toString())
                            catProController.setCategoryTime(LocalDateTime.now().toString())
                            catProController.setCategories(cats)
                            this@DashboardActivity.categories.value = cats
                            isLoading.value = false
                            isSuccess2.value = true
                            Log.e("listt", categories.toString())

                        } catch (e: Exception) {
                            isLoading.value = false
                            isError.value = true
                            error.value = "not json"
                            Log.e("Errorsuccess", data)

                        }
                    }

                    400 -> {
                        isLoading.value = false
                        isError.value = true
                        error.value = requestServer.getErrorMessage(response).message.en
                        Log.e(
                            "error",
                            response.toString()
                        )
                        Log.e(
                            "error",
                            requestServer.getErrorMessage(response).message.ar
                        )
                    }

                    else -> {
                        isLoading.value = false
                        isError.value = true
                        error.value =
                            "not 400 madry " + requestServer.getErrorMessage(response).message.en
                        Log.e("errorr", response.toString())
                    }
                }
            } else {
                isLoading.value = false
                isError.value = true
                error.value = requestServer.getErrorMessage(response).message.en
            }


        }
    }





    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (homeComponentStorage.isSetHomeComponent()){
            val diff = Duration.between(homeComponentStorage.getDate(),currenctDate).toMinutes()
            if (diff>1){
                read()
            }
            else{
                homeComponent = homeComponentStorage.getHomeComponent()
                successState()

            }

        }else{
            read()
        }


//        if (catProController.isSetCategoryTime()){
//            Log.e("lisstts ids",catProController.getCategoriesIds().toString())
//            val date = LocalDateTime.parse(catProController.getCategoryTime())
//            Log.e("dattt",date.toString())
//            
//            val diff = Duration.between(date,currenctDate).toMinutes()
//            Log.e("diff",diff.toString())
//            if (diff < 1){
//                categories.value = catProController.getCategories()
//                isSuccess2.value = true
//            }
//            else{
//                checkChanges()
//            }
//        }else{
//            read()
//        }
        setContent {
            GreenlandRestaurantTheme {
                val navController = rememberNavController()
                val topBarHeight = 70.dp
                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
//                           cCompose.topBar(topBarHeight = topBarHeight,this)
                    },
                    content = {


                        NavHost(navController = navController, startDestination = "home") {
                            composable("home") {
                                MainCompose {
                                    CategoriesComponent()
                                }
                            }
                            composable("settings") {
                                SettingsCompose()
                            }
                            composable("cart") {
                                CartCompose()
                            }
                        }
                    },
                )
            }
        }
    }

    @Composable
    private fun CategoriesComponent() {

        var count = 3
        if (homeComponent.ads.isNotEmpty()) count++
        if (homeComponent.offers.isNotEmpty()) count++
        if (homeComponent.discounts.isNotEmpty()) count++

        if (homeComponent.user != null)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animatedBorder(
                        borderColors = listOf(Color.Red, Color.Green, Color.Blue),
                        backgroundColor = Color.White,
                        shape = RoundedCornerShape(0.dp, 0.dp, 15.dp, 15.dp),
                        borderWidth = 4.dp,
                        animationDurationInMillis = 5000
                    ),
            ) {
                Row(

                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(10.dp), model = R.drawable.user, contentDescription = null
                    )

                    Text(modifier = Modifier.padding(3.dp), text = "مرحبا بك: ")
                    Text(text = homeComponent.user!!.name)
                }
                HorizontalDivider()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count), content = {
                        item {
                            Column(
                                Modifier
                                    .padding(3.dp)
                                    .width(20.dp)
                                    .clickable {
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            CartActivity::class.java
                                        )
                                        startActivity(intent)

                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Row(

                                    Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    BadgedBox(
                                        modifier = Modifier.fillMaxWidth(),
                                        badge = {
                                            Text(text = (cartController3.products.value.size + cartController3.offers.value.size).toString())
                                        }) {
                                        Box(
                                            Modifier.align(Alignment.Center)
                                        ) {


                                            AsyncImage(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .padding(10.dp),
                                                model = R.drawable.shopping_cart_svgrepo_com,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }


                                Text(text = "السلة", fontSize = 10.sp)
                            }
                        }
                        item {
                            Column(
                                Modifier
                                    .padding(3.dp)
                                    .width(20.dp)
                                    .clickable {
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            OrdersActivity::class.java
                                        )
                                        startActivity(intent)

                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(10.dp),
                                    model = R.drawable.orders,
                                    contentDescription = null
                                )
                                Text(text = "الطلبات", fontSize = 10.sp)
                            }
                        }
                        item {
                            Column(
                                Modifier
                                    .padding(3.dp)
                                    .width(20.dp)
                                    .clickable {
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            SearchActivity::class.java
                                        )
                                        startActivity(intent)

                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(10.dp),
                                    model = R.drawable.search,
                                    contentDescription = null
                                )
                                Text(text = "بحث", fontSize = 10.sp)
                            }
                        }
                        if (homeComponent.offers.isNotEmpty())
                            item {
                                Column(
                                    Modifier
                                        .padding(3.dp)
                                        .clickable {
                                            itemView.value = !itemView.value
                                            itemType.value = 1
                                        }
                                        .width(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(10.dp),
                                        model = R.drawable.offers,
                                        contentDescription = null
                                    )
                                    Text(text = "العروض", fontSize = 10.sp)
                                }
                            }
                        if (homeComponent.ads.isNotEmpty())
                            item {
                                Column(
                                    Modifier
                                        .padding(3.dp)
                                        .clickable {
                                            itemView.value = !itemView.value
                                            itemType.value = 2
                                        }
                                        .width(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(10.dp),
                                        model = R.drawable.ads_advertising_color_svgrepo_com,
                                        contentDescription = null
                                    )
                                    Text(text = "الاعلانات", fontSize = 10.sp)
                                }
                            }
                        if (homeComponent.discounts.isNotEmpty())
                            item {
                                Column(
                                    Modifier
                                        .padding(3.dp)
                                        .clickable {
                                            Log.e("discounts", homeComponent.discounts.toString())
                                        }
                                        .width(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(10.dp),
                                        model = R.drawable.discount_label_svgrepo_com,
                                        contentDescription = null
                                    )
                                    Text(text = "التخفيضات", fontSize = 10.sp)
                                }
                            }
                    })
            }

        if (itemView.value == true) {
            if (itemType.value == 1) {
                if (homeComponent.offers.isNotEmpty()) {
                    OffersComponents()
                }
            }
            if (itemType.value == 2) {
                if (homeComponent.ads.isNotEmpty())
                    AdsComponent()
            }
        }
        CategoriesComponents()
    }

    @Composable
    private fun CategoriesComponents() {
        Column {
            Text(text = "الاصناف")
            LazyVerticalGrid(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                columns = GridCells.Fixed(2),
                content = {
                    itemsIndexed(homeComponent.categories) { index, s ->

                        Card(
                            Modifier

                                .height(200.dp)
                                .width(200.dp)
                                .padding(5.dp)

                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color.White)
                                    .border(1.dp, Color.Black,RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {

                                        //                                                navHostController.navigate("products/${s.category_id}")
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            ProductsActivity::class.java
                                        )
                                        intent.putExtra("category_id", s.id)
                                        startActivity(intent)
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .align(Alignment.TopCenter)
                                ) {
//                                    GlideImage(model= s.category_image_path + s.image,contentDescription = null,loading= Placeholder,)
                                    //   AsyncImage(model = , contentDescription = )
                                    CustomImageView(context = this@DashboardActivity, imageUrl =s.category_image_path + s.image , okHttpClient = requestServer.createOkHttpClientWithCustomCert())

//                                    SubcomposeAsyncImage(
//                                        error = {
//                                            Column(
//                                                Modifier.fillMaxSize(),
//                                                horizontalAlignment = Alignment.CenterHorizontally,
//                                                verticalArrangement = Arrangement.Center
//                                            ) {
////                                                    IconButton(onClick = { /*TODO*/ }) {
////                                                        Icon(painter = , contentDescription = )
////                                                    }
//                                                Text(text = "خطأ في التحميل")
////                                                    Text(text = it.result.throwable.message.toString())
//                                            }
//                                        },
//                                        loading = {
//                                            Column(
//                                                Modifier.fillMaxSize(),
//                                                horizontalAlignment = Alignment.CenterHorizontally,
//                                                verticalArrangement = Arrangement.Center
//                                            ) {
//                                                LoadingCompose()
//                                            }
//                                        },
//                                        contentScale = ContentScale.Fit,
//                                        modifier = Modifier
//                                            .fillMaxSize(),
//                                        model = s.category_image_path + s.image,
//                                        contentDescription = "null",
//                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(30.dp)
                                        .align(Alignment.BottomCenter)
                                        .background(MaterialTheme.colorScheme.primary),
                                ) {

                                    Text(
                                        modifier = Modifier
                                            .align(Alignment.Center),
                                        text = s.name,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.secondary,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )

                                }
                            }


                        }

                    }
                })
        }

    }

    @Composable
    private fun OffersComponents() {
        Text(text = "العروض")
        HorizontalDivider()
        LazyHorizontalGrid(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .padding(5.dp),
            rows = GridCells.Fixed(1), content = {
                itemsIndexed(homeComponent.offers) { index, item ->
                    Column(
                        Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .animatedBorder(
                                    borderColors = listOf(Color.Green, Color.Blue),
                                    backgroundColor = Color.White,
                                    shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp),
                                    borderWidth = 2.dp,
                                    animationDurationInMillis = 5000
                                )
                        )
                        {
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = item.price)
                                Text(text = " ريال ")
                            }
                            Row {
                                val formattedDateTime = item.expireAt.replace("\\s".toRegex(), "T")
                                val date = (LocalDateTime.parse(formattedDateTime))
                                val diff =
                                    (Duration.between(currenctDate, date).toDays() + 1).toString()
                                if (diff == "1") {
                                    Text(text = "ينتهي اليوم ", fontSize = 12.sp)
                                } else {
                                    Text(text = "ينتهي خلال: ", fontSize = 12.sp)
                                    Text(text = diff, fontSize = 12.sp)
                                    Text(text = " ايام ")
                                }

                            }
                        }


                        Card(
                            Modifier
                                .aspectRatio(16f / 9f)
//                            .padding(5.dp)

                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        val intent = Intent(
                                            this@DashboardActivity,
                                            OfferProductsActivity::class.java
                                        )
                                        intent.putExtra("offer", MyJson.MyJson.encodeToString(item))
                                        startActivity(intent)
                                    }) {
                                CustomImageView(context = this@DashboardActivity, imageUrl =item.image , okHttpClient = requestServer.createOkHttpClientWithCustomCert())
//                                SubcomposeAsyncImage(
//                                    loading = {
//                                        LoadingCompose()
//                                    },
//                                    contentScale = ContentScale.Fit,
//                                    modifier = Modifier
//                                        .fillMaxSize(),
//                                    model = item.image,
//                                    contentDescription = "null",
//                                )
//                                Box (
//                                    Modifier
//                                        .align(
//                                            Alignment.BottomStart
//                                        )
//                                        .padding(5.dp)
//                                        .background(MaterialTheme.colorScheme.primary),
//                                ){
//
//
//
//                                }

                            }
                        }
                    }


                }
            })
        HorizontalDivider(thickness = 5.dp)
    }

    @Composable
    private fun AdsComponent() {
        Text(text = "الاعلانات")
        LazyHorizontalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            rows = GridCells.Fixed(1), content = {
                itemsIndexed(homeComponent.ads) { index, item ->
                    Card(
                        Modifier
                            .height(150.dp)
                            .width(300.dp)
                            .padding(5.dp)

                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .clickable {

                                }) {
                            CustomImageView(context = this@DashboardActivity, imageUrl = item.image, okHttpClient = requestServer.createOkHttpClientWithCustomCert(), modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            })
    }

    @Composable
    private fun CartCompose() {
        Text(text = "Cart")
    }

    @Composable
    private fun SettingsCompose() {
        Text(text = "Settings")
    }

    @Composable
    private fun MainCompose(onSuccess: @Composable() (() -> Unit)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Button(onClick = {
//                requestServer.login.setLoginToken("")
//                val intent =
//                    Intent(this@DashboardActivity, LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                startActivity(intent)
//                finish()
//            }) {
//                Text(text = "تسجيل الخروج")
//            }
            if (stateController.isLoadingAUD.value) {
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        LoadingCompose()
                    }
                }
            }
            if (stateController.isErrorAUD.value) {
                Toast.makeText(
                    this@DashboardActivity,
                    stateController.errorAUD.value,
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (stateController.isSuccessRead.value) {
                onSuccess()
            }
            if (stateController.isLoadingRead.value) {
                LoadingCompose()
            }
            if (stateController.isErrorRead.value) {
                Text(text = stateController.errorRead.value)
                Button(onClick = {
                    read()

                }
                ) {
                    Text(text = "retry")
                }
            }
        }
    }

}

@Composable
fun Modifier.animatedBorder(
    borderColors: List<Color>,
    backgroundColor: Color,
    shape: Shape = RectangleShape,
    borderWidth: Dp = 1.dp,
    animationDurationInMillis: Int = 1000,
    easing: Easing = LinearEasing
): Modifier {
    val brush = Brush.sweepGradient(borderColors)
    val infiniteTransition = rememberInfiniteTransition(label = "animatedBorder")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationInMillis, easing = easing),
            repeatMode = RepeatMode.Restart
        ), label = "angleAnimation"
    )

    return this
        .clip(shape)
        .padding(borderWidth)
        .drawWithContent {
            rotate(angle) {
                drawCircle(
                    brush = brush,
                    radius = size.width,
                    blendMode = BlendMode.SrcIn,
                )
            }
            drawContent()
        }
        .background(color = backgroundColor, shape = shape)
}