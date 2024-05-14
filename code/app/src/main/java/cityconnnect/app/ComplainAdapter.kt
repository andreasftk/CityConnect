package cityconnnect.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComplainAdapter (
    private val complains: MutableList<Complain>
) : RecyclerView.Adapter<ComplainAdapter.ComplainViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplainViewHolder {
        return ComplainViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_complain,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ComplainViewHolder, position: Int) {
        val currentComplain= complains[position]
        holder.itemView.apply {
            holder.bind(currentComplain)

        }

    }

    override fun getItemCount(): Int {
        return complains.size
    }
    inner class ComplainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvTotalRating: TextView = itemView.findViewById(R.id.tvTotalRating)
        private val rbRate: RatingBar = itemView.findViewById(R.id.rbRate)

        fun bind(complain: Complain) {
            tvTitle.text = complain.title
            tvDescription.text = complain.description
            tvTotalRating.text = complain.totalRating.toString()
            rbRate.setOnRatingBarChangeListener { _, rating, _ ->
                // Update the complain's rating when the user interacts with the RatingBar

            }
        }
    }
}