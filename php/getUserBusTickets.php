<?php
include_once('config.php');


// Decode the incoming JSON data
$data = file_get_contents("php://input");
$json = json_decode($data, true);

if ($_SERVER['REQUEST_METHOD'] === 'GET' && isset($_GET['route']) && isset($_GET['user_id']) && isset($_GET['user_cat'])) {
    $route = $_GET['route'];
    $user_id = $_GET['user_id'];
    $user_cat = $_GET['user_cat'];

    // Get bus numbers
    $result = get_bus_numbers($con, $route, $user_id, $user_cat);

    
} else {
    // Return an error response if the input is invalid
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input'
    ]);
}



// Function to check ticket validity
function get_bus_numbers($con, $route, $user_id ,$user_cat) {


$query = "SELECT number 
          FROM user_bus_tickets 
          INNER JOIN bus_tickets ON bus_tickets.id = user_bus_tickets.bus_ticket_id
          WHERE user_id = ? 
          AND bus_tickets.route = ? 
          AND bus_tickets.category = ?";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iss", $user_id, $route, $user_cat);

// Execute the statement
$stmt->execute();

// Bind result variables
$stmt->bind_result($number1);

// Fetch the result
$stmt->fetch();

// Close the statement
$stmt->close();



$query = "SELECT expiration_time
          FROM user_weekly_bus_tickets 
          INNER JOIN weekly_bus_tickets ON weekly_bus_tickets.id = user_weekly_bus_tickets.bus_ticket_id
          WHERE user_id = ? 
          AND weekly_bus_tickets.route = ? 
          AND weekly_bus_tickets.category = ?";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iss", $user_id, $route, $user_cat);

// Execute the statement
$stmt->execute();

// Bind result variables
$stmt->bind_result($number2);

// Fetch the result
$stmt->fetch();

// Close the statement
$stmt->close();




$query = "SELECT expiration_time
          FROM user_monthly_bus_tickets 
          INNER JOIN monthly_bus_tickets ON monthly_bus_tickets.id = user_monthly_bus_tickets.bus_ticket_id
          WHERE user_id = ? 
          AND monthly_bus_tickets.route = ? 
          AND monthly_bus_tickets.category = ?";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iss", $user_id, $route, $user_cat);

// Execute the statement
$stmt->execute();

// Bind result variables
$stmt->bind_result($number2);

// Fetch the result
$stmt->fetch();

// Close the statement
$stmt->close();



$numbers = [
    ['type' => 'Single', 'number' => $number1 ?? 0],
    ['type' => 'Weekly', 'number' => $number2 ?? 0],
    ['type' => 'Monthly', 'number' => $number3 ?? 0],
];

$jsonOutput = json_encode($numbers);

echo $jsonOutput;

}
?>