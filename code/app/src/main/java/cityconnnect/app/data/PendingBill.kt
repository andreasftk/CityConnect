package cityconnnect.app.data
data class PendingBill(
    val title: String,
    val category: String,
    val amount: Double,
    val date: String,
    val billId: Int,
    val citizenId: Int
)