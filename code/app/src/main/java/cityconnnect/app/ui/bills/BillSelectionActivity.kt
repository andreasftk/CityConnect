package cityconnnect.app.ui.bills

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import cityconnnect.app.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BillSelectionActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var userId: Int = -1 // Class-level variable to store user ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_selection)

        // Retrieve the Bundle from the Intent
        val bundle = intent.extras

        // Extract the data from the Bundle
        if (bundle != null) {
            userId = bundle.getInt("id")
        }

        // Load the initial fragment with userId
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PendingBillsFragment.newInstance(userId))
            .commit()

        // Set listener for bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.billBottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_pending_bills -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PendingBillsFragment.newInstance(userId))
                    .commit()
                return true
            }
            R.id.menu_paid_bills -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PaidBillsFragment.newInstance(userId))
                    .commit()
                return true
            }
        }
        return false
    }
}
