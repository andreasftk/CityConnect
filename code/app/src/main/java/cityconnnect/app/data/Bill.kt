package cityconnnect.app.data
data class Bill(
    val title: String,
    val amount: Double,
    val date: String,
    val billId: Int,
    val citizenId: Int
)