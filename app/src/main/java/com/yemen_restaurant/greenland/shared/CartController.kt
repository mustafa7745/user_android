package com.yemen_restaurant.greenland.shared

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.yemen_restaurant.greenland.models.OfferModel
import com.yemen_restaurant.greenland.models.OrderContentDeliveryModel
import com.yemen_restaurant.greenland.models.OrderContentDiscountModel
import com.yemen_restaurant.greenland.models.OrderContentModel
import com.yemen_restaurant.greenland.models.OrderContentOffersModel
import com.yemen_restaurant.greenland.models.OrderContentProductsModel
import com.yemen_restaurant.greenland.models.ProductModel


data class ProductInCart (
    val productsModel: ProductModel,
    var productCount:MutableState<Int>
)
data class OfferInCart (
    val offerModel: OfferModel,
    var offerCount:MutableState<Int>
)

class CartController3{
     val products : MutableState<List<ProductInCart>> = mutableStateOf(emptyList())
     val offers: MutableState<List<OfferInCart>> = mutableStateOf(emptyList())

    fun addProduct(productModel: ProductModel, count: Int = 1) {
        val currentProducts = products.value.toMutableList()
        val existingProduct = currentProducts.find { it.productsModel.id == productModel.id }
        if (existingProduct != null) {
            existingProduct.productCount.value += count
        } else {
            currentProducts.add(ProductInCart(productModel, mutableStateOf(count)))
        }
        products.value = currentProducts
    }
    fun removeProduct(productId: String) {
        val updatedProducts = products.value.filterNot { it.productsModel.id == productId }
        products.value = updatedProducts
    }

    // Method to increment the quantity of a product
    fun incrementProductQuantity(productId: String) {
        val updatedProducts = products.value.toMutableList()
        val product = updatedProducts.find { it.productsModel.id == productId }
        if (product != null) {
            product.productCount.value = (product.productCount.value + 1).coerceAtLeast(0)
        }
        products.value = updatedProducts
    }

    // Method to decrement the quantity of a product
    fun decrementProductQuantity(productId: String, count: Int = 1) {
        val updatedProducts = products.value.toMutableList()
        val product = updatedProducts.find { it.productsModel.id == productId }
        if (product != null) {

            if (product.productCount.value > 1) {
                product.productCount.value = (product.productCount.value - count).coerceAtLeast(0)
                products.value = updatedProducts
            }

        }
    }
    // Method to add an offer to the cart
    fun addOffer(offerModel: OfferModel, count: Int = 1) {
        val currentOffers = offers.value.toMutableList()
        val existingOffer = currentOffers.find { it.offerModel.id == offerModel.id }
        if (existingOffer != null) {
            existingOffer.offerCount.value += count
        } else {
            currentOffers.add(OfferInCart(offerModel, mutableStateOf(1)))
        }
        offers.value = currentOffers
    }

    // Method to remove an offer from the cart
    fun removeOffer(offerId: String) {
        val updatedOffers = offers.value.filterNot { it.offerModel.id == offerId }
        offers.value = updatedOffers
    }

    // Method to increment the quantity of an offer
    fun incrementOfferQuantity(offerId: String, count: Int = 1) {
        val updatedOffers = offers.value.toMutableList()
        val offer = updatedOffers.find { it.offerModel.id == offerId }
        if (offer != null) {
            offer.offerCount.value = (offer.offerCount.value + count).coerceAtLeast(0)
        }
        offers.value = updatedOffers
    }

    // Method to decrement the quantity of an offer
    fun decrementOfferQuantity(offerId: String, count: Int = 1) {
        val updatedOffers = offers.value.toMutableList()
        val offer = updatedOffers.find { it.offerModel.id == offerId }
        if (offer != null) {
            if (offer.offerCount.value > 1) {
                offer.offerCount.value = (offer.offerCount.value - count).coerceAtLeast(0)
                offers.value = updatedOffers
            }

        }
    }

    // Method to calculate the total price of all products
    fun calculateTotalProductPrice(): Double {
        return products.value.sumOf { it.productsModel.postPrice.toDouble() * it.productCount.value }
    }

    // Method to calculate the total price of all offers
    fun calculateTotalOfferPrice(): Double {
        return offers.value.sumOf {  (it.offerModel.price.toDouble() * it.offerCount.value) }
    }
    fun getFinalPrice(): Double {
        return calculateTotalOfferPrice() + calculateTotalProductPrice()
    }

}

class CartController2 {

    // Mutable properties
    private val products: MutableList<OrderContentProductsModel> = mutableListOf()
    private val offers: MutableList<OrderContentOffersModel> = mutableListOf()
    private var delivery: OrderContentDeliveryModel? = null
    private var discount: OrderContentDiscountModel? = null

    // Method to add a product
//    fun addProduct(product: OrderContentProductsModel) {
//        val existingProduct = products.find { it.id == product.id }
//        if (existingProduct != null) {
//            // Product already exists, just update the quantity
//            existingProduct.productQuantity.value += product.productQuantity.value
//        } else {
//            // New product, add to the list
//            products.add(product)
//        }
//    }
//
//    // Method to increment the quantity of a product
//    fun incrementQuantity(productId: String, quantity: Int = 1) {
//        val product = products.find { it.id == productId }
//        if (product != null) {
//            product.productQuantity += quantity
//        }
//    }
//
//    // Method to decrement the quantity of a product
//    fun decrementQuantity(productId: String, quantity: Int = 1) {
//        val product = products.find { it.id == productId }
//        if (product != null) {
//            product.productQuantity.value = (product.productQuantity.value - quantity).coerceAtLeast(0)
//            // Remove product if quantity is 0
//            if (product.qua == 0) {
//                products.remove(product)
//            }
//        }
//    }
//
//
//    // Method to remove a product
//    fun removeProduct(product: OrderContentProductsModel) {
//        products.remove(product)
//    }

    // Method to add an offer
    fun addOffer(offer: OrderContentOffersModel) {
        offers.add(offer)
    }

    // Method to remove an offer
    fun removeOffer(offer: OrderContentOffersModel) {
        offers.remove(offer)
    }

    // Method to set delivery information
    fun setDelivery(delivery: OrderContentDeliveryModel?) {
        this.delivery = delivery
    }

    // Method to set discount information
    fun setDiscount(discount: OrderContentDiscountModel?) {
        this.discount = discount
    }

    // Method to get the current state of the cart
    fun getCartContent(): OrderContentModel {
        return OrderContentModel(
            products = products,
            offers = offers.toList(),
            delivery = delivery,
            discount = discount
        )
    }
}
