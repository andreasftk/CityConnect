/*package cityconnnect.app.data
import java.util.*
open class PendingBills(
    var title: String,
    var amount: Double,
    var date: String,
    var billId: Int,
    var citizenId: Int
) {
    companion object {
        val pendingBillsList = mutableListOf<PendingBills>()

        fun addBill(bill: PendingBills) {
            pendingBillsList.add(bill)
        }

        fun removeBill(billId: Int) {
            val billToRemove = pendingBillsList.find { it.billId == billId }
            if (billToRemove != null) {
                pendingBillsList.remove(billToRemove)
                println("Bill removed successfully.")
            } else {
                println("Bill with ID $billId not found.")
            }
        }
    }
}

class PaidBills(
    title: String,
    amount: Double,
    date: String,
    billId: Int,
    citizenId: Int,
    var receipt: String
) : PendingBills(title, amount, date, billId, citizenId)*/

package cityconnnect.app.data
data class Bill(
    val title: String,
    val amount: Double,
    val date: String,
    val billId: Int,
    val citizenId: Int
)