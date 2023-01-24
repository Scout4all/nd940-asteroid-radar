package me.bigad.asteroidradar.ui.main


import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.bigad.asteroidradar.R

import me.bigad.asteroidradar.databinding.AsteroidListItemBinding

import me.bigad.asteroidradar.domain.Asteroid


class MainFragmentRecyclerAdapter(
    private val onClickListener: OnClickListener,
    private val isTablet: Boolean?
) : ListAdapter<Asteroid, MainFragmentRecyclerAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: AsteroidListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
            binding.executePendingBindings()
        }

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AsteroidListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    var selectedPosition = -1
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val asteroid = getItem(position)
        holder.bind(asteroid)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(asteroid)
            if (isTablet == true) {
                selectedPosition = position
                notifyDataSetChanged()
            }

        }


        if (isTablet == true) {
            if (selectedPosition == position) {
                holder.itemView.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.color_item_selected)
                );
            } else {
                holder.itemView.setBackgroundColor(
                    holder.itemView.getResources().getColor(R.color.colorPrimary)
                );
            }
        }

    }
}

class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}



