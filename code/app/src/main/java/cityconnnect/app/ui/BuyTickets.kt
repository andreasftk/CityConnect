package cityconnnect.app.ui

import cityconnnect.app.R
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cityconnnect.app.BusLine
import cityconnnect.app.BusTicket

class BuyTickets : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var amount: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    private lateinit var busLineList: ArrayList<BusLine>
    private lateinit var singleUseBusTicketList: ArrayList<BusTicket>
    private lateinit var weeklyBusTicketList: ArrayList<BusTicket>
    private lateinit var monthlyBusTicketList: ArrayList<BusTicket>
    private var selectedSingleTicketCost: Float? = null
    private var selectedWeeklyTicketCost: Float? = null
    private var selectedMonthlyTicketCost: Float? = null
    private var selectedAmount: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_ticket)

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

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {

            R.id.spinner2 -> {
                // Handle the bus line spinner selection
                if (::busLineList.isInitialized) {
                    val selectedBusLine = busLineList[position]
                    val lineTextView = findViewById<TextView>(R.id.line_tv)
                    lineTextView.text = "Line: ${selectedBusLine.id}"

                    // Find the matching SingleUseBusTicket for the selected route
                    if (::singleUseBusTicketList.isInitialized && ::monthlyBusTicketList.isInitialized) {
                        val selectedSingleTicket = singleUseBusTicketList.find { it.route == selectedBusLine.id }
                        val selectedWeeklyTicket = weeklyBusTicketList.find { it.route == selectedBusLine.id }
                        val selectedMonthlyTicket = monthlyBusTicketList.find { it.route == selectedBusLine.id }

                        val priceSingleTextView = findViewById<TextView>(R.id.price_single)
                        val priceWeeklyTextView = findViewById<TextView>(R.id.price_week)
                        val priceMonthlyTextView = findViewById<TextView>(R.id.price_month)

                        if (selectedSingleTicket != null) {
                            selectedSingleTicketCost = selectedSingleTicket.cost
                            priceSingleTextView.text = "Single: ${selectedSingleTicket.cost}$"
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


    private fun updateTotalCost() {
        val priceSingleXAmountTextView = findViewById<TextView>(R.id.price_single_xamount)
        if (selectedSingleTicketCost != null) {
            val totalCost = selectedSingleTicketCost!! * selectedAmount
            priceSingleXAmountTextView.text = "Total: $totalCost$"
        } else {
            priceSingleXAmountTextView.text = "Select line first."
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Do nothing
    }
}
