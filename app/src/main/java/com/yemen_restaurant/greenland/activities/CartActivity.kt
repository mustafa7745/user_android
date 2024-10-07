package com.yemen_restaurant.greenland.activities
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.yemen_restaurant.greenland.MainCompose2
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.models.OrderProductWithQntModel
import com.yemen_restaurant.greenland.models.SuccessIntModel
import com.yemen_restaurant.greenland.models.UserLocationModel
import com.yemen_restaurant.greenland.shared.MyJson
import com.yemen_restaurant.greenland.shared.OfferInCart
import com.yemen_restaurant.greenland.shared.ProductInCart
import com.yemen_restaurant.greenland.shared.RequestServer
import com.yemen_restaurant.greenland.shared.StateController
import com.yemen_restaurant.greenland.shared.Urls
import com.yemen_restaurant.greenland.storage.UserStorage
import com.yemen_restaurant.greenland.ui.theme.GreenlandRestaurantTheme
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import okhttp3.MultipartBody
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.roundToInt

class CartActivity : ComponentActivity() {
    private val stateController = StateController()
    private val requestServer = RequestServer(this)
private val locationData = mutableStateOf<UserLocationModel?>(null)

    val isShow = mutableStateOf(false)
    private var type = mutableStateOf(0)
    private lateinit var productInCart : ProductInCart
    private lateinit var offerInCart: OfferInCart
    private val cart = cartController3
    val userStorage = UserStorage()
    private val finalPrice =
        mutableStateOf(
            0
        )
    private val deliveryPrice =
        mutableStateOf<Double?>(
            null
        )

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if ( userStorage.isSetUserLocation()){
            locationData.value = userStorage.getUserLocation()
            deliveryPrice.value = locationData.value!!.deliveryPrice
            val diff = Duration.between(userStorage.getDateLocation(), getCurrentDate()).toMinutes()
            if (diff>1){
                readDeliveryPrice()
            }
        }
        addDeliveryPriceToFinalPrice()
        setContent {
            GreenlandRestaurantTheme {
                MainCompose2(padding = 0.dp, stateController = stateController, activity = this) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (cart.products.value.isEmpty() and cart.offers.value.isEmpty()) Text(text = "لاتوجد منتجات في السلة")
                        else {
                            CartScreen()
                            if (isShow.value)
                                Dialog(onDismissRequest = { isShow.value = false }) {
                                    if (type.value == 1)
                                    Card {
                                        Column(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(text = productInCart.productsModel.name , color = Color.Blue)
                                        }

                                        HorizontalDivider()
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(onClick = {
                                                    cartController3.incrementProductQuantity(productInCart.productsModel.id)
                                                    addDeliveryPriceToFinalPrice()
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Add,
                                                        contentDescription = ""
                                                    )
                                                }
                                                Text(text = productInCart.productCount.value.toString())
                                                IconButton(onClick = {
                                                    cartController3.decrementProductQuantity(productInCart.productsModel.id)
                                                    addDeliveryPriceToFinalPrice()
                                                }) {
                                                    Icon(
                                                        painter = painterResource(
                                                            R.drawable.baseline_remove_24
                                                        ),
                                                        contentDescription = ""
                                                    )

                                                    //                                                                            Icon(imageVector = R.drawable.ic_launcher_background, contentDescription = "" )
                                                }
                                            }

                                            IconButton(onClick = {
                                                cartController3.removeProduct(productInCart.productsModel.id)
                                                addDeliveryPriceToFinalPrice()
                                                isShow.value = false
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Delete,
                                                    contentDescription = "",
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                    }
                                    if (type.value == 2)
                                        Card {
                                            Text(text = offerInCart.offerModel.name)
                                            HorizontalDivider()
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row (
                                                    verticalAlignment = Alignment.CenterVertically
                                                ){
                                                    IconButton(onClick = {
                                                        cartController3.incrementOfferQuantity(offerInCart.offerModel.id)
                                                        addDeliveryPriceToFinalPrice()
                                                    }) {
                                                        Icon(
                                                            imageVector = Icons.Outlined.Add,
                                                            contentDescription = ""
                                                        )
                                                    }
                                                    Text(text = offerInCart.offerCount.value.toString())
                                                    IconButton(onClick = {


                                                        cartController3.decrementOfferQuantity(offerInCart.offerModel.id)
                                                        addDeliveryPriceToFinalPrice()
                                                    }) {
                                                        Icon(
                                                            painter = painterResource(
                                                                R.drawable.baseline_remove_24
                                                            ),
                                                            contentDescription = ""
                                                        )

                                                        //                                                                            Icon(imageVector = R.drawable.ic_launcher_background, contentDescription = "" )
                                                    }
                                                }

                                                IconButton(onClick = {
                                                    cartController3.removeOffer(productInCart.productsModel.id)
                                                    addDeliveryPriceToFinalPrice()
                                                    isShow.value = false
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Delete,
                                                        contentDescription = "",
                                                        tint = Color.Red
                                                    )
                                                }
                                            }
                                        }
                                }
                        }
                    }
                }
            }
        }
    }

    private fun addDeliveryPriceToFinalPrice() {

            if (deliveryPrice.value != null){
                finalPrice.value =   cartController3.getFinalPrice()
                    .roundToInt() + deliveryPrice.value!!.toInt()
            }
        else{
                finalPrice.value =   cartController3.getFinalPrice()
                    .roundToInt()
            }
    }

    @Composable
    private fun CartScreen() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {



                // Total Price Display
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "اجمالي الاصناف: ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = roundToNearestFifty(formatPrice(cartController3.getFinalPrice().roundToInt().toString()).toInt()).toString(),
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                }
                if (locationData.value != null){
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "سعر التوصيل",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (deliveryPrice.value != null)
                        Text(
                            text = formatPrice(deliveryPrice.value.toString()).toString(),
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                        else{
                            Card (
                                Modifier
                                    .clickable {
                                       readDeliveryPrice()
                                    },
                            ){
                                Box (
                                    Modifier
                                        .background(MaterialTheme.colorScheme.primary)){
                                    Text(
                                        modifier = Modifier.padding(1.dp),

                                        text = "عرض سعر التوصيل",
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                            }

                        }
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "المبلغ المتوجب دفعه: ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = finalPrice.value.toString(),
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                    }

                }

                HorizontalDivider(Modifier.padding(10.dp))

                if (locationData.value == null){
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        onClick = {
                            chooseLocation()
                        }
                    ) {
                        Text(
                            text = "اختيار موقع التوصيل", fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(
                                Font(R.font.bukra_bold))
                        )
                    }
                }else{
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        onClick = { confirmOrder() }
                    ) {
                        Text(
                            text = "تأكيد الطلب", fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(
                                Font(R.font.bukra_bold))
                        )
                    }
                }
            }
        }


        val column0Weight = 0.07f // 30%
        val column1Weight = 0.44f // 30%
        val column2Weight = 0.12f // 70%
        val column3Weight = 0.15f // 30%
        val column4Weight = 0.22f // 70%



        HorizontalDivider()
        LazyColumn(content = {
            if (locationData.value != null)
                item {
                    CardView(title = "موقع التوصيل") {
                        Text(
                            text = locationData.value!!.street, // Replace with actual location data
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Text(
                            text = locationData.value!!.nearTo, // Replace with actual location data
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Text(
                            text = locationData.value!!.contactPhone, // Replace with actual location data
                            style = TextStyle(fontSize = 16.sp),
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Card (
                            Modifier

                                .clickable { chooseLocation() },
                        ){
                            Box (
                                Modifier
                                    .fillMaxSize().background(MaterialTheme.colorScheme.primary)){
                                Text(
                                    modifier = Modifier.padding(1.dp),
                                    text = "تغيير العنوان",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
//                    Column(
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//
//
//                        Text(
//                            text = "طريقة الدفع:", // Replace with actual location data
//                            style = TextStyle(fontSize = 12.sp),
//                            color = Color.Red,
//                            modifier = Modifier
//                                .padding(bottom = 5.dp)
//                        )
//                        Text(
//                            text = "الدفع عند التسليم", // Replace with actual location data
//                            style = TextStyle(fontSize = 12.sp),
//                            color = Color.Red,
//                            modifier = Modifier
//                                .padding(bottom = 5.dp)
//                        )
//                        Text(
//                            text = "الدفع الالكتروني؟ تواصل معنا"  , // Replace with actual location data
//                            style = TextStyle(fontSize = 12.sp),
//                            color = Color.Red,
//                            modifier = Modifier
//                                .padding(bottom = 5.dp)
//                        )
//                        Button(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 8.dp),
//                            onClick = { confirmOrder() }
//                        ) {
//                            Text(
//                                text = "تأكيد الطلب", fontSize = 20.sp,
//                                fontWeight = FontWeight.Bold,
//                                fontFamily = FontFamily(
//                                    Font(R.font.bukra_bold))
//                            )
//                        }
//                    }
                }
            item{
                CardView("طريقة الدفع"){
                    Row (
                        Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.Start,
                     verticalAlignment = Alignment.CenterVertically,
                    ){
                        Icon(
                        tint = MaterialTheme.colorScheme.primary,
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = ""
                    )
                        Text(
                            modifier = Modifier.padding(vertical = 8.dp),
                            text = "الدفع عند الاستلام",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                    }
                    Row (
                        Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ){

                    }

                    Card (
                        Modifier

                            .clickable {
                                intentFunWhatsapp()
                            },
                    ){
                        Box (
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primary)){
                            Text(
                                modifier = Modifier.padding(1.dp),

                                text = "الدفع الالكتروني؟ تواصل معنا",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }

                }
            }
            item{
                Text(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),text = "للتعديل او الحذف اضغط على المنتج", fontSize = 8.sp,
                    color =   Color.Blue
                )
            }



            item {
                Row(Modifier.background(Color.Gray)) {

                    TableCellHeader(text = "#", weight = column0Weight)
                    TableCellHeader(text = "الصنف", weight = column1Weight)
                    TableCellHeader(text = "ك", weight = column2Weight)
                    TableCellHeader(text = "السعر", weight = column3Weight)
                    TableCellHeader(text = "الاجمالي", weight = column4Weight)

                }
            }
            itemsIndexed(cart.products.value) { index, s ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            productInCart = s
                            type.value = 1
                            isShow.value = true

                        }) {

                    TableCell(
                        text = (index + 1).toString(), weight = column0Weight
                    )
                    TableCell(
                        text = s.productsModel.name, weight = column1Weight
                    )
                    TableCell(
                        text = s.productCount.value.toString(),
                        weight = column2Weight
                    )
                    TableCell(
                        text = formatPrice(s.productsModel.postPrice) , weight = column3Weight
                    )
                    TableCell(
                        text = formatPrice ((s.productsModel.postPrice.toDouble() * s.productCount.value).toString()),
                        weight = column4Weight
                    )

                }
            }
            itemsIndexed(cart.offers.value) { index, s ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            offerInCart = s
                            type.value = 2
                            isShow.value = true
                        }
                ) {
                    TableCell(
                        text = (index + 1).toString(), weight = column0Weight
                    )
                    TableCell(
                        text = s.offerModel.name, weight = column1Weight
                    )
                    TableCell(
                        text = s.offerCount.value.toString(),
                        weight = column2Weight
                    )
                    TableCell(
                        text = formatPrice(s.offerModel.price) , weight = column3Weight
                    )
                    TableCell(
                        text = formatPrice((s.offerModel.price.toDouble() * s.offerCount.value).toString()),
                        weight = column4Weight
                    )
                }
            }
        }


        )
    }

    @Composable
    private fun CardView(title:String, content: @Composable() (ColumnScope.() -> Unit)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(Modifier.padding(10.dp))
                content()
            }
        }
    }

    private fun chooseLocation() {
        val intent =
            Intent(this, UserLocationsActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    private fun confirmOrder() {
        stateController.startAud()

        val orderProducts = arrayListOf<OrderProductWithQntModel>()
        val orderOffers = arrayListOf<OrderProductWithQntModel>()

        for ((index, value) in cartController3.products.value.withIndex()) {
            orderProducts.add(
                OrderProductWithQntModel(
                    value.productsModel.id, value.productCount.value.toString()
                )
            )
        }
        for ((index, value) in cartController3.offers.value.withIndex()) {
            orderOffers.add(
                OrderProductWithQntModel(
                    value.offerModel.id, value.offerCount.value.toString()
                )
            )
        }

        val data3 = buildJsonObject {
            put("tag", "add")
            put("inputOrderProductsIdsWithQnt", MyJson.MyJson.encodeToJsonElement(orderProducts))
            put("inputOrderOffersIdsWithQnt", MyJson.MyJson.encodeToJsonElement(orderOffers))
            put("inputUserLocationId", locationData.value!!.id)
        }


        val body = MultipartBody.Builder().setType(
            MultipartBody.FORM
        ).addFormDataPart(
            "data1", requestServer.getData1().toString()
        ).addFormDataPart(
            "data2", requestServer.getData2()
        ).addFormDataPart("data3", data3.toString()).build()


        requestServer.request2(body, Urls.ordersUrl, { code, it ->
            stateController.errorStateAUD(it)
        }) {
            stateController.successState()
            val intent = Intent(
                this@CartActivity, OrdersProductsActivity::class.java
            )
            intent.putExtra("orderContent", it)
            startActivity(intent)
            finish()
        }

    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.e("result Avt", result.toString())
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val location = data.getStringExtra("location")
                if (location != null) {
                    locationData.value = MyJson.IgnoreUnknownKeys.decodeFromString(location)
                    deliveryPrice.value = locationData.value!!.deliveryPrice
                    addDeliveryPriceToFinalPrice()
                }
            }
        }
    }

    @Composable
    fun RowScope.TableCell(
        text: String, weight: Float
    ) {
        Text(
            modifier = Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            text = text,
            fontSize = 10.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
    @Composable
    fun RowScope.TableCellHeader(
        text: String, weight: Float
    ) {
        Text(
            modifier = Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            text = text,
            fontSize = 10.sp,
            color =Color.White,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
    private fun intentFunWhatsapp(): Boolean {
        val formattedNumber = "967780222271"
        val message = "السلام عليكم"

        // Create the URI for the WhatsApp link
        val uri =
            "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"

        // Create an Intent to open the WhatsApp application
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uri)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        try {
            startActivity(intent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "يجب تثبيت الواتس اولا", Toast.LENGTH_LONG).show()
            return false
        }
    }
    private fun readDeliveryPrice() {
        stateController.startAud()
        val data3 = buildJsonObject {
            put("tag", "readDeliveryPrice")
            put( "inputUserLocationId",locationData.value!!.id)
        }

        val body1 = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("data3", data3.toString())
            .build()

        requestServer.request2(body1, Urls.userLocationUrl, { code, it ->
          stateController.errorStateAUD(it)
           deliveryPrice.value = null
            addDeliveryPriceToFinalPrice()
        }) {
            val price = MyJson.IgnoreUnknownKeys.decodeFromString<SuccessIntModel>(it)
               deliveryPrice.value = price.success.toDouble()
            addDeliveryPriceToFinalPrice()
            userStorage.setDateLocation()
            stateController.successStateAUD()
        }
    }
}

fun getCurrentDate(): LocalDateTime {
    return LocalDateTime.now()
}
