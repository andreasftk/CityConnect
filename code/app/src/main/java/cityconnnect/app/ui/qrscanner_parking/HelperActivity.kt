package cityconnnect.app.ui.qrscanner_parking


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cityconnnect.app.R
import cityconnnect.app.databinding.ActivityHelperBinding

class HelperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelperBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.rootContainer, QRCodeFragment())
            .commit()

    }


}
