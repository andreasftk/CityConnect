package cityconnnect.app.ui

import cityconnnect.app.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cityconnect.app.ParkingTicket
import cityconnect.app.UserParkingTicket
import cityconnnect.app.ParkingCategories
import cityconnnect.app.data.ApiClient
import cityconnnect.app.data.User
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuyParkingTickets : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var duration: Array<String> = arrayOf("1hr", "3hrs", "5hrs")
    private lateinit var catList: ArrayList<ParkingCategories>
    private lateinit var selectedCat: ParkingCategories
    private lateinit var parkingTicketList: ArrayList<ParkingTicket>
    private var selectedTicketCost: Float? = null
    private var selectedAmount: Int = 1
    private var selectedDuration: String = "1hr"
    private var userId: Int = 1 // Class-level variable to store user ID
    private var parking_id: Int = 1 // Class-level variable to store user ID

    private var userCat: String = "normal" // Class-level variable to store user ID

    private lateinit var dailyParkingTickets: ArrayList<ParkingTicket>
    private lateinit var fiveHoursParkingTickets: ArrayList<ParkingTicket>
    private lateinit var oneHourParkingTickets: ArrayList<ParkingTicket>
    private lateinit var threeHoursParkingTickets: ArrayList<ParkingTicket>
    private lateinit var weeklyParkingTickets: ArrayList<ParkingTicket>

    private lateinit var userParkingTicketAdapter: UserParkingTicketAdapter
    private lateinit var userParkingTicketList: ArrayList<UserParkingTicket>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_time)
        val bundle = intent.extras

        // Extract the data from the Bundle
        if (bundle != null) {
            userId = bundle.getInt("id")
            parking_id = bundle.getInt("parking_id")
            val selectedParkingCategory = bundle.getString("category_id")
            if (selectedParkingCategory != null) {
                fetchParkingCategories(selectedParkingCategory)
            }
        }

        // Initialize the first spinner for amounts
        val btnMinus = findViewById<Button>(R.id.btn_minus)
        val btnPlus = findViewById<Button>(R.id.btn_plus)
        val amountTextView = findViewById<TextView>(R.id.amount_tv)

        btnMinus.setOnClickListener {
            if (selectedAmount > 1) {
                selectedAmount--
                amountTextView.text = selectedAmount.toString()
                updateTotalCost()
            }
        }

        btnPlus.setOnClickListener {
            selectedAmount++
            amountTextView.text = selectedAmount.toString()
            updateTotalCost()
        }

        val spin3 = findViewById<Spinner>(R.id.hour_spinner)
        spin3.onItemSelectedListener = this
        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, duration)
        spin3.adapter = adapter3

        recyclerView = findViewById<RecyclerView>(R.id.rv2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userParkingTicketList = ArrayList()
        userParkingTicketAdapter = UserParkingTicketAdapter(userParkingTicketList)
        recyclerView.adapter = userParkingTicketAdapter

        val buyDay = findViewById<Button>(R.id.buy_day)
        val buyWeek = findViewById<Button>(R.id.buy_week2)
        val buySingle = findViewById<Button>(R.id.buy_single)

        User.getUserCategory(this, userId) { category ->
            if (category != null) {
                userCat = category
            }
        }
        buyWeek.setOnClickListener {
            // Call insertBusTicket when the button is clicked
            val zone = selectedCat.category
            insertUserParkingTicket(parking_id.toString(), userId, userCat, "weekly", 0)
            fetchUserParkingTickets(selectedCat.category, userId)

        }
        buySingle.setOnClickListener {
            // Call insertBusTicket when the button is clicked
            val zone = selectedCat.category
            val duration = selectedDuration
            var selected = "null"
            when (duration) {
                "1hr" -> {
                    selected = "one hour"
                }
                "3hrs" -> {
                    selected = "three hours"
                }
                "5hrs" -> {
                    selected = "five hours"
                }
            }
            insertUserParkingTicket(parking_id.toString(), userId, userCat, selected, selectedAmount)
            fetchUserParkingTickets(selectedCat.category, userId)

        }
        buyDay.setOnClickListener {
            // Call insertBusTicket when the button is clicked
            val zone = selectedCat.category
            insertUserParkingTicket(parking_id.toString(), userId, userCat, "daily", 0)
            fetchUserParkingTickets(selectedCat.category, userId)
        }
    }

    private fun fetchParkingCategories(selectedParkingCategory: String) {
        // Fetch parking categories
        ParkingCategories.getParkingCategories(this) { categories ->
            catList = categories
            selectedCat = catList.find { it.category == selectedParkingCategory }
                ?: throw IllegalArgumentException("Selected category not found")

            val categoryTextView = findViewById<TextView>(R.id.zone_tv)
            categoryTextView.text = "Category: ${selectedCat.category}"

            fetchParkingTickets()
            fetchUserParkingTickets(selectedCat.category, userId)
        }
    }

    private fun fetchParkingTickets() {
        // Fetch parking tickets
        ParkingTicket.getParkingTickets(this) { parkingTickets ->
            parkingTicketList = parkingTickets

            // Split the parkingTicketList into different arrays based on category
            dailyParkingTickets = ArrayList()
            fiveHoursParkingTickets = ArrayList()
            oneHourParkingTickets = ArrayList()
            threeHoursParkingTickets = ArrayList()
            weeklyParkingTickets = ArrayList()

            for (ticket in parkingTicketList) {
                if (ticket.ticket_type != null) {
                    when (ticket.ticket_type) {
                        "daily_parking_ticket" -> dailyParkingTickets.add(ticket)
                        "five_hours_parking_ticket" -> fiveHoursParkingTickets.add(ticket)
                        "one_hour_parking_ticket" -> oneHourParkingTickets.add(ticket)
                        "three_hours_parking_ticket" -> threeHoursParkingTickets.add(ticket)
                        "weekly_parking_ticket" -> weeklyParkingTickets.add(ticket)
                        else -> {
                            Log.w("BuyParkingTickets", "Unknown ticket type: ${ticket.ticket_type}")
                        }
                    }
                } else {
                    Log.w("BuyParkingTickets", "Found a ticket with null ticket_type: $ticket")
                }
            }
            updateTicketPrices()
        }
    }

    private fun fetchUserParkingTickets(category: String, userId: Int) {
        UserParkingTicket.getUserParkingTickets(this, userId.toString()) { userParkingTickets ->
            if (userParkingTickets != null) {
                Log.d("UserParkingTickets", "UserParkingTickets: $userParkingTickets")
                val filteredTickets = userParkingTickets.filter { it.region == category }
                userParkingTicketList.clear()
                userParkingTicketList.addAll(filteredTickets)
                userParkingTicketAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun updateTicketPrices() {
        val selectedDailyTicket = dailyParkingTickets.find { it.region == selectedCat.category }
        val selectedFiveHoursTicket = fiveHoursParkingTickets.find { it.region == selectedCat.category }
        val selectedOneHourTicket = oneHourParkingTickets.find { it.region == selectedCat.category }
        val selectedThreeHoursTicket = threeHoursParkingTickets.find { it.region == selectedCat.category }
        val selectedWeeklyTicket = weeklyParkingTickets.find { it.region == selectedCat.category }

        val priceDailyTextView = findViewById<TextView>(R.id.price_day)
        val priceFiveHoursTextView = findViewById<TextView>(R.id.price_single)
        val priceOneHourTextView = findViewById<TextView>(R.id.price_single)
        val priceThreeHoursTextView = findViewById<TextView>(R.id.price_single)
        val priceWeeklyTextView = findViewById<TextView>(R.id.price_week)

        if (selectedDailyTicket != null) {
            selectedTicketCost = selectedDailyTicket.price
            priceDailyTextView.text = "${selectedDailyTicket.price}$"
        } else {
            selectedTicketCost = null
            priceDailyTextView.text = "Daily: N/A"
        }

        if (selectedFiveHoursTicket != null) {
            selectedTicketCost = selectedFiveHoursTicket.price
            priceFiveHoursTextView.text = "${selectedFiveHoursTicket.price}$"
        } else {
            selectedTicketCost = null
            priceFiveHoursTextView.text = "Five Hours: N/A"
        }

        if (selectedOneHourTicket != null) {
            selectedTicketCost = selectedOneHourTicket.price
            priceOneHourTextView.text = "${selectedOneHourTicket.price}$"
        } else {
            selectedTicketCost = null
            priceOneHourTextView.text = "One Hour: N/A"
        }

        if (selectedThreeHoursTicket != null) {
            selectedTicketCost = selectedThreeHoursTicket.price
            priceThreeHoursTextView.text = "${selectedThreeHoursTicket.price}$"
        } else {
            selectedTicketCost = null
            priceThreeHoursTextView.text = "Three Hours: N/A"
        }

        if (selectedWeeklyTicket != null) {
            selectedTicketCost = selectedWeeklyTicket.price
            priceWeeklyTextView.text = "${selectedWeeklyTicket.price}$"
        } else {
            selectedTicketCost = null
            priceWeeklyTextView.text = "Weekly: N/A"
        }

        updateTotalCost()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.hour_spinner -> {
                // Handle the spinner3 selection for hour options
                selectedDuration = duration[position]
                Log.d("SelectedAmount", "Selected Amount: $selectedDuration")

                // Ensure the selectedCat and oneHourParkingTickets are initialized
                if (::selectedCat.isInitialized && ::oneHourParkingTickets.isInitialized) {
                    val priceTextView = findViewById<TextView>(R.id.price_single)
                    when (selectedDuration) {
                        "1hr" -> {
                            val selectedOneHourTicket = oneHourParkingTickets.find { it.region == selectedCat.category }
                            selectedTicketCost = selectedOneHourTicket?.price ?: 0.0f
                        }
                        "3hrs" -> {
                            val selectedThreeHoursTicket = threeHoursParkingTickets.find { it.region == selectedCat.category }
                            selectedTicketCost = selectedThreeHoursTicket?.price ?: 0.0f
                        }
                        "5hrs" -> {
                            val selectedFiveHoursTicket = fiveHoursParkingTickets.find { it.region == selectedCat.category }
                            selectedTicketCost = selectedFiveHoursTicket?.price ?: 0.0f
                        }
                    }
                    priceTextView.text = "${selectedTicketCost}$"
                    updateTotalCost()
                } else {
                    Log.e("BuyParkingTickets", "selectedCat or oneHourParkingTickets not initialized")
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Do nothing
    }

    private fun updateTotalCost() {
        val priceXAmountTextView = findViewById<TextView>(R.id.price_xamount)
        if (selectedTicketCost != null) {
            val totalCost = selectedTicketCost!! * selectedAmount
            priceXAmountTextView.text = "Total: $totalCost$"
        } else {
            priceXAmountTextView.text = "Select category first."
        }
    }

    private fun insertUserParkingTicket(parkingId: String, userId: Int, userCat: String, duration: String, number: Int) {
        val api = ApiClient.apiService
        val call = api.insertUserParkingTicket(parkingId, userId, userCat, duration, number)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val responseBody = it.string()
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val status = jsonResponse.getString("status")
                            val message = jsonResponse.getString("message")
                            if (status == "success") {
                                Log.d("InsertParkingTicket", message)
                                showToast("Parking ticket purchased successfully")
                            } else {
                                Log.e("InsertParkingTicket", message)
                                showToast(message)
                            }
                        } catch (e: JSONException) {
                            Log.e("InsertParkingTicket", "Failed to parse response JSON", e)
                            showToast("Failed to insert parking ticket: Invalid response format")
                        }
                    } ?: run {
                        Log.e("InsertParkingTicket", "Response body is null")
                        showToast("Failed to insert parking ticket: No response from server")
                    }
                } else {
                    Log.e("InsertParkingTicket", "Failed to insert parking ticket. Error code: ${response.code()}")
                    showToast("Failed to insert parking ticket. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("InsertParkingTicket", "Failed to insert parking ticket", t)
                showToast("Failed to insert parking ticket. Please try again later.")
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(this@BuyParkingTickets, message, Toast.LENGTH_SHORT).show()
    }
}
