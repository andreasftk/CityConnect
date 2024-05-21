package cityconnnect.app

data class Complain (
    val title: String,
    val description :String,
    val photo: Int, // resource ID
    var totalRating : Float


)