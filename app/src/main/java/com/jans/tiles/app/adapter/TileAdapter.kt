package com.jans.tiles.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jans.tiles.app.R
import com.jans.tiles.app.adapter.TileAdapter.TileViewHolder
import java.util.Collections

class TileAdapter(context: Context?, private val tileList: List<String?>) :
    RecyclerView.Adapter<TileViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
        val itemView = inflater.inflate(R.layout.item_tile, parent, false)
        return TileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
        val tile = tileList[position]
        holder.tileText.text = tile
    }

    override fun getItemCount(): Int {
        return tileList.size
    }

    fun swapItems(fromPosition: Int, toPosition: Int) {
        Collections.swap(tileList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class TileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tileText: TextView = itemView.findViewById(R.id.tile_text)
    }
}
