package com.jans.tiles.app.activities

import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.jans.tiles.app.R
import com.jans.tiles.app.adapter.DashboardAdapter
import com.jans.tiles.app.databinding.ActivityTilesScreenBinding
import com.jans.tiles.app.model.DashboardModel
import com.jans.tiles.app.utils.RVUtils.Companion.getRoundedCornerBitmap
import com.jans.tiles.app.utils.RVUtils.Companion.readJsonFile
import com.jans.tiles.app.weather.WeatherService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TilesScreen : AppCompatActivity() {

    private lateinit var b: ActivityTilesScreenBinding
    private val apiKeyWeather = "f05195df0792144c11766dc8f5d0ad07"
    private lateinit var weatherService: WeatherService
    private lateinit var adapter: DashboardAdapter
    private lateinit var rvList: MutableList<DashboardModel.Dashboard>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTilesScreenBinding.inflate(layoutInflater)
        setContentView(b.root)

        // make corner radius to image header
        b.imgHeader.setImageBitmap(
            getRoundedCornerBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.img_header
                ), 100
            )
        )

        showWeatherDetails()

        // setup RV
        setUpDashboardRV()
//        // setup buttons
        buttonsInit()


    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun showWeatherDetails() {

        b.tvCityName.visibility = INVISIBLE
        b.ivWeatherIcon.visibility = INVISIBLE
        b.tvTemperature.visibility = INVISIBLE
        b.weatherLoader.visibility = VISIBLE

        weatherService = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val weatherData = weatherService.getWeather("Peshawar", apiKeyWeather)
            withContext(Dispatchers.Main) {
                // getting weather data
                val cityNameStr = "${weatherData.name}, ${formatTimestamp(weatherData.dt)}"
                val tempCelsiusStr = "${(weatherData.main.temp - 273.15).toInt()}°C"
                val iconUrl = "https://openweathermap.org/img/w/${weatherData.weather[0].icon}.png"

                b.tvCityName.visibility = VISIBLE
                b.ivWeatherIcon.visibility = VISIBLE
                b.tvTemperature.visibility = VISIBLE
                b.weatherLoader.visibility = GONE


                b.tvCityName.typeface = ResourcesCompat.getFont(this@TilesScreen, com.jans.tiles.app.R.font.barlow)
                b.tvTemperature.typeface =
                    ResourcesCompat.getFont(this@TilesScreen, com.jans.tiles.app.R.font.barlow_extrabold)

                // assigning weather data
                b.tvCityName.text = cityNameStr
                b.tvTemperature.text = tempCelsiusStr
                Glide.with(this@TilesScreen)
                    .load(iconUrl)
                    .into(b.ivWeatherIcon)
            }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000) // Convert from seconds to milliseconds
        val sdf = SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    private fun buttonsInit() {
        findViewById<ImageView>(com.jans.tiles.app.R.id.backBtn).setOnClickListener {
            finish()
        }
        // setup Nested Scroll View
        b.rvProgressBar.visibility = View.VISIBLE
        b.rvBox.visibility = View.INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            b.rvBox.visibility = View.VISIBLE
            b.rvProgressBar.visibility = View.GONE
        }, 1000) // Delay in milliseconds

    }

    private fun setUpDashboardRV() {
        // populate the List
        val jsonString = readJsonFile(com.jans.tiles.app.R.raw.dashboard_json)
        val dashboardResponse = Gson().fromJson(jsonString, DashboardModel::class.java)


        rvList = dashboardResponse.dashboard.toMutableList()

        // setup RV
        val adapter = DashboardAdapter(rvList)
        val dashboardRV = b.verticalRecyclerView
        val verticalLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        dashboardRV.layoutManager = verticalLayoutManager
        dashboardRV.adapter = adapter





        findViewById<ImageView>(com.jans.tiles.app.R.id.addItem).setOnClickListener {

            val list: List<DashboardModel.Dashboard.Submodule> = ArrayList()
            rvList.add(
                DashboardModel.Dashboard(
                    "R3", "",
                    true, 0, "dashboard_1", true,
                    "First", "", list, "", "First", "",
                )
            )

            val position = adapter.itemCount - 1
            adapter.notifyItemInserted(position)
            Toast.makeText(this, "Item added Successfully", Toast.LENGTH_SHORT).show()


                // Get the total height of the content in the NestedScrollView
                val lastChild: View =
                    b.nestedScrollView.getChildAt(b.nestedScrollView.getChildCount() - 1)
                val bottom: Int = lastChild.bottom + b.nestedScrollView.getPaddingBottom()


// Create an ObjectAnimator to smoothly scroll to the bottom
                val animator = ObjectAnimator.ofInt(b.nestedScrollView, "scrollY", bottom)
                animator.setDuration(1000) // Duration in milliseconds (1 second in this case)
                animator.start()





        }


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


