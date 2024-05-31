<?php
// Assuming you have a database connection established
include_once('config.php');



    // Query to select the 'number' column based on the QR data
    // Modify this query according to your database schema
    $query = "SELECT * FROM `user_bus_tickets` 
          INNER JOIN all_bus_tickets AS all_tickets ON user_bus_tickets.bus_ticket_id = all_tickets.id 
          INNER JOIN bus_tickets AS m ON m.id = all_tickets.id 
          WHERE m.route = '1' AND user_id = 21";


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

?>
