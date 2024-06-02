package cityconnnect.app.data

data class PaidBill(
    val title: String,
    val category: String,
    val amount: Double,
    val date: String,
    val billId: Int,
    val citizenId: Int,
    val receipt: String
)