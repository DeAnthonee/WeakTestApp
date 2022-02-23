package com.example.testapp.modal

data class TodaysMenu(
    val menuVersion: Int = 0,
    val productList: List<Product>
)

class FakeDataGenerator {
    val listOfProductNamesAdj = listOf(
        "Super",
        "Cold",
        "Hot",
        "Slimey",
        "Crunchy",
        "Purply",
        "Wet",
        "Dry",
        "Bready",
        "Buttery",
        "Creamy",
        "Silly",
        "Big",
        "Small",
        "Lean",
        "Deadly"
    )

    val listOfProductNamesNouns = listOf(
        "Dog",
        "Cat",
        "Cherry Tree",
        "Viper",
        "Cookies",
        "Sherbert",
        "Ice",
        "Soap",
        "Dog",
        "Whensirk",
        "Porkon",
        "Proton",
        "Tellio",
        "Brakon",
        "Gambit"
    )

    val categories = listOf("Indoor", "Outdoor", "Soils", "Pots", "Tools", "Accessories")

    val makers = listOf(
        "Integrity Farms",
        "Plot Armor Op",
        "Pillups",
        "Google",
        "Herts",
        "Walmart",
        "Indoor Express",
        "Outdoors 1",
        "Deadly Gambits"
    )

    fun getFakeProduct():Product {
        val trueName = "${listOfProductNamesAdj.random()} ${listOfProductNamesNouns.random()}"
        val rnds = (3..50).random()
        val rnds2 = (0..99).random()
        val price = "$ ${(3..50).random()}.${(0..99).random()}"
        val isPopular = rnds >= 35
        val isSuper = rnds >= 40
        return Product(
            "",
            trueName,
            price,
            categories.random(),
            makers.random(),
            "",
            false,
            isPopular,
            isSuper
        )
    }


}

data class Product(
    var productId:String,
    var name: String,
    var price: String,
    var category: String,
    var maker: String,
    var imageUrl: String,
    var isOutOfStock: Boolean,
    var isPopularItem: Boolean,
    var isSuperPopularItem: Boolean
) {
    constructor() : this("","", "", "", "", "", false, false, false)
}

// Object for submitting a PreOrder
data class OrderToBePlaced(
    val customerId: String?,
    val timeUserSubmitted: String?,
    val totalPrice: String?,
    val shoppingCartListOfProducts: List<Product>?,
    val customerName: String?,
    val hasBeenVerified: Boolean = false
)

// TO sign the user up
data class UserRegistrationObject(
    val firstName: String = "DeAnthonee",
    val lastName: String = "King",
    val email: String = "dean@gmail.com",
    val dob: String = "05-25-1988",
    val phoneNumber: String = "562-444-1610",
    val picUrl: String = "www.picurl.com",
    val address: String = "1213 Some Addy ave"
)

//Deal Object
data class Deal(
    var mainText: String?,
    var subText: String?,
    var imageUrl: String?,
    var price: String?
){
    constructor():this("","","","")
}

// get client payload
data class ClientPayload(
    val allProducts: List<Product>?,
    val weeklyDeals: List<Deal>?,
    val specialDeals: List<Deal>?,
    val menuVersion: Int
){
    constructor():this(null,null,null,-1)
}

//userObject
data class UserObject(
    val name: String?,
    val isBanned: Boolean = false,
    val rewardPoints: Int?,
    val userID: String
)

//get userPayload
data class UserPayload(
    val user: UserObject,
    val pastOrderIds: List<String>?,
    val currentOrderId: String?
)

