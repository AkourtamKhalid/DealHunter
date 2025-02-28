package com.example.dealhunter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dealhunter.R
import com.example.dealhunter.model.Game
import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.DataSource
import android.util.Log
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

class GameAdapter(private var games: List<Game>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {
    private var originalGames: List<Game> = games.toList()

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameImage: ImageView = view.findViewById(R.id.gameImage)
        val gameTitle: TextView = view.findViewById(R.id.gameTitle)
        val gamePlatform: TextView = view.findViewById(R.id.gamePlatform)
        val gamePrice: TextView = view.findViewById(R.id.gamePrice)
        val favoriteIcon: TextView = view.findViewById(R.id.favoriteIcon)
        val originalPrice: TextView = view.findViewById(R.id.originalPrice)
        val savings: TextView = view.findViewById(R.id.savings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.game_card, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        
        holder.gameTitle.text = game.external
        holder.gamePlatform.visibility = View.GONE
        holder.originalPrice.visibility = View.GONE
        holder.gamePrice.text = "€${game.cheapest}"
        holder.savings.visibility = View.GONE

        // Create a GlideUrl with headers
        val glideUrl = GlideUrl(game.thumb, LazyHeaders.Builder()
            .addHeader("User-Agent", "Mozilla/5.0")
            .build())

        // Load image with modified Glide configuration
        Glide.with(holder.gameImage.context)
            .load(glideUrl)
            .placeholder(R.drawable.placeholder_game)
            .error(R.drawable.placeholder_game)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("GameAdapter", "Image load failed for ${game.external}: ${e?.rootCauses}", e)
                    e?.logRootCauses("GameAdapter")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("GameAdapter", "Image loaded successfully for ${game.external}")
                    return false
                }
            })
            .into(holder.gameImage)

        holder.favoriteIcon.setOnClickListener {
            // TODO: Implement favorite functionality
        }
    }

    override fun getItemCount() = games.size

    fun updateGames(newGames: List<Game>) {
        originalGames = newGames.toList()
        games = newGames
        notifyDataSetChanged()
    }

    fun filterDeals(filterOption: Int) {
        games = when (filterOption) {
            0 -> originalGames // No filter
            1 -> originalGames.sortedBy { it.cheapest.toDoubleOrNull() ?: 0.0 }
            2 -> originalGames.sortedByDescending { it.cheapest.toDoubleOrNull() ?: 0.0 }
            3 -> originalGames.sortedBy { it.cheapest.toDoubleOrNull() ?: 0.0 }
            4 -> originalGames.sortedByDescending { it.cheapest.toDoubleOrNull() ?: 0.0 }
            else -> originalGames
        }
        notifyDataSetChanged()
    }
}