package com.example.hackersnewsapp.ui.main

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackersnewsapp.R
import com.example.hackersnewsapp.dao.ResponseStatus
import com.example.hackersnewsapp.databinding.FragmentMainBinding
import com.example.hackersnewsapp.model.Story
import org.w3c.dom.Text

class MainFragment : Fragment(),TopStoriesAdapter.ActionListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private var _binding: FragmentMainBinding? = null
    private var topStoriesAdapter:TopStoriesAdapter? = null
    private var topStoriesList:List<String>? = null
    private var alertDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayoutManager()

        getTopStories()

        _binding?.edtxTopStoryId?.addTextChangedListener(object :TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {

                val topStoryId = topStoriesList?.filter { it -> it.contains(p0.toString())}

                if(!topStoryId?.isEmpty()!!){
                    _binding?.idMessage?.visibility = View.GONE
                    _binding?.topStoriesRecyclerView?.visibility = View.VISIBLE
                    updateAdapter(topStoryId)
                }else {
                    _binding?.idMessage?.visibility = View.VISIBLE
                    _binding?.topStoriesRecyclerView?.visibility = View.GONE
                    _binding?.idMessage?.text = "No Stories Found"
                }

            }

        })


        viewModel.observeTopStoryLiveData().observe(requireActivity(), Observer { tpStryList ->
            _binding?.idProgressBar?.visibility = View.GONE
            if(tpStryList.id==ResponseStatus.SUCCESS){
                _binding?.swipeRefreshTopStory?.isRefreshing = false
                _binding?.idMessage?.visibility = View.GONE
                _binding?.topStoriesRecyclerView?.visibility = View.VISIBLE

                topStoriesList = tpStryList?.topStoryId
                if(!topStoriesList?.isEmpty()!!){
                    topStoriesAdapter = TopStoriesAdapter(requireContext(),topStoriesList!!,this)
                    _binding?.topStoriesRecyclerView?.adapter = topStoriesAdapter
                }else{
                    _binding?.idMessage?.text = "No Storied found"
                }
            }else{
                _binding?.idMessage?.visibility = View.VISIBLE
                _binding?.topStoriesRecyclerView?.visibility = View.GONE
                _binding?.idMessage?.text = tpStryList?.topStoryId?.get(0)?:"Something went wrong"
            }
        })

        _binding?.swipeRefreshTopStory?.setOnRefreshListener {
            getTopStories()
        }
    }

    private fun updateAdapter(lst:List<String>){
        topStoriesAdapter = TopStoriesAdapter(requireContext(),lst!!,this)
        _binding?.topStoriesRecyclerView?.adapter = topStoriesAdapter
    }

    private fun getTopStories() {
        if(isNetworkAvailable(requireContext())){
            _binding?.idProgressBar?.visibility = View.VISIBLE
            viewModel.getTopStoriesFromApi()
        }else{
            _binding?.idProgressBar?.visibility = View.VISIBLE
            viewModel.getTopStoriesFromRoomDb()
        }
    }

    private fun setLayoutManager() {
        // create  layoutManager
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 3)
        // pass it to  layoutManager
        _binding?.topStoriesRecyclerView?.layoutManager = layoutManager
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    override fun onClickTopStory(topStoryId: String, position: Int) {

        _binding?.idProgressBar?.visibility = View.VISIBLE
        viewModel.getStoryFromApi(topStoryId).observe(requireActivity()) { it ->
            _binding?.idProgressBar?.visibility = View.GONE
            if(it.status == ResponseStatus.SUCCESS){
                if(it.data!=null){
                    var data = it.data as Story
                    val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog)

                    val view = layoutInflater.inflate(R.layout.item_story,null)
                    val  storyType = view.findViewById<TextView>(R.id.storyType)
                    val storyId = view.findViewById<TextView>(R.id.storyId)
                    storyType.text = data.type?:""
                    storyId.text = data.id?:""
                    val  storyTitle = view.findViewById<TextView>(R.id.storyTitle)
                    storyTitle.text = data.title?:""
                    builder.setView(view)
                    val close = view.findViewById<ImageView>(R.id.icClose)

                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(true)
                    alertDialog.show()
                    close.setOnClickListener {
                        alertDialog.dismiss()
                    }
                }else{
                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_LONG).show()
                }
            }else if(it.status == ResponseStatus.FAIL){
                Toast.makeText(requireContext(),it.data.toString(),Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(),it.throwable.toString(),Toast.LENGTH_LONG).show()
            }
        }
    }




}