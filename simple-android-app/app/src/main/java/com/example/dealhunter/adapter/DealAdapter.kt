package com.example.dealhunter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.dealhunter.R
import com.example.dealhunter.model.Deal
import kotlin.math.roundToInt
import android.graphics.Paint  // Add this import
import android.util.Log  // Add this import

class DealAdapter(private var deals: List<Deal>) : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {
    private var originalDeals: List<Deal> = deals.toList()

    class DealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameImage: ImageView = view.findViewById(R.id.dealImage)
        val gameTitle: TextView = view.findViewById(R.id.dealTitle)
        val storeInfo: TextView = view.findViewById(R.id.storeInfo)
        val salePrice: TextView = view.findViewById(R.id.salePrice)
        val normalPrice: TextView = view.findViewById(R.id.normalPrice)
        val savings: TextView = view.findViewById(R.id.savings)
        val dealRating: TextView = view.findViewById(R.id.dealRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.deal_item, parent, false)
        return DealViewHolder(view)
    }

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val deal = deals[position]
        Log.d("DealAdapter", "Binding deal at position $position: ${deal.title}")
        
        holder.gameTitle.text = deal.title
        holder.storeInfo.text = if (deal.storeName.isNotEmpty()) {
            "Store: ${deal.storeName}"
        } else {
            "Store ID: ${deal.storeID}"
        }
        holder.storeInfo.visibility = View.VISIBLE
        
        // Set prices with strike-through on normal price
        holder.salePrice.text = "€${deal.salePrice}"
        holder.normalPrice.apply {
            text = "€${deal.normalPrice}"
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        
        // Set savings
        val savingsValue = deal.savings.toDoubleOrNull()?.roundToInt() ?: 0
        holder.savings.text = "-$savingsValue%"
        
        // Set rating
        holder.dealRating.text = "Deal Rating: ${deal.dealRating}"

        // Load image with headers to prevent loading issues
        val glideUrl = GlideUrl(deal.thumb, LazyHeaders.Builder()
            .addHeader("User-Agent", "Mozilla/5.0")
            .build())

        Glide.with(holder.gameImage.context)
            .load(glideUrl)
            .placeholder(R.drawable.placeholder_game)
            .error(R.drawable.placeholder_game)
            .into(holder.gameImage)
    }

    override fun getItemCount() = deals.size

    fun updateDeals(newDeals: List<Deal>) {
        Log.d("DealAdapter", "Updating deals: received ${newDeals.size} deals")
        originalDeals = newDeals.toList()
        deals = newDeals
        notifyDataSetChanged()
    }

    fun filterDeals(filterOption: Int) {
        deals = when (filterOption) {
            0 -> originalDeals // No filter
            1 -> originalDeals.sortedBy { it.normalPrice.toDoubleOrNull() ?: 0.0 }
            2 -> originalDeals.sortedByDescending { it.normalPrice.toDoubleOrNull() ?: 0.0 }
            3 -> originalDeals.sortedBy { it.salePrice.toDoubleOrNull() ?: 0.0 }
            4 -> originalDeals.sortedByDescending { it.salePrice.toDoubleOrNull() ?: 0.0 }
            5 -> originalDeals.sortedByDescending { it.savings.toDoubleOrNull() ?: 0.0 }
            else -> originalDeals
        }
        notifyDataSetChanged()
    }

    fun getOriginalDeals(): List<Deal> = originalDeals.toList()

    fun sortBySalePrice(ascending: Boolean) {
        deals = if (ascending) {
            deals.sortedBy { it.salePrice.toDoubleOrNull() ?: Double.MAX_VALUE }
        } else {
            deals.sortedByDescending { it.salePrice.toDoubleOrNull() ?: 0.0 }
        }
        notifyDataSetChanged()
    }

    fun sortBySavings() {
        deals = deals.sortedByDescending { it.savings.toDoubleOrNull() ?: 0.0 }
        notifyDataSetChanged()
    }

    fun sortByDealRating() {
        deals = deals.sortedByDescending { it.dealRating.toDoubleOrNull() ?: 0.0 }
        notifyDataSetChanged()
    }
}
