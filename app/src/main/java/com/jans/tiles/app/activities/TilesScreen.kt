package com.jans.tiles.app.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.jans.tiles.app.adapter.DashboardAdapter
import com.jans.tiles.app.R
import com.jans.tiles.app.databinding.ActivityTilesScreenBinding
import com.jans.tiles.app.model.DashboardModel
import com.jans.tiles.app.utils.RVUtils.Companion.readJsonFile


class TilesScreen : AppCompatActivity() {

    private lateinit var b: ActivityTilesScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTilesScreenBinding.inflate(layoutInflater)

        setContentView(b.root)






        setUpDashboardRV()
        buttonsInit()


    }

    private fun buttonsInit() {
        b.backBtn.setOnClickListener {
            finish()
        }

        // setup Nested Scroll View
        val relMain = b.relMain
        relMain.visibility = View.INVISIBLE

        // Setup Progress Bar
        val rvProgressBar = findViewById<LinearLayout>(R.id.rvProgressBar)
        Handler(Looper.getMainLooper()).postDelayed({
            relMain.visibility = View.VISIBLE
            rvProgressBar.visibility = View.GONE
        }, 50) // Delay in milliseconds

    }

    private fun setUpDashboardRV() {
        // populate the List
        val jsonString = readJsonFile(R.raw.dashboard_json)
        val dashboardResponse = Gson().fromJson(jsonString, DashboardModel::class.java)

        // setup RV
        val adapter = DashboardAdapter(dashboardResponse.dashboard)
        val dashboardRV = b.verticalRecyclerView
        val verticalLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        dashboardRV.layoutManager = verticalLayoutManager
        dashboardRV.adapter = adapter



        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                adapter.swapItems(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // No action needed on swipe
            }
        })

        itemTouchHelper.attachToRecyclerView(dashboardRV)






    }





}