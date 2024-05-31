<?php
include_once('config.php');

// Check if POST data exists
//if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Assuming 'qr_data' is sent from Android
   // $qr_data = $_POST['qr_data'];

    // Query to select the 'number' column based on the QR data
    // Modify this query according to your database schema
    $query = "SELECT * FROM `user_bus_tickets` WHERE bus_ticket_id = 104 AND user_id = 21 ";

    // Execute the query
    $result = mysqli_query($con, $query);

    if ($result) {
        // Check if any rows are returned
        if (mysqli_num_rows($result) > 0) {
            // Fetch the result as an associative array
            $row = mysqli_fetch_assoc($result);
            
            // Access the 'number' column value
            $number = $row['number'];
            
            // Return the 'number' value as response
            echo $number;
        } else {
            // No rows found for the specified QR data
            echo "No data found for the specified QR code.";
        }
    } else {
        // Query execution failed
        echo "Query execution failed.";
    }
//} 
//else {
    // No POST data received
    //echo "No data received.";
//}
?>
