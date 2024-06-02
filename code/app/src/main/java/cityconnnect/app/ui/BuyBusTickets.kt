package cityconnnect.app.ui

import cityconnnect.app.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cityconnect.app.UserBusTicket
import cityconnnect.app.BusLine
import cityconnnect.app.BusTicket
import cityconnnect.app.data.ApiClient
import cityconnnect.app.data.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuyBusTickets : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var amount: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    private lateinit var busLineList: ArrayList<BusLine>
    private lateinit var selectedBusLine: BusLine
    private lateinit var singleUseBusTicketList: ArrayList<BusTicket>
    private lateinit var weeklyBusTicketList: ArrayList<BusTicket>
    private lateinit var monthlyBusTicketList: ArrayList<BusTicket>
    private var selectedSingleTicketCost: Float? = null
    private var selectedWeeklyTicketCost: Float? = null
    private var selectedMonthlyTicketCost: Float? = null
    private var selectedAmount: Int = 1
    private var selectedRoute: String = "1" // Default route value
    private var userId: Int = -1 // Class-level variable to store user ID
    private var userCat: String = "normal" // Class-level variable to store user ID

    private lateinit var userBusTicketAdapter: UserBusTicketAdapter
    private lateinit var userBusTicketList: ArrayList<UserBusTicket>
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_ticket)
        val bundle = intent.extras

        // Extract the data from the Bundle
        if (bundle != null) {
            userId = bundle.getInt("id")
        }
        // Initialize other UI components
        initializeUIComponents()

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView = findViewById<RecyclerView>(R.id.rv2)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userBusTicketList = ArrayList()
        userBusTicketAdapter = UserBusTicketAdapter(userBusTicketList)
        recyclerView.adapter = userBusTicketAdapter

        swipeRefreshLayout.setOnRefreshListener {
            fetchUserBusTickets(selectedRoute, userId.toString(), userCat)
        }



        fetchUserBusTickets(selectedRoute, userId.toString(), userCat) // Fetch tickets with the default route
        val spin2 = findViewById<Spinner>(R.id.spinner2)
    }

    private fun initializeUIComponents() {
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

        // Fetch single use bus tickets
        BusTicket.getSingleUseBusTickets(this) { singleUseBusTickets ->
            singleUseBusTicketList = singleUseBusTickets
        }
        BusTicket.getWeeklyBusTickets(this) { weeklyBusTickets ->
            weeklyBusTicketList = weeklyBusTickets
        }
        BusTicket.getMonthlyBusTickets(this) { singleUseBusTickets ->
            monthlyBusTicketList = singleUseBusTickets
        }

        val spin1 = findViewById<Spinner>(R.id.spinner1)
        spin1.onItemSelectedListener = this
        // Initialize the second spinner for bus lines
        val spin2 = findViewById<Spinner>(R.id.spinner2)
        spin2.onItemSelectedListener = this

        // Fetch bus lines and update the spinner
        BusLine.getBusLines(this) { busLines ->
            busLineList = busLines

            val busLineStrings = busLines.map { "Line: ${it.id}" }
            val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, busLineStrings)
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spin2.adapter = adapter2
            spin1.adapter = adapter2
        }



        val buyWeek = findViewById<Button>(R.id.buy_week)
        val buyMonth = findViewById<Button>(R.id.buy_month)
        val buySingle = findViewById<Button>(R.id.buy_single)

        User.getUserCategory(this, userId) { category ->
            if (category != null) {
                userCat = category
            }
        }
        buyWeek.setOnClickListener {
            // Call insertBusTicket when the button is clicked
            val routeID = selectedBusLine.id
            insertUserBusTicket(routeID, userId, userCat, "weekly", 0)
        }
        buySingle.setOnClickListener {
            // Call insertBusTicket when the button is clicked
            val routeID = selectedBusLine.id
            insertUserBusTicket(routeID, userId, userCat, "single", selectedAmount)
        }
        buyMonth.setOnClickListener {
            // Call insertBusTicket when the button is clicked
            val routeID = selectedBusLine.id
            insertUserBusTicket(routeID, userId, userCat, "monthly", 0)
        }

    }

    private fun fetchUserBusTickets(route: String, userId: String, userCat: String) {
        UserBusTicket.getUserBusTickets(this, route, userId, userCat) { userBusTickets ->
            if (userBusTickets != null) {
                Log.d("UserBusTickets", "UserBusTickets: $userBusTickets")
                userBusTicketList.clear()
                userBusTicketList.addAll(userBusTickets)
                userBusTicketAdapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false // Stop the refreshing animation
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.spinner1 -> {
                // Handle the spinner1 selection
                selectedRoute = busLineList[position].id.toString()
                fetchUserBusTickets(selectedRoute, userId.toString(), userCat)

                val selectedBusLine = busLineList[position]
                val lineTextView = findViewById<TextView>(R.id.line_tv2)
                lineTextView.text = "Line: ${selectedBusLine.id}"// Fetch tickets with the selected route
            }
            R.id.spinner2 -> {
                // Handle the bus line spinner selection
                if (::busLineList.isInitialized) {
                    selectedBusLine = busLineList[position]
                    val lineTextView = findViewById<TextView>(R.id.line_tv)
                    lineTextView.text = "Line: ${selectedBusLine.id}"

                    // Find the matching SingleUseBusTicket for the selected route
                    if (::singleUseBusTicketList.isInitialized && ::monthlyBusTicketList.isInitialized) {
                        val selectedSingleTicket = singleUseBusTicketList.find { it.route == selectedBusLine.id }
                        val selectedWeeklyTicket = weeklyBusTicketList.find { it.route == selectedBusLine.id }
                        val selectedMonthlyTicket = monthlyBusTicketList.find { it.route == selectedBusLine.id }

                        val priceSingleTextView = findViewById<TextView>(R.id.price_single)
                        val priceWeeklyTextView = findViewById<TextView>(R.id.price_day)
                        val priceMonthlyTextView = findViewById<TextView>(R.id.price_week)

                        if (selectedSingleTicket != null) {
                            selectedSingleTicketCost = selectedSingleTicket.cost
                            priceSingleTextView.text = "${selectedSingleTicket.cost}$"
                        } else {
                            selectedSingleTicketCost = null
                            priceSingleTextView.text = "Single: N/A"
                        }

                        if (selectedWeeklyTicket != null) {
                            selectedWeeklyTicketCost = selectedWeeklyTicket.cost
                            priceWeeklyTextView.text = "${selectedWeeklyTicket.cost}$"
                        } else {
                            selectedWeeklyTicketCost = null
                            priceWeeklyTextView.text = "Weekly: N/A"
                        }

                        if (selectedMonthlyTicket != null) {
                            selectedMonthlyTicketCost = selectedMonthlyTicket.cost
                            priceMonthlyTextView.text = "${selectedMonthlyTicket.cost}$"
                        } else {
                            selectedMonthlyTicketCost = null
                            priceMonthlyTextView.text = "Monthly: N/A"
                        }
                        updateTotalCost()
                    }
                }


            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Do nothing
    }

    private fun updateTotalCost() {
        val priceSingleXAmountTextView = findViewById<TextView>(R.id.price_xamount)
        if (selectedSingleTicketCost != null) {
            val totalCost = selectedSingleTicketCost!! * selectedAmount
            priceSingleXAmountTextView.text = "Total: $totalCost$"
        } else {
            priceSingleXAmountTextView.text = "Select line first."
        }
    }


    private fun insertUserBusTicket(route: Int, userId: Int, userCat: String, duration: String, number: Int) {
        Log.d("InsertBusTicket", "Route ID: $route, User ID: $userId, User Category: $userCat, Duration: $duration, Number: $number")

        val api = ApiClient.apiService
        val call = api.insertUserBusTicket(route, userId, userCat, duration, number)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (responseBody != null) {
                        Log.d("InsertBusTicket", "Response: $responseBody")
                        // Handle successful insertion
                        showToast("Bus ticket inserted successfully")
                    } else {
                        Log.d("InsertBusTicket", "Null response received.")
                        // Provide feedback to the user (optional)
                        showToast("Failed to insert bus ticket: Null response")
                    }
                } else {
                    Log.d("InsertBusTicket", "Failed to insert bus ticket. Error code: ${response.code()}")
                    // Provide feedback to the user (optional)
                    showToast("Failed to insert bus ticket. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("InsertBusTicket", "Failed to insert bus ticket", t)
                // Provide feedback to the user (optional)
                showToast("Failed to insert bus ticket. Please try again later.")
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(this@BuyBusTickets, message, Toast.LENGTH_SHORT).show()
    }

}
