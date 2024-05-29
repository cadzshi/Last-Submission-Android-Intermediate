package com.dicoding.midsubmissionintermediate.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.midsubmissionintermediate.R
import com.dicoding.midsubmissionintermediate.data.adapter.LoadingStateAdapter
import com.dicoding.midsubmissionintermediate.data.adapter.StoriesAdapter
import com.dicoding.midsubmissionintermediate.databinding.ActivityMainBinding
import com.dicoding.midsubmissionintermediate.helper.ViewModelFactory
import com.dicoding.midsubmissionintermediate.view.addstory.AddStoryActivity
import com.dicoding.midsubmissionintermediate.view.maps.MapsActivity
import com.dicoding.midsubmissionintermediate.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
            else{
                setStoriesData()
            }
        }
        viewModel.showLoading.observe(this){
            showLoading(it)
        }
        binding.fabAddStory.setOnClickListener{
            moveToAddStoryActivity()
        }
    }
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)
    }
    private fun setStoriesData() {
        val adapter = StoriesAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }
    private fun moveToAddStoryActivity(){
        startActivity(Intent(this, AddStoryActivity::class.java))
    }
    private fun moveToMapsActivity(){
        startActivity(Intent(this, MapsActivity::class.java))
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.logout))
                    setMessage(getString(R.string.logout_validation))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel.logout()
                    }
                    setNegativeButton(getString(R.string.no)){ _, _ ->
                        return@setNegativeButton
                    }
                    create()
                    show()
                }
                true
            }
            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.action_maps -> {
                moveToMapsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbMain.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}