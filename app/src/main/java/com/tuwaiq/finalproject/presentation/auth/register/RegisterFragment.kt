package com.tuwaiq.finalproject.presentation.auth.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tuwaiq.finalproject.R
import com.tuwaiq.finalproject.databinding.RegisterFragmentBinding
import com.tuwaiq.finalproject.domain.model.User
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding : RegisterFragmentBinding

    private val viewModel : RegisterViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegisterFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

         val navControl = findNavController()

        binding.registerBtn.setOnClickListener {

            val userName:String = binding.registerUsernameEt.text.toString()
            val email:String = binding.registerEmailEt.text.toString()
            val password:String = binding.registerPasswordEt.text.toString()
            val confirmPass: String = binding.registerComPassEt.text.toString()

            validateRegistration(userName,email, password, confirmPass)




        }

        binding.toSinginTv.setOnClickListener {
            navControl.navigate(RegisterFragmentDirections.actionRegisterFragmentToSingInFragment())
        }

    }


    private fun validateRegistration(userName: String, email:String, password: String, confirmPass:String){

        when{
            userName.isEmpty() -> binding.registerUsernameEt.error = "enter username"
            email.isEmpty() -> binding.registerEmailEt.error = "please enter email"
            password.isEmpty() -> binding.registerPasswordEt.error = "please enter password"
            password != confirmPass -> binding.apply {
                registerPasswordEt.error = "passwords must be matched"
                registerComPassEt.error = "passwords must be matched"
            }
            else -> {
                viewModel.register(email,password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val c = Firebase.auth.currentUser?.uid.toString()
                        viewModel.addUser(User(c,userName))
                        findNavController().navigate(R.id.action_registerFragment_to_homePageFragment)
                    }

                }.addOnFailureListener {
                    Toast.makeText(requireContext(),it.localizedMessage, Toast.LENGTH_LONG).show()
                    Log.e(TAG, "onStart: $it", )

                }
            }

        }
    }


}