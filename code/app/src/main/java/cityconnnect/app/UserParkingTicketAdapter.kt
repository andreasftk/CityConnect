package cityconnnect.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cityconnect.app.UserParkingTicket
import cityconnnect.app.R

class UserParkingTicketAdapter(private val userParkingTicketList: List<UserParkingTicket>) :
    RecyclerView.Adapter<UserParkingTicketAdapter.UserParkingTicketViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserParkingTicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_bus_ticket, parent, false)
        return UserParkingTicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserParkingTicketViewHolder, position: Int) {
        holder.bind(userParkingTicketList[position])
    }

    override fun getItemCount(): Int {
        return userParkingTicketList.size
    }

    class UserParkingTicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberTextView: TextView = itemView.findViewById(R.id.number_tv)
        val typeTextView: TextView = itemView.findViewById(R.id.type)

        fun bind(userParkingTicket: UserParkingTicket) {
            numberTextView.text = "${userParkingTicket.number}"
            typeTextView.text = "${userParkingTicket.ticket_type}"
        }
    }
}
