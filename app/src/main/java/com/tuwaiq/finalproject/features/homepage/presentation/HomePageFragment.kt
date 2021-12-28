package com.tuwaiq.finalproject.features.homepage.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuwaiq.finalproject.R
import com.tuwaiq.finalproject.core.domain.model.Post
import com.tuwaiq.finalproject.core.util.Constant.mAdapter
import com.tuwaiq.finalproject.core.util.Constant.postCollectionRef
import com.tuwaiq.finalproject.databinding.HomePageFragmentBinding
import com.tuwaiq.finalproject.databinding.HomePageItemsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "HomePageFragment"
@AndroidEntryPoint
class HomePageFragment : Fragment() {



    private val homePageViewModel: HomePageViewModel by viewModels()

    private lateinit var binding : HomePageFragmentBinding

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }
    private var clicked = false



    private fun onAddButtonClicked(){
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
       if (!clicked){
           binding.cars.startAnimation(fromBottom)
           binding.electronic.startAnimation(fromBottom)
           binding.furniture.startAnimation(fromBottom)
           binding.clothes.startAnimation(fromBottom)
           binding.realestate.startAnimation(fromBottom)

       }else{
           binding.cars.startAnimation(toBottom)
           binding.electronic.startAnimation(toBottom)
           binding.furniture.startAnimation(toBottom)
           binding.clothes.startAnimation(toBottom)
           binding.realestate.startAnimation(toBottom)
       }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = HomePageFragmentBinding.inflate(layoutInflater)

        binding.viewModel = homePageViewModel

        binding.homeRv.layoutManager = LinearLayoutManager(context)






        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        homePageViewModel.getPost(requireContext(), 100.0f).observe(
            viewLifecycleOwner, {
                binding.shimmerLayout.visibility = View.GONE
                mAdapter = HomeAdapter(it as ArrayList<Post>)

                binding.homeRv.adapter = mAdapter
            }
        )



        binding.threeKm.setOnClickListener {
            homePageViewModel.getPost(requireContext(), 3000.0f).observe(
                viewLifecycleOwner, {
                    binding.shimmerLayout.visibility = View.GONE
                    mAdapter = HomeAdapter(it as ArrayList<Post>)

                    binding.homeRv.adapter = mAdapter
                }
            )

        }


        binding.fiveKm.setOnClickListener {
            homePageViewModel.getPost(requireContext(), 5000.0f).observe(
                viewLifecycleOwner, {
                    binding.shimmerLayout.visibility = View.GONE
                    mAdapter = HomeAdapter(it as ArrayList<Post>)

                    binding.homeRv.adapter = mAdapter
                }
            )
        }











    }



    override fun onStart() {
        super.onStart()

        binding.floatingActionButton2.setOnClickListener {
            onAddButtonClicked()
        }

        val navCon = findNavController()
        binding.homeProfileBtn.setOnClickListener {
            val action = HomePageFragmentDirections.actionHomePageFragmentToMyProfileFragment()
            navCon.navigate(action)
        }

        binding.floatingActionButton.setOnClickListener {
            val action = HomePageFragmentDirections.actionHomePageFragmentToItemsFragment()
            navCon.navigate(action)
        }

        binding.homeDmBtn.setOnClickListener {
            val action = HomePageFragmentDirections.actionHomePageFragmentToDirectMessageFragment()
            navCon.navigate(action)
        }
    }





    inner class HomeHolder(val binding: HomePageItemsBinding):RecyclerView.ViewHolder(binding.root), View.OnClickListener{


        init {

            binding.viewModel = homePageViewModel

            itemView.setOnClickListener(this)
        }

        fun bind(post: Post){

           binding.viewModel?.post = post

        }

        override fun onClick(v: View?) {
            if (v == itemView){
                val navCon1 =  findNavController()
                
                val action = HomePageFragmentDirections
                    .actionHomePageFragmentToPreviewFragment(
                    postCollectionRef.document().toString()
                )
                Log.d(TAG, "onClick: $action")
                navCon1.navigate(action)
            }
        }



    }



    inner class HomeAdapter(val posts: ArrayList<Post>): RecyclerView.Adapter<HomeHolder>(), Filterable {

        val allPost: MutableList<Post> = mutableListOf()

        init {
            posts.forEach { allPost.add(it) }



        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
            val binding = HomePageItemsBinding.inflate(
                layoutInflater,
                parent,
                false
            )

            return HomeHolder(binding)



        }

        override fun onBindViewHolder(holder: HomeHolder, position: Int) {
           val post = posts[position]
            holder.bind(post)
        }

        override fun getItemCount(): Int = posts.size


        override fun getFilter(): Filter {
            return newFilter
        }

        private val newFilter = object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList: MutableList<Post> = mutableListOf()


                if (constraint == null || constraint.isEmpty()) {
                    filteredList.addAll(allPost)
                } else {
                    val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                    for (item in allPost) {
                        Log.e(TAG, "performFiltering: ${item.categoryName}")
                        if (item.categoryName.lowercase(Locale.getDefault()).contains(filterPattern)) {
                            Log.e(TAG, "performFiltering: hi")
                            filteredList.add(item)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                posts.clear()
                posts.addAll(results!!.values as List<Post>)
                notifyDataSetChanged()
            }

        }
    }


}