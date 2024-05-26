package com.jans.tiles.app.activities

import android.R.attr
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.jans.tiles.app.databinding.DialogLayoutBinding
import com.jans.tiles.app.databinding.ItemR1Binding
import com.jans.tiles.app.databinding.ItemR2Binding
import com.jans.tiles.app.databinding.ItemR3Binding
import com.jans.tiles.app.databinding.ItemRtBinding
import com.jans.tiles.app.databinding.ItemRtfBinding
import com.jans.tiles.app.databinding.ItemRthBinding
import com.jans.tiles.app.databinding.ItemS1Binding
import com.jans.tiles.app.databinding.ItemS2Binding
import com.jans.tiles.app.enums.BlockDashboard
import com.jans.tiles.app.model.DashboardModel
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
import java.util.Timer
import java.util.TimerTask


class TilesScreen : AppCompatActivity() {

    private lateinit var b: ActivityTilesScreenBinding
    private lateinit var weatherService: WeatherService
    private lateinit var rvList: MutableList<DashboardModel.Dashboard>
    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTilesScreenBinding.inflate(layoutInflater)
        setContentView(b.root)

        // make image header
        b.imgHeader.setImageResource(R.drawable.img_header)
        // show weather details
        showWeatherDetails()
        // setup RV
        setUpDashboardRV()
        // setup buttons
        buttonsInit()
    }

    private fun addNewItemRV(adapter: DashboardAdapter) {
        val dialog = Dialog(this@TilesScreen)
        var blockSelected = ""

        findViewById<ImageView>(R.id.addItem).setOnClickListener {
            val bDialogAddItem = DialogLayoutBinding.inflate(layoutInflater)
            dialog.setContentView(bDialogAddItem.root)
            // -1 means match_parent , -2 means wrap_content
            dialog.window!!.setLayout(-1, -2)

            bDialogAddItem.closeDialog.setOnClickListener {
                dialog.dismiss()
            }

            // choose image click listener
            bDialogAddItem.btnChooseImage.setOnClickListener{
                val optionsMenu = arrayOf("Take Photo", "Choose from Gallery", "Exit")
                val builder = AlertDialog.Builder(this);
                builder.setItems(optionsMenu) { dialog, which ->
                    if (optionsMenu[which] == "Take Photo") {
                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(takePicture, 0)
                    } else if (optionsMenu[which] == "Choose from Gallery") {
                        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, 1)
                    } else if (optionsMenu[which] == "Exit") {
                        dialog.dismiss()
                    }
                }
                builder.show()
            }

            // set up spinner
            val spinner = bDialogAddItem.spinner
            val categories: List<String> = listOf(BlockDashboard.R1.toString(),
                BlockDashboard.R2.toString(), BlockDashboard.R3.toString(),
                BlockDashboard.RT.toString(), BlockDashboard.RTF.toString(),
                BlockDashboard.RTH.toString(), BlockDashboard.S1.toString(), BlockDashboard.S2.toString())
            val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = dataAdapter

            // set up spinner item listener
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val item = parent!!.getItemAtPosition(position).toString()
                    blockSelected = item
                        when (blockSelected) {
                            BlockDashboard.R1.toString() -> {
                                bDialogAddItem.PreviewLayout.removeAllViews()
                                val v1 = ItemR1Binding.inflate(layoutInflater)
                                bDialogAddItem.PreviewLayout.addView(v1.root)
                                v1.root.layoutParams.height = 870
                                v1.root.layoutParams.width = 500
                                // refresh the view
                                val timer = Timer()
                                timer.schedule(object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            val value = "${bDialogAddItem.edtItemName.text}\n" + blockSelected
                                            v1.text1.text = value
                                            if (bitmap != null) {
                                                v1.imgBackground.setImageBitmap(bitmap)
                                                v1.imgBackground.scaleType =
                                                    ImageView.ScaleType.FIT_XY
                                            }
                                        }
                                    }
                                }, 0, 100)

                            }

                            BlockDashboard.R2.toString() -> {
                                bDialogAddItem.PreviewLayout.removeAllViews()
                                val v1 = ItemR2Binding.inflate(layoutInflater)
                                bDialogAddItem.PreviewLayout.addView(v1.root)
                                v1.root.layoutParams.height = 500
                                // refresh the view
                                val timer = Timer()
                                timer.schedule(object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            val value = "${bDialogAddItem.edtItemName.text}\n" + blockSelected
                                            v1.text1.text = value
                                            if (bitmap != null) {
                                                v1.imgBackground.setImageBitmap(bitmap)
                                                v1.imgBackground.scaleType =
                                                    ImageView.ScaleType.FIT_XY
                                            }
                                        }
                                    }
                                }, 0, 100)
                            }

                            BlockDashboard.R3.toString() -> {
                                bDialogAddItem.PreviewLayout.removeAllViews()
                                val v1 = ItemR3Binding.inflate(layoutInflater)
                                bDialogAddItem.PreviewLayout.addView(v1.root)
                                v1.root.layoutParams.height = 500
                                // refresh the view
                                val timer = Timer()
                                timer.schedule(object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            val value = "${bDialogAddItem.edtItemName.text}\n" + blockSelected
                                            v1.text1.text = value
                                            if (bitmap != null) {
                                                v1.imgBackground.setImageBitmap(bitmap)
                                                v1.imgBackground.scaleType =
                                                    ImageView.ScaleType.FIT_XY
                                            }
                                        }
                                    }
                                }, 0, 100)

                            }

                            BlockDashboard.RT.toString() -> {
                                bDialogAddItem.PreviewLayout.removeAllViews()
                                val v1 = ItemRtBinding.inflate(layoutInflater)
                                bDialogAddItem.PreviewLayout.addView(v1.root)
                                v1.root.layoutParams.height = 500
                                // refresh the view
                                val timer = Timer()
                                timer.schedule(object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            val value = "${bDialogAddItem.edtItemName.text}\n" + blockSelected
                                            v1.text1.text = value
                                            if (bitmap != null) {
                                                v1.imgBackground.setImageBitmap(bitmap)
                                                v1.imgBackground.scaleType =
                                                    ImageView.ScaleType.FIT_XY
                                            }
                                        }
                                    }
                                }, 0, 100)

                            }

                            BlockDashboard.RTF.toString() -> {
                                bDialogAddItem.PreviewLayout.removeAllViews()
                                val v1 = ItemRtfBinding.inflate(layoutInflater)
                                bDialogAddItem.PreviewLayout.addView(v1.root)
                                v1.root.layoutParams.height = 500
                                // refresh the view
                                val timer = Timer()
                                timer.schedule(object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            val value = "${bDialogAddItem.edtItemName.text}\n" + blockSelected
                                            v1.text1.text = value
                                            if (bitmap != null) {
                                                v1.imgBackground.setImageBitmap(bitmap)
                                                v1.imgBackground.scaleType =
                                                    ImageView.ScaleType.FIT_XY
                                            }
                                        }
                                    }
                                }, 0, 100)
                            }

                            BlockDashboard.RTH.toString() -> {
                                bDialogAddItem.PreviewLayout.removeAllViews()
                                val v1 = ItemRthBinding.inflate(layoutInflater)
                                bDialogAddItem.PreviewLayout.addView(v1.root)
                                v1.root.layoutParams.height = 500
                                // refresh the view
                                val timer = Timer()
                                timer.schedule(object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            val value = "${bDialogAddItem.edtItemName.text}\n" + blockSelected
                                            v1.text1.text = value
                                            if (bitmap != null) {
                                                v1.imgBackground.setImageBitmap(bitmap)
                                                v1.imgBackground.scaleType =
                                                    ImageView.ScaleType.FIT_XY
                                            }
                                        }
                                    }
                                }, 0, 100)

                            }

                            BlockDashboard.S1.toString() -> {
                                bDialogAddItem.PreviewLayout.removeAllViews()
                                val v1 = ItemS1Binding.inflate(layoutInflater)
                                bDialogAddItem.PreviewLayout.addView(v1.root)
                                v1.root.layoutParams.height = 470
                                v1.root.layoutParams.width = 500
                                // refresh the view
                                val timer = Timer()
                                timer.schedule(object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            val value = "${bDialogAddItem.edtItemName.text}\n" + blockSelected
                                            v1.text1.text = value
                                            if (bitmap != null) {
                                                v1.imgBackground.setImageBitmap(bitmap)
                                                v1.imgBackground.scaleType =
                                                    ImageView.ScaleType.FIT_XY
                                            }
                                        }
                                    }
                                }, 0, 100)

                            }

                            BlockDashboard.S2.toString() -> {
                                bDialogAddItem.PreviewLayout.removeAllViews()
                                val v1 = ItemS2Binding.inflate(layoutInflater)
                                bDialogAddItem.PreviewLayout.addView(v1.root)
                                v1.root.layoutParams.height = 470
                                v1.root.layoutParams.width = 500
                                // refresh the view
                                val timer = Timer()
                                timer.schedule(object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            val value = "${bDialogAddItem.edtItemName.text}\n" + blockSelected
                                            v1.text1.text = value
                                            if (bitmap != null) {
                                                v1.imgBackground.setImageBitmap(bitmap)
                                                v1.imgBackground.scaleType =
                                                    ImageView.ScaleType.FIT_XY
                                            }
                                        }
                                    }
                                }, 0, 100)
                            }
                        }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            // Adding item
            bDialogAddItem.btnAddItem.setOnClickListener {
                val list: List<DashboardModel.Dashboard.Submodule> = ArrayList()
                // add title, image as bitmap and block selected
                rvList.add(DashboardModel.Dashboard(
                        blockSelected,// block
                    "", true, 0,
                    bitmap!!, // image as bitmap
                    true, "", "", list, "",
                    bDialogAddItem.edtItemName.text.toString(), "",) // title
                )

                val position = adapter.itemCount - 1
                adapter.notifyItemInserted(position)
                Toast.makeText(this, "Item added Successfully", Toast.LENGTH_SHORT).show()

                // Get the total height of the content in the NestedScrollView
                val lastChild: View = b.nestedScrollView.getChildAt(b.nestedScrollView.childCount - 1)
                val bottom: Int = lastChild.bottom + b.nestedScrollView.paddingBottom

                val animator = ObjectAnimator.ofInt(b.nestedScrollView, "scrollY", bottom)
                animator.setDuration(2000)
                animator.start()
                dialog.dismiss()
                bitmap = null
            }
            dialog.show()
        }
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
            val weatherData = weatherService.getWeather(
                "Peshawar",
                getString(com.jans.tiles.app.R.string.weather_api_key)
            )
            withContext(Dispatchers.Main) {
                // getting weather data
                val cityNameStr = "${weatherData.name}, ${formatTimestamp(weatherData.dt)}"
                val tempCelsiusStr = "${(weatherData.main.temp - 273.15).toInt()}°C"
                val iconUrl = "https://openweathermap.org/img/w/${weatherData.weather[0].icon}.png"

                b.tvCityName.visibility = VISIBLE
                b.ivWeatherIcon.visibility = VISIBLE
                b.tvTemperature.visibility = VISIBLE
                b.weatherLoader.visibility = GONE


                b.tvCityName.typeface =
                    ResourcesCompat.getFont(this@TilesScreen, com.jans.tiles.app.R.font.barlow)
                b.tvTemperature.typeface =
                    ResourcesCompat.getFont(
                        this@TilesScreen,
                        com.jans.tiles.app.R.font.barlow_extrabold
                    )

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
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }
        // setup Nested Scroll View
        b.rvProgressBar.visibility = VISIBLE
        b.rvBox.visibility = INVISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            b.rvBox.visibility = VISIBLE
            b.rvProgressBar.visibility = GONE
        }, 1000) // Delay in milliseconds

    }

    private fun setUpDashboardRV() {
        // populate the List
        val jsonString = readJsonFile(R.raw.dashboard_json)
        val dashboardResponse = Gson().fromJson(jsonString, DashboardModel::class.java)
        rvList = dashboardResponse.dashboard.toMutableList()

        // setup RV
        val adapter = DashboardAdapter(rvList)
        val dashboardRV = b.verticalRecyclerView
        val verticalLayoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        dashboardRV.layoutManager = verticalLayoutManager
        dashboardRV.adapter = adapter

        // add new item to rv
        addNewItemRV(adapter)

        // make moving tile feature
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                adapter.swapItems(fromPosition, toPosition)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })

        itemTouchHelper.attachToRecyclerView(dashboardRV)

    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) { // image from gallery
            val selectedImage = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
        }
        else if (requestCode == 0 && resultCode == RESULT_OK){ // image from camera
            bitmap = data!!.extras!!.get("data") as Bitmap
        }

    }

}


