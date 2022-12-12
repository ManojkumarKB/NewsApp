package com.example.hackersnewsapp.ui.main

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hackersnewsapp.R
import com.example.hackersnewsapp.dao.ResponseStatus
import com.example.hackersnewsapp.databinding.FragmentMainBinding
import com.example.hackersnewsapp.model.Story
import retrofit2.HttpException
import java.io.IOException
import java.util.ArrayList

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

        filterTextWatcher()

        observerTopStoryLiveData()

        _binding?.swipeRefreshTopStory?.setOnRefreshListener {
            _binding?.edtxTopStoryId?.setText("")
            getTopStories()
        }
    }

    private fun observerTopStoryLiveData() {
        viewModel.observeTopStoryLiveData().observe(requireActivity(), Observer { tpStryList ->
            hideProgressBar()
            _binding?.swipeRefreshTopStory?.isRefreshing = false

            if (tpStryList.id == ResponseStatus.SUCCESS) {
                if ((tpStryList?.topStoryId != null) || (!topStoriesList?.isEmpty()!!)) {
                    visibleTopStoryList(true)
                    topStoriesList = tpStryList?.topStoryId
                    updateAdapter(topStoriesList!!)
                } else {
                    visibleTopStoryList(false)
                    _binding?.idMessage?.text = getString(R.string.txt_no_top_stories_found)
                }

            } else {
                visibleTopStoryList(false)
                _binding?.idMessage?.text =
                    tpStryList?.topStoryId?.get(0) ?: getString(R.string.txt_something_went_wrong)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //saving  the top story list while orientation changes
        outState.putStringArrayList("TopStoryList",topStoriesList as ArrayList<String>)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        //restoring  the top story list while orientation changes
        _binding?.edtxTopStoryId?.setText("")
        topStoriesList =  savedInstanceState?.getStringArrayList("TopStoryList")
    }




    private fun visibleTopStoryList(bool:Boolean){
        if(bool){
            _binding?.idMessage?.visibility = View.GONE
            _binding?.topStoriesRecyclerView?.visibility = View.VISIBLE
        }else{
            _binding?.idMessage?.visibility = View.VISIBLE
            _binding?.topStoriesRecyclerView?.visibility = View.GONE
        }
    }

    private fun filterTextWatcher() {
        _binding?.edtxTopStoryId?.addTextChangedListener(object :TextWatcher{

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {

                val topStoryId = topStoriesList?.filter { it -> it.contains(p0.toString())}
                if(topStoriesList!=null){
                    if(!topStoryId?.isEmpty()!!){
                        visibleTopStoryList(true)
                        updateAdapter(topStoryId)
                    }else {
                        visibleTopStoryList(false)
                        _binding?.idMessage?.text = getString(R.string.txt_no_top_stories_found)
                    }
                }else {
                    visibleTopStoryList(false)
                    _binding?.idMessage?.text = getString(R.string.txt_no_top_stories_found)
                }
            }
        })
    }

    private fun updateAdapter(lst:List<String>){
        topStoriesAdapter = TopStoriesAdapter(requireContext(),lst!!,this)
        _binding?.topStoriesRecyclerView?.adapter = topStoriesAdapter
    }

    private fun getTopStories() {
        //if network is connected call remote API,store data in room db and retreive the data
        //if network is not connected, retreive data from room db
        if(isNetworkAvailable(requireContext())){
            showProgressBar()
            viewModel.getTopStoriesFromApi()
        }else{
            showProgressBar()
            viewModel.getTopStoriesFromRoomDb()
        }
    }

    fun showProgressBar(){
        _binding?.idProgressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar(){
        _binding?.idProgressBar?.visibility = View.GONE
    }

    private fun setLayoutManager() {
        if(requireActivity().resources.configuration.orientation == ORIENTATION_PORTRAIT){
            val layoutManager = GridLayoutManager(requireActivity(), 3)
            _binding?.topStoriesRecyclerView?.layoutManager = layoutManager
        }else if(requireActivity().resources.configuration.orientation == ORIENTATION_LANDSCAPE){
            val layoutManager = GridLayoutManager(requireActivity(), 5)
            _binding?.topStoriesRecyclerView?.layoutManager = layoutManager
        }
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

        showProgressBar()
        viewModel.getStoryFromApi(topStoryId).observe(requireActivity()) { it ->
            hideProgressBar()
            if(it.status == ResponseStatus.SUCCESS){
                if(it.data!=null){
                    val data = it.data as Story
                    showStoryDialog(data)
                }else{
                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_LONG).show()
                }
            }else if(it.status == ResponseStatus.FAIL){
                Toast.makeText(requireContext(),it.data.toString(),Toast.LENGTH_LONG).show()
            }else{
                val errorMessage = when (it.throwable) {
                    is IOException -> "No internet connection"
                    is HttpException -> "Something went wrong!"
                    else -> it.throwable?.localizedMessage
                }

                Toast.makeText(requireContext(),errorMessage?:"Something went wrong",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showStoryDialog(data: Story) {

        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog)

        val view = layoutInflater.inflate(R.layout.item_story,null)
        val  storyType = view.findViewById<TextView>(R.id.storyType)
        val storyId = view.findViewById<TextView>(R.id.storyId)
        val  storyTitle = view.findViewById<TextView>(R.id.storyTitle)
        val close = view.findViewById<ImageView>(R.id.icClose)

        //type first character as capital
        storyType.text = data.type?.replaceFirstChar { it.uppercaseChar() }?:""
        storyId.text = data.id?:""
        storyTitle.text = data.title?:""

        builder.setView(view)

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(true)
        alertDialog.show()
        close.setOnClickListener {
            alertDialog.dismiss()
        }
    }


}