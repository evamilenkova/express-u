package mk.ukim.finki.expressu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import mk.ukim.finki.expressu.databinding.ActivityLoginPhoneNumberBinding

class LoginPhoneNumberActivity : AppCompatActivity() {

    private var _binding: ActivityLoginPhoneNumberBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val countryCode = binding.loginCountryCode
        val phoneNumber = binding.loginMobileNumber
        binding.loginProgressBar.visibility = View.GONE


        countryCode.registerCarrierNumberEditText(phoneNumber)
        binding.sendOtpButton.setOnClickListener {
            if (!countryCode.isValidFullNumber) {
                phoneNumber.error = "Phone Number Not Valid"
            } else {
                intent = Intent(this, LoginOtpActivity::class.java)
                intent.putExtra("phone", countryCode.fullNumberWithPlus)
                startActivity(intent)
            }
        }
    }
}
