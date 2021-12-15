package com.tuwaiq.finalproject.presentation.profile.presentation.myProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.tuwaiq.finalproject.core.data.firebase.User
import com.tuwaiq.finalproject.core.data.firebase.Rating
import com.tuwaiq.finalproject.databinding.MyProfileFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyProfileFragment : Fragment() {



    private val viewModel: MyProfileViewModel by viewModels()

    lateinit var binding: MyProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyProfileFragmentBinding.inflate(layoutInflater)

        binding.myProfButton.setOnClickListener {
            val name = binding.myProfNameTv.text.toString()
            val bio = binding.myProfBioTv.text.toString()
            val rate: List<Rating> = listOf()

            for (i in rate){
                binding.myProfTextRating.text.toString().toFloat()
            }

            val user = User(name,bio,rate)


                viewModel.saveUser(user)


        }

        return binding.root
    }







}