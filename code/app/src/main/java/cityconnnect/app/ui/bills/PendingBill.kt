package cityconnnect.app.ui.bills
data class PendingBill(
    val title: String,
    val amount: Double,
    val date: String,
    val billId: Int,
    val citizenId: Int
)