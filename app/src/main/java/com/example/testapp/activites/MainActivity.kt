package com.example.testapp.activites

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.BasicViewModel
import com.example.testapp.R
import com.example.testapp.modal.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {
    val viewModel: BasicViewModel by inject()
    private lateinit var functions: FirebaseFunctions
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        functions = FirebaseFunctions.getInstance()
        val something = viewModel.sayHello()
//        getProducts()
        val button: Button = findViewById(R.id.my_button_id)
        button.setOnClickListener {
//            SecondActivity.start(this)
//            getProducts()
//            saveProductsToDb()
        }

        makeMyToast(something)
    }


    private fun getProducts() {
        db.collection("products")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val list = ArrayList<Product>()
                querySnapshot.documents.forEach {
                    if (it.exists()) {
                        val product = it.toObject(Product::class.java)
                        list.add(product!!)
                    }
                }

                list.forEach {
                    makeMyToast(it.name)
                    makeMyToast(it.isPopularItem.toString())
                }

                saveClientPayload(list)
            }
            .addOnFailureListener {
                makeMyToast("The pull failed")
            }
    }

    private fun makeMyToast(something: String?) {
        val thisOne = something ?: "Empty / Null"
        Log.d("mixo", something!!)
        Toast.makeText(this, something, Toast.LENGTH_SHORT).show()
    }

    private fun saveProductsToDb() {
        val prod = createSomeProducts(1)[0]
        var ref = db.collection("products").document()
        prod.productId = ref.id
        db.collection("products")
            .document(prod.productId)
            .set(prod)
            .addOnFailureListener { makeMyToast("FAILED") }
            .addOnSuccessListener { makeMyToast("Success") }
    }

    private fun createSomeProducts(target: Int): List<Product> {
        val fakeData = FakeDataGenerator()
        val listOfFakeProducts = ArrayList<Product>()
        for (i in 0..target) listOfFakeProducts.add(fakeData.getFakeProduct())
        return listOfFakeProducts
    }


    private fun saveClientPayload(listOfProducts: List<Product>) {

        val clientPayload = ClientPayload(
            allProducts = listOfProducts,
            weeklyDeals = saveSomeDeals(),
            specialDeals = saveSomeSpecialDeals(),
            1
        )
        db.collection("Utils")
            .document("TodaysMenu")
            .set(clientPayload)
            .addOnSuccessListener { makeMyToast("On Success for saving Todays Menu") }
            .addOnFailureListener { makeMyToast("Failed to save Todays Menu") }
    }

    private fun saveSomeDeals(): List<Deal> {

        val deal = Deal("Monday", "50% off all Outdoor", "", "")
        val deal2 = Deal("Tuesday", "10% off all Indoor", "", "")
        val deal3 = Deal("Wednesday", "15% off all Soil", "", "")
        val deal4 = Deal("Thursday", "By one Pot get one Free", "", "")
        val deal5 = Deal("Friday", "10% off everything", "", "")
        val deal6 = Deal("Saturday", "Get a free puppy with ever purchase", "", "")
        val deal7 = Deal("Sunday", "All Plastic bags are free", "", "")

        return listOf(deal, deal2, deal3, deal4, deal5, deal6, deal7)
    }

    private fun saveSomeSpecialDeals(): List<Deal> {
        val deal = Deal("Shirts", "50% off all Outdoor", "", "")
        val deal2 = Deal("Pants", "10% off all Indoor", "", "")
        val deal3 = Deal("Intergrity Products half off", "some swtuff ", "", "")
        val deal4 = Deal("Free Dogs", "By one Pot get one Free", "", "")
        val deal5 = Deal("Fry food Yall", "10% off everything", "", "")
        val deal6 = Deal("Bogo", "Get a free puppy with ever purchase", "", "")
        val deal7 = Deal("Super Bogo", "All Plastic bags are free", "", "")

        return listOf(deal, deal2, deal3, deal4, deal5, deal6, deal7)
    }
}