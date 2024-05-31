<?php
include_once('config.php');

// Check if POST data exists
$current_time = date('Y-m-d H:i:s');

// Query to check if the ticket is expired or not
$query = "
    SELECT `expiration_time` 
    FROM `user_weekly_bus_tickets` 
    WHERE `bus_ticket_id` = 1040
    AND `user_id` = 21
";

// Execute the query
$result = mysqli_query($con, $query);

if ($result) {
    $row = mysqli_fetch_assoc($result);
    if ($row) {
        $expiration_time = $row['expiration_time'];
        if ($expiration_time > $current_time) {
            echo "Ticket is valid.";
        } else {
            // Ticket is expired, proceed to delete it
            $delete_query = "
                DELETE FROM `user_weekly_bus_tickets` 
                WHERE `bus_ticket_id` = 1040 
                AND `user_id` = 21
            ";
            $delete_result = mysqli_query($con, $delete_query);

            if ($delete_result) {
                echo "Ticket has expired and has been deleted.";
            } else {
                echo "Error deleting expired ticket: " . mysqli_error($con);
            }
        }
    } else {
        echo "Ticket not found.";
    }
} else {
    echo "Error checking ticket: " . mysqli_error($con);
}
?>
