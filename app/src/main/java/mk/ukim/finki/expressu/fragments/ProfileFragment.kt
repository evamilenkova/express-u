package mk.ukim.finki.expressu.fragments

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mk.ukim.finki.expressu.SplashActivity
import mk.ukim.finki.expressu.databinding.FragmentProfileBinding
import mk.ukim.finki.expressu.model.User
import mk.ukim.finki.expressu.utils.AndroidUtil
import mk.ukim.finki.expressu.utils.FirebaseUtil

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var user: User
    private var selectedImageUri: Uri? = null
    private lateinit var imagePickLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val view = binding.root

        getUserData()

        binding.updateProfileBtn.setOnClickListener {

            updateBtnClick()
        }

        binding.logoutBtn.setOnClickListener {
            FirebaseUtil.logout()
            val intent = Intent(context, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.profileImage.setOnClickListener {
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
                                binding.profileImage
                            )
                        }
                    }
                }
            }
    }

    fun updateBtnClick() {
        val newUsername = binding.profileUsername.text.toString()
        if (newUsername.isEmpty() || newUsername.length < 3) {
            binding.profileUsername.error = "Username length should be at least 3 characters"
            return
        }
        user.username = newUsername
        //set progress true

        selectedImageUri?.let {
            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(it)
                .addOnCompleteListener { _ ->
                    updateToFirestore()
                }
        } ?: updateToFirestore()

        //set progress false
    }


    fun updateToFirestore() {
        FirebaseUtil.currentUserDetails()?.set(user)?.addOnCompleteListener {
            if (it.isSuccessful) {
                AndroidUtil.showToast(requireContext(), "Updated Successfully")
            } else {
                AndroidUtil.showToast(requireContext(), " Update failed")
            }
        }
    }

    fun getUserData() {
        //setProgres(true)

        FirebaseUtil.getCurrentProfilePicStorageRef().downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                AndroidUtil.setProfilePic(requireContext(), task.result, binding.profileImage)
            }
        }
        FirebaseUtil.currentUserDetails()?.get()?.addOnCompleteListener {
//          setProgress(false)
            user = it.getResult().toObject(User::class.java)!!
            binding.profileUsername.setText(user.username)
            binding.profileMobileNumber.setText(user.phone)
            // image of the user
        }
    }

//    suspend fun setProgressBar(isVisible: Boolean) {
//        withContext(Dispatchers.Main) {
//            binding.loginNextBtn.visibility = if (isVisible) View.GONE else View.VISIBLE
//        }
//    }

}