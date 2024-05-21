<?php
include_once('config.php');

// Check if POST data exists
//if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Assuming 'qr_data' is sent from Android
   // $qr_data = $_POST['qr_data'];

    // Query to select the 'number' column based on the QR data
    // Modify this query according to your database schema
$query = "UPDATE `user_bus_tickets` SET `number` = `number` - 1 WHERE `bus_ticket_id` = 104 AND `user_id` = 21";

    // Execute the query
    $result = mysqli_query($con, $query);


//else {
    // No POST data received
    //echo "No data received.";
//}
?>
