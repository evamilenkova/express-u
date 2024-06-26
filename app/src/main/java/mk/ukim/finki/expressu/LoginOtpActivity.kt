package mk.ukim.finki.expressu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import mk.ukim.finki.expressu.databinding.ActivityLoginOtpBinding
import mk.ukim.finki.expressu.utils.AndroidUtil
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

class LoginOtpActivity : AppCompatActivity() {
    private var _binding: ActivityLoginOtpBinding? = null
    private val binding get() = _binding!!
    private var phoneNumber: String? = null
    private var timeout: Long = 60L;
    private var verificationCode: String = ""
    private lateinit var resendingToken: ForceResendingToken
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("phone")
        val otp = binding.loginOtp

        phoneNumber?.let {
            sendOtp(it, false)
            // AndroidUtil.showToast(applicationContext, phoneNumber.toString())
        }

        binding.loginNextBtn.setOnClickListener {
            val enteredOtp = otp.text.toString()
            val credentials = PhoneAuthProvider.getCredential(verificationCode, enteredOtp)
            signIn(credentials)
        }

//        binding.resendOtpTextBtn.setOnClickListener{
//           phoneNumber?.let{ sendOtp(phoneNumber!!,true) }
//        }

    }


    private fun sendOtp(phoneNumber: String, isResend: Boolean) {

        //startResendTimer()
        setProgressBar(true)
        CoroutineScope(Dispatchers.Main).launch {

            val phoneAuthOptions = withContext(Dispatchers.Default) {
                val builder = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(timeout, TimeUnit.SECONDS)
                    .setActivity(this@LoginOtpActivity)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                            signIn(phoneAuthCredential)
                        }

                        override fun onVerificationFailed(p0: FirebaseException) {
                            AndroidUtil.showToast(applicationContext, "OTP verification failed")
                        }

                        override fun onCodeSent(
                            p0: String,
                            p1: PhoneAuthProvider.ForceResendingToken
                        ) {
                            super.onCodeSent(p0, p1)
                            verificationCode = p0
                            resendingToken = p1
                            AndroidUtil.showToast(
                                applicationContext,
                                "OTP verification sent successfully"
                            )
                        }
                    })
                setProgressBar(false)
                if (isResend) {
                    builder.setForceResendingToken(resendingToken).build()
                } else {
                    builder.build()
                }
            }

            PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
        }
    }

    private fun setProgressBar(isVisible: Boolean) {
        runOnUiThread {
            binding.loginProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            binding.loginNextBtn.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
    }

    private fun signIn(phoneAuthCredential: PhoneAuthCredential) {
        setProgressBar(true)

        mAuth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginOtpActivity, LoginUsernameActivity::class.java)
                    intent.putExtra("phone", phoneNumber)
                    startActivity(intent)
                } else {
                    AndroidUtil.showToast(applicationContext, "OTP verification failed")
                }
            }

    }

//    fun startResendTimer() {
//        val resendOtpTextView = binding.resendOtpTextBtn
//        resendOtpTextView.isEnabled = false
//        val timer = Timer()
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                timeout--
//                runOnUiThread {
//                    resendOtpTextView.text = "Resend OTP in $timeout seconds"
//                }
//                if (timeout <= 0) {
//                    timeout = 60L
//                    timer.cancel()
//                    runOnUiThread {
//                        resendOtpTextView.isEnabled = true
//                    }
//                }
//            }
//        }, 0, 1000)
//    }


}