package com.jans.tiles.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class RVUtils {
    companion object{

        //        values for all blocks
        const val ITEM_R1 = 1
        const val ITEM_R2 = 2
        const val ITEM_R3 =  3
        const val ITEM_S1 =  4
        const val ITEM_S2 =  5
        const val ITEM_RTF =  6
        const val ITEM_RTH =  7
        const val ITEM_RT =  8
        var tvWidth: Int = 0

        // extra methods for Recycler View Adapter
        fun getViewTypeLayout(resourceName: Int, parent: ViewGroup): View {
            return LayoutInflater.from(parent.context).inflate(resourceName, parent, false)
        }

        private fun getDrawableResourceId(context: Context, imageNameFromJson: String): Int {
            val packageName = context.packageName
            val res = context.resources.getIdentifier(imageNameFromJson, "drawable", packageName)

            return if (imageNameFromJson == "" || imageNameFromJson.isEmpty()) {
                context.resources.getIdentifier(
                    "ic_launcher_background",
                    "drawable",
                    packageName
                )
            } else if (res == 0) {
                context.resources.getIdentifier(
                    "ic_launcher_background",
                    "drawable",
                    packageName
                )
            } else {
                res
            }


        }

        fun bitmapDrawable1(context: Context, backgroundImage: Bitmap, maxHeight: Int): BitmapDrawable {
            return BitmapDrawable(
                context.resources,
                Bitmap.createScaledBitmap(backgroundImage, 400, maxHeight, true)
            )
        }

        fun setBGImage(imageName:Any, imgBG: ImageView, maxHeight: Int){
            val context = imgBG.context
            when(imageName){
                is String -> {
                    val backgroundImage: Bitmap = BitmapFactory.decodeResource(context.resources,
                        getDrawableResourceId(context, imageName)
                    )
                    val layerDrawable = LayerDrawable(arrayOf(bitmapDrawable1(context,backgroundImage, maxHeight)))
                    imgBG.background = (layerDrawable)
                }
                is Bitmap -> {
                    val layerDrawable = LayerDrawable(arrayOf(bitmapDrawable1(context,imageName, maxHeight)))
                    imgBG.background = (layerDrawable)
                }
            }


        }

        fun Context.readJsonFile(resourceId: Int): String {
            val inputStream: InputStream = resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            return buildString {
                try {
                    var line = reader.readLine()
                    while (line != null) {
                        append(line).append('\n')
                        line = reader.readLine()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        reader.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
            val output = Bitmap.createBitmap(
                bitmap.width, bitmap
                    .height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)

            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = pixels.toFloat()

            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
            canvas.drawBitmap(bitmap, rect, rect, paint)

            return output
        }

    }
}