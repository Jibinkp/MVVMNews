package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.ui.MainActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var rvSavedNews: RecyclerView
    lateinit var paginationProgressBar: ProgressBar
    val TAG = "SavedNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        initView(view)
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(
                p0: RecyclerView.ViewHolder,
                p1: Int
            ) {
                val position = p0.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view,"Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })
    }

    private fun initView(view: View) {
        rvSavedNews = view.findViewById(R.id.rvSavedNews)
        // paginationProgressBar = view.findViewById(R.id.paginationProgressBar)
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}