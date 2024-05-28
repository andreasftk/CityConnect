package cityconnnect.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cityconnect.app.UserBusTicket
import cityconnnect.app.R

class UserBusTicketAdapter(private val userBusTicketList: List<UserBusTicket>) :
    RecyclerView.Adapter<UserBusTicketAdapter.UserBusTicketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBusTicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_bus_ticket, parent, false)
        return UserBusTicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserBusTicketViewHolder, position: Int) {
        holder.bind(userBusTicketList[position])
    }

    override fun getItemCount(): Int {
        return userBusTicketList.size
    }

    class UserBusTicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberTextView: TextView = itemView.findViewById(R.id.number_tv)
        val typeTextView: TextView = itemView.findViewById(R.id.type)

        fun bind(userBusTicket: UserBusTicket) {
            numberTextView.text = "${userBusTicket.number}"
            typeTextView.text = "${userBusTicket.type}"
        }
    }
}
