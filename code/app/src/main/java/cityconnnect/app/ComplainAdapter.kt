package cityconnnect.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import cityconnnect.app.data.Complain
import java.util.Locale

class ComplainAdapter (
    private val complains: MutableList<Complain>
) : RecyclerView.Adapter<ComplainAdapter.ComplainViewHolder>(), Filterable{

    private var filteredList: MutableList<Complain> = complains.toMutableList()

    private var imageButtonClickListener: ImageButtonClickListener? = null

    enum class ButtonType {
        RATE,
        COMMENT
    }

    interface ImageButtonClickListener {
        fun onImageButtonClick(complain: Complain, buttonType: ButtonType)
    }
    fun setImageButtonClickListener(listener: ImageButtonClickListener) {
        imageButtonClickListener = listener
    }

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
        val currentComplain= filteredList[position]
        holder.itemView.apply {
            holder.bind(currentComplain)

        }

    }

    override fun getItemCount(): Int {
         return filteredList.size
    }
    inner class ComplainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvTotalRating: TextView = itemView.findViewById(R.id.tvTotalRating)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val ivComplain: ImageView = itemView.findViewById(R.id.ivComplain)
        private val ibRate: ImageButton = itemView.findViewById(R.id.ibRate)
        private val ibComment: ImageButton = itemView.findViewById(R.id.ibComment)
        private val tvTotalReview: TextView = itemView.findViewById(R.id.tvTotalReview)

        init {
            ibRate.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentComplain = filteredList[position]
                    imageButtonClickListener?.onImageButtonClick(currentComplain, ButtonType.RATE)
                }
            }
            ibComment.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentComplain = filteredList[position]
                    imageButtonClickListener?.onImageButtonClick(currentComplain, ButtonType.COMMENT)
                }
            }
        }

        fun bind(complain: Complain) {
            tvTitle.text = complain.title
            tvLocation.text = complain.location
            tvDescription.text = complain.description
            tvDate.text = complain.date.toString()
            tvTotalRating.text = complain.totalRating.toString()
            ivComplain.setImageResource(complain.photo)
            tvTotalReview.text = complain.totalReviews.toString()

        }
    }
        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val filteredResults = mutableListOf<Complain>()

                    if (constraint.isNullOrEmpty()) {
                        // No filter applied, return the original list
                        filteredResults.addAll(complains)
                    } else {
                        val filterPattern =
                            constraint.toString().lowercase(Locale.getDefault()).trim()

                        for (complain in filteredList) {
                            val title = complain.title.lowercase(Locale.getDefault())
                            val description = complain.description.lowercase(Locale.getDefault())

                            // Check if either the title or description contains the filter pattern
                            if (title.contains(filterPattern) || description.contains(filterPattern)) {
                                filteredResults.add(complain)
                            }
                        }
                    }

                    val filterResults = FilterResults()
                    filterResults.values = filteredResults
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    filteredList.clear()
                    filteredList.addAll(results?.values as MutableList<Complain>)
                    notifyDataSetChanged()
                }
            }
        }
    }
