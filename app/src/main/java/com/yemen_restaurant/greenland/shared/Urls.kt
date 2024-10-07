package com.yemen_restaurant.greenland.shared

class Urls {
    companion object {
//        init {
//            System.loadLibrary("channeldetails");
//        }
//
//
//        private external fun Java_com_onemegasoft_greenlandrestaurant_baseurl(): String?
//        private val baseUrl = Base64.decode(Java_com_onemegasoft_greenlandrestaurant_baseurl(),Base64.DEFAULT).toString(Charsets.UTF_8);
//        private val baseUrl = "http://192.168.1.4/test2/v1/"
//            val hostname = "https://greenland-rest.com"
//        private val baseUrl = "https://greenland-rest.com/v1/"
        private val baseUrl = "https://user99123.greenland-rest.com/"
        private val base = baseUrl + "user_app/api/"
        val initUrl = base + "init.php"
        val publicKeyUrl = baseUrl +"get_public_key.php"
        //
        val loginUrl = base + "login.php"
        val refreshToken = base + "refresh_token.php"
        //
        val categoryUrl = base + "categories/index.php"
        val homeUrl = base + "home/index.php"
            val offersProductsUrl = base + "offers_products/index.php"
        val currencyUrl = base + "projects_currencies/index.php"
        val productsUrl = base + "products/index.php"
        val ordersUrl = base + "orders/index.php"
        val usersUrl = base + "users/index.php"
        val projectAdsUrl = base + "project_ads/index.php"
        val userLocationUrl = base + "users_locations/index.php"
            val locationTypesUrl = base + "location_types/index.php"
    }

}