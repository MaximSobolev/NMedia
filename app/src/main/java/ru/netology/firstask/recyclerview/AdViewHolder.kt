package ru.netology.firstask.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.firstask.databinding.CardAdBinding
import ru.netology.firstask.dto.Ad

class AdViewHolder(
    private val binding : CardAdBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
    }

    fun bind(ad : Ad) {
       Glide.with(binding.adImage)
           .load("${BASE_URL}/media/${ad.image}")
           .timeout(10_000)
           .into(binding.adImage)
    }
}