package cityconnnect.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable
import java.util.Locale

class ComplainAdapter (
    private val complains: MutableList<Complain>
) : RecyclerView.Adapter<ComplainAdapter.ComplainViewHolder>(), Filterable{

    private var filteredList: MutableList<Complain> = complains.toMutableList()

    private var rateButtonClickListener: RateButtonClickListener? = null

    interface RateButtonClickListener {
        fun onRateButtonClick(complain: Complain)
    }
    fun setRateButtonClickListener(listener: RateButtonClickListener) {
        rateButtonClickListener = listener
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
        val currentComplain= complains[position]
        holder.itemView.apply {
            holder.bind(currentComplain)

        }

    }

    override fun getItemCount(): Int {
         return filteredList.size
    }
    inner class ComplainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvTotalRating: TextView = itemView.findViewById(R.id.tvTotalRating)
        private val buttonRate: ImageButton = itemView.findViewById(R.id.ivRate)


        fun bind(complain: Complain) {
            tvTitle.text = complain.title
            tvDescription.text = complain.description
            tvTotalRating.text = complain.totalRating.toString()
            buttonRate.setOnClickListener {
                rateButtonClickListener?.onRateButtonClick(complain)
            }
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

                        for (complain in complains) {
                            if (complain.title.lowercase(Locale.getDefault())
                                    .contains(filterPattern)
                            ) {
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
