package com.example.assignment.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.navigation.fragment.navArgs
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.assignment.R
import com.example.assignment.data.Product
import com.example.assignment.databinding.FragmentCheckoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import org.json.JSONException
import org.json.JSONObject


class CheckoutFragment : Fragment() {
    private val args by navArgs<CheckoutFragmentArgs>()
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var paymentSheet: PaymentSheet
    private var paymentIntentClientSecret: String? = null
    private var configuration: PaymentSheet.CustomerConfiguration? = null
    private lateinit var product: Product
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        product = args.product
        firestoreDb = FirebaseFirestore.getInstance()


        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        binding.productName.text = product.name
        binding.productPrice.text = String.format("RM%.2f", product.price)
        binding.productCategory.text = product.category
        binding.subtotalValue.text = String.format("RM%.2f", product.price)
        binding.sstValue.text = String.format("RM%.2f", product.price * 0.06)
        var shippingFee = 10.0
        if (product.price > 100)
            shippingFee = 5.0
        binding.shippingFeeValue.text = String.format("RM%.2f", shippingFee)
        val total = product.price + product.price * 0.06 + shippingFee
        binding.totalValue.text = String.format("RM%.2f", total)


        // Use Glide to load the product image into the ImageView
        Glide.with(this)
            .load(product.imageUrls[0])
            .into(binding.productImage)

        fetchApi()


        binding.btnStripe.setOnClickListener {
            if (paymentIntentClientSecret != null) {
                paymentSheet.presentWithPaymentIntent(
                    paymentIntentClientSecret!!,
                    PaymentSheet.Configuration("TARUMT", configuration)
                )
            } else {
                //show error message
                Toast.makeText(context, "Api is loading...", Toast.LENGTH_LONG).show()

            }
        }

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        return binding.root
    }



    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> Toast.makeText(context, "Payment Canceled", Toast.LENGTH_SHORT).show()
            is PaymentSheetResult.Failed -> Toast.makeText(context, "Payment Failed: ${paymentSheetResult.error?.message}", Toast.LENGTH_SHORT).show()
            is PaymentSheetResult.Completed -> Toast.makeText(context, "Payment Completed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchApi() {
        // First, fetch the username.
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        fetchUsername(currentUserID!!) { username ->
            // Once the username is fetched, proceed with creating and queuing the request.
            val queue = Volley.newRequestQueue(requireContext())
            val url = "http://10.0.2.2/stripe/index.php"

            val stringRequest = object : StringRequest(Method.POST, url,
                Response.Listener<String> { response ->
                    Log.d("API Response", "Full response: $response")
                    try {
                        val jsonObject = JSONObject(response)
                        paymentIntentClientSecret = jsonObject.getString("paymentIntent")
                        configuration = PaymentSheet.CustomerConfiguration(
                            jsonObject.getString("customer"),
                            jsonObject.getString("ephemeralKey")
                        )
                        PaymentConfiguration.init(requireContext(), jsonObject.getString("publishableKey"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(context, "Error parsing the server response", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    Toast.makeText(context, "Error fetching data from server", Toast.LENGTH_LONG).show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["authKey"] = "abc"
                    params["amount"] = (product.price * 100).toInt().toString() // Convert to cents
                    params["customerName"] = username ?: "Unknown Seller"
                    params["customerEmail"] = userEmail ?: "Unknown Email"
                    return params
                }
            }
            queue.add(stringRequest)
        }
    }

    private fun fetchUsername(userID: String, onResult: (String?) -> Unit) {
        firestoreDb.collection("users").document(userID).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val sellerName = document.getString("username")
                    onResult(sellerName)
                } else {
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("CheckoutFragment", "Error getting user details: ", exception)
                Toast.makeText(context, "Failed to fetch user details", Toast.LENGTH_SHORT).show()
                onResult(null)
            }
    }

}