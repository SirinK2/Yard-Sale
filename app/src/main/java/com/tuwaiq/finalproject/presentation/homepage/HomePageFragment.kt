package com.tuwaiq.finalproject.presentation.homepage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tuwaiq.finalproject.R
import com.tuwaiq.finalproject.databinding.HomePageFragmentBinding
import com.tuwaiq.finalproject.databinding.HomePageItemsBinding
import com.tuwaiq.finalproject.domain.model.Post
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val TAG = "HomePageFragment"
@AndroidEntryPoint
class HomePageFragment : Fragment() {

    lateinit var mAdapter: HomePageFragment.HomeAdapter

    private val homePageViewModel: HomePageViewModel by viewModels()

    private lateinit var binding : HomePageFragmentBinding


    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }
    private var clicked = false




    private fun onAddButtonClicked(){
        visibility(clicked)
        setAnimation(clicked)
        clickable(clicked)
        clicked = !clicked
    }


    private fun clickable(clicked: Boolean){
        if (!clicked){
            binding.floatingActionButton2.isClickable = true
            binding.cars.isCheckable = true
            binding.furniture.isCheckable = true
            binding.electronic.isCheckable = true
            binding.clothes.isCheckable =true
            binding.realestate.isCheckable = true

        }else{
            binding.floatingActionButton2.isClickable = false
            binding.cars.isCheckable = false
            binding.furniture.isCheckable = false
            binding.electronic.isCheckable = false
            binding.clothes.isCheckable =false
            binding.realestate.isCheckable = false

        }
    }

    private fun visibility(clicked: Boolean){
        if (!clicked){

            binding.cars.visibility = View.VISIBLE
            binding.furniture.visibility = View.VISIBLE
            binding.electronic.visibility = View.VISIBLE
            binding.clothes.visibility = View.VISIBLE
            binding.realestate.visibility = View.VISIBLE

        }else{
            binding.cars.visibility = View.INVISIBLE
            binding.furniture.visibility = View.INVISIBLE
            binding.electronic.visibility = View.INVISIBLE
            binding.clothes.visibility = View.INVISIBLE
            binding.realestate.visibility = View.INVISIBLE


        }
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


    private fun observe(dis: Float){
        homePageViewModel.getPost(requireContext(), dis).observe(
            viewLifecycleOwner, {
                binding.shimmerLayout.visibility = View.GONE
                mAdapter = HomeAdapter(it as ArrayList<Post>)

                binding.homeRv.adapter = mAdapter
                Log.d(TAG, "onViewCreated: $dis")
            }
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        observe(100.0f)


        binding.threeKm.setOnClickListener {
           observe(3000.0f)
        }


        binding.fiveKm.setOnClickListener {
           observe(5000.0f)

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

        binding.settingBtn.setOnClickListener {
            findNavController().navigate(R.id.settingFragment)
        }


        binding.cars.setOnClickListener {
            mAdapter.filter.filter("cars")
        }

       binding.electronic.setOnClickListener {
           mAdapter.filter.filter("electronic")
       }



        binding.furniture.setOnClickListener {
            mAdapter.filter.filter("furniture")

        }

        binding.clothes.setOnClickListener {
            mAdapter.filter.filter("clothes")
        }

        binding.realestate.setOnClickListener {
            mAdapter.filter.filter("real estate")
        }

    }






    inner class HomeHolder(val binding: HomePageItemsBinding):RecyclerView.ViewHolder(binding.root), View.OnClickListener{

        var post = Post()
        init {

            binding.viewModel = homePageViewModel

            itemView.setOnClickListener(this)
        }

        fun bind(post: Post){


            this.post = post

            binding.homeTitleTv.text = post.title
            binding.homePriceTv.text = post.price
            post.photoUrl.forEach {
                binding.homeItemIv.load(it)
            }


        }



        override fun onClick(v: View?) {





            if (v == itemView){
                val navCon1 =  findNavController()
                
                val action = HomePageFragmentDirections
                    .actionHomePageFragmentToPreviewFragment(post.id)

                navCon1.navigate(action)

                Log.d(TAG, "onClick: $action")
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

                        if (item.categoryName.lowercase(Locale.getDefault()) == filterPattern) {
                            Log.e(TAG, "performFiltering: ${item.categoryName}   $filterPattern  ${item.title}")

                            filteredList.add(item)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList

            //    Log.e(TAG, "performFiltering: $results", )

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