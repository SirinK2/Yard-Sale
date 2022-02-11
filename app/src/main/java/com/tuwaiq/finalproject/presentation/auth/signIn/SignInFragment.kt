package com.tuwaiq.finalproject.presentation.auth.signIn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tuwaiq.finalproject.R
import com.tuwaiq.finalproject.databinding.SignInFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SingInFragment"
@AndroidEntryPoint
class SignInFragment : Fragment() {



    private lateinit var binding: SignInFragmentBinding
    private val viewModel: SignInViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignInFragmentBinding.inflate(layoutInflater)

        return binding.root
    }


    override fun onStart() {
        super.onStart()

        binding.singinBtn.setOnClickListener {

            val email = binding.singinEmailEt.text.toString()
            val password = binding.singinPasswordEt.text.toString()


            validateSignIn(email, password)



        }

        binding.toRegisterTv.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        binding.resendPasswordTv.setOnClickListener {



        }



    }


    private fun validateSignIn(email:String, password:String){
        when{
            email.isEmpty() -> binding.signinEmailTil.error = getString(R.string.enter_email)
            password.isEmpty() -> binding.signinPasswordTil.error = getString(R.string.enter_password)

            else -> {
                viewModel.signIn(email, password).addOnCompleteListener {
                    if (it.isSuccessful){
//                        findNavController().popBackStack(R.id.homePageFragment,true)
                        findNavController().navigate(R.id.action_singInFragment_to_homePageFragment)
                    }
                }.addOnFailureListener {

                    Snackbar.make(binding.root,it.localizedMessage,Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }



}