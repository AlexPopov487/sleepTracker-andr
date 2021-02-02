package com.example.android.trackmysleepquality.sleeptracker

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter: ListAdapter<SleepNight, SleepNightAdapter.MyViewHolder>(SleepNightDiffCallback()) {

    // with ListAdapter, we do not need var data declaration as well as getItemCount()
//    var data = listOf<SleepNight>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    override fun getItemCount() = data.size
//
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }



    class MyViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // declare all the views that are included in the viewHolder
            private val sleepDurationTextView: TextView = itemView.findViewById(R.id.sleep_length_text)
            private val sleepQualityTextView: TextView = itemView.findViewById(R.id.quality_text)
            private val sleepQualityImageView: ImageView = itemView.findViewById<ImageView>(R.id.quality_image)

        fun bind(item: SleepNight) {
            //getting access to resources in order to invoke one of the Utils function
            val res = itemView.context.resources

            // set the text for the sleepDurationTextView using one of the utils formatting methods
            sleepDurationTextView.text = convertDurationToFormatted(item.startTimeMillis,
                    item.endTimeMillis, res)
            // set the formatted string to sleepQualityTextView
            sleepQualityTextView.text = convertNumericQualityToString(item.sleepQuality, res)
            //set the custom icon to the sleepQualityImageView
            sleepQualityImageView.setImageResource(when (item.sleepQuality) {
                0 -> R.drawable.ic_sleep_0
                1 -> R.drawable.ic_sleep_1
                2 -> R.drawable.ic_sleep_2
                3 -> R.drawable.ic_sleep_3
                4 -> R.drawable.ic_sleep_4
                5 -> R.drawable.ic_sleep_5
                else -> R.drawable.ic_sleep_active
            })
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
                return MyViewHolder(view)
            }
        }
        }

    // this class helps us find what items in the list were changed and update only them, instead
    // of updating the whole list
    class SleepNightDiffCallback: DiffUtil.ItemCallback<SleepNight>() {
        override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
            // check, whether items are the same by comparing their IDs
            return oldItem.nightId == newItem.nightId
        }

        override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
            // check, whether the contents of the item are the same by comparing the items
            return oldItem == newItem
        }


    }
}
