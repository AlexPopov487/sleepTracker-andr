package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
import com.example.android.trackmysleepquality.convertDurationToFormatted
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight

class SleepNightAdapter: RecyclerView.Adapter<SleepNightAdapter.MyViewHolder>() {
    var data = listOf<SleepNight>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
        //getting access to resources in order to invoke one of the Utils function
        val res = holder.itemView.context.resources

        // set the text for the sleepDurationTextView using one of the utils formatting methods
        holder.sleepDurationTextView.text = convertDurationToFormatted(item.startTimeMillis,
                item.endTimeMillis, res)
        // set the formatted string to sleepQualityTextView
        holder.sleepQualityTextView.text = convertNumericQualityToString(item.sleepQuality, res)
        //set the custom icon to the sleepQualityImageView
        holder.sleepQualityImageView.setImageResource(when(item.sleepQuality){
            0 -> R.drawable.ic_sleep_0
            1 -> R.drawable.ic_sleep_1
            2 -> R.drawable.ic_sleep_2
            3 -> R.drawable.ic_sleep_3
            4 -> R.drawable.ic_sleep_4
            5 -> R.drawable.ic_sleep_5
            else -> R.drawable.ic_sleep_active
        })
    }

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // declare all the views that are included in the viewHolder
            val sleepDurationTextView: TextView = itemView.findViewById(R.id.sleep_length_text)
            val sleepQualityTextView: TextView = itemView.findViewById(R.id.quality_text)
            val sleepQualityImageView: ImageView = itemView.findViewById<ImageView>(R.id.quality_image)

        }

}
