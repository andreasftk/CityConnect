package cityconnnect.app.ui.bills

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import cityconnnect.app.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class BillSelectionActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_selection)

        // Load the initial fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, PendingBillsFragment())
            .commit()

        // Set listener for bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.billBottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_pending_bills -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PendingBillsFragment())
                    .commit()
                return true
            }
            R.id.menu_paid_bills -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PaidBillsFragment())
                    .commit()
                return true
            }
        }
        return false
    }
}


