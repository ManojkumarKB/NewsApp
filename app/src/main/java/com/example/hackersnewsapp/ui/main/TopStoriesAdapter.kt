package com.example.hackersnewsapp.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hackersnewsapp.databinding.ItemStoryBinding
import com.example.hackersnewsapp.databinding.ItemTopStoryBinding

class TopStoriesAdapter ( var ctx: Context,
    var topStoryList: List<String>,
    var actionListener: ActionListener
) : RecyclerView.Adapter<TopStoriesAdapter.ViewHolder>() {

    // create an inner class with name ViewHolder
    // It takes a view argument, in which pass the generated class of single_item.xml
    // ie ItemCourseBinding and in the RecyclerView.ViewHolder(binding.root) pass it like this
    inner class ViewHolder(val binding: ItemTopStoryBinding ) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of CourseItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    // bind the items with each item
    // of the list languageList
    // which than will be
    // shown in recycler view
    // to keep it simple we are
    // not setting any image data to view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            topStoryList.apply {
                binding.txtTopStoryId.text = this[position]

                binding.topStoryLayout.setOnClickListener {
                    actionListener.onClickTopStory(this[position],position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return topStoryList.size
    }

    interface ActionListener{
        fun onClickTopStory(topStoryId:String, position: Int)
    }
}
