package com.tuwaiq.finalproject.features.post.presentation.preview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.tuwaiq.finalproject.core.domain.model.Post
import com.tuwaiq.finalproject.databinding.PreviewFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewFragment : Fragment() {



    private val previewViewModel:PreviewViewModel by viewModels()


    private val args:PreviewFragmentArgs by navArgs()
    lateinit var id : String
    var post = Post()

    lateinit var binding: PreviewFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PreviewFragmentBinding.inflate(layoutInflater)



         id = args.id


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = previewViewModel
        previewViewModel.getPost(requireContext(), 5000.0f).observe(
            viewLifecycleOwner, {


            }
        )

    }



}