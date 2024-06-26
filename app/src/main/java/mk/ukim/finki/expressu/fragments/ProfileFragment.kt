package mk.ukim.finki.expressu.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.messaging.FirebaseMessaging
import mk.ukim.finki.expressu.SplashActivity
import mk.ukim.finki.expressu.databinding.FragmentProfileBinding
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.translationUtils.LanguageManager
import mk.ukim.finki.expressu.utils.AndroidUtil
import mk.ukim.finki.expressu.utils.FirebaseUtil

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var user: User
    private lateinit var languageSpinner: Spinner
    private var selectedImageUri: Uri? = null
    private lateinit var imagePickLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val view = binding.root
        languageSpinner = binding.languageSpinner
        setupLanguageSpinner()

        getUserData()

        binding.updateProfileBtn.setOnClickListener {
            updateBtnClick()
        }

        binding.logoutBtn.setOnClickListener {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        FirebaseUtil.logout()
                        val intent = Intent(context, SplashActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }

        }

        binding.profileImageView.setOnClickListener {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512).createIntent {
                imagePickLauncher.launch(it)
            }
        }



        return view

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null && data.data != null) {
                        data.data?.let {
                            selectedImageUri = it
                            AndroidUtil.setProfilePic(
                                requireContext(),
                                it,
                                binding.profileImageView
                            )
                        }
                    }
                }
            }
    }

    private fun setupLanguageSpinner() {
        val languages = LanguageManager.getLanguages()
        val languageNames = languages.map { it.name }.toList()

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            languageNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

    }

    private fun updateBtnClick() {
        val newUsername = binding.profileUsername.text.toString()
        if (newUsername.isEmpty() || newUsername.length < 3) {
            binding.profileUsername.error = "Username length should be at least 3 characters"
            return
        }
        user.username = newUsername
        setProgressBar(true)
        val selectedLanguage = languageSpinner.selectedItemPosition
        val languageCode = LanguageManager.getLanguages()[selectedLanguage].code
        user.language = languageCode

        selectedImageUri?.let {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(it).addOnCompleteListener { _ ->
                updateToFirestore()
            }
        } ?: updateToFirestore()

        setProgressBar(false)
    }


    private fun updateToFirestore() {
        FirebaseUtil.currentUserDetails()?.set(user)?.addOnCompleteListener {
            if (it.isSuccessful) {
                AndroidUtil.showToast(requireContext(), "Updated Successfully")
            } else {
                AndroidUtil.showToast(requireContext(), " Update failed")
            }
        }
    }

    private fun getUserData() {
        setProgressBar(true)

        FirebaseUtil.currentUserDetails()?.get()?.addOnCompleteListener { it ->
            setProgressBar(false)
            user = it.result.toObject(User::class.java)!!
            binding.profileUsername.setText(user.username)
            binding.profileMobileNumber.setText(user.phone)
            val userLanguage = user.language
            val position = LanguageManager.getLanguages().indexOfFirst { it.code == userLanguage }
            if (position >= 0) {
                binding.languageSpinner.setSelection(position)
            }
            FirebaseUtil.getCurrentProfilePicStorageRef().downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    AndroidUtil.setProfilePic(
                        requireContext(),
                        task.result,
                        binding.profileImageView
                    )
                } else {
                    val exception = task.exception
        //                    AndroidUtil.showToast(requireContext(), exception.toString())
                }
            }

        }

    }

    fun setProgressBar(isVisible: Boolean) {
        binding.profileProgressBar.visibility = if (isVisible) View.VISIBLE else View.GONE

    }
}