<?php
header('Content-Type: application/json');

include_once('config.php');

// User ID parameter (replace 'your_user_id' with the actual user ID)
$user_id = $_GET['user_id'] ?? '';

// Array to store ticket data
$user_parking_tickets = array();

// Mapping of table names to ticket types
$table_to_type = array(
    'user_daily_parking_tickets' => 'day',
    'user_five_hours_parking_tickets' => '5hrs',
    'user_one_hour_parking_tickets' => '1hr',
    'user_three_hours_parking_tickets' => '3hrs',
    'user_weekly_parking_tickets' => 'weekly'
);

// Loop through each table and retrieve records
foreach ($table_to_type as $table => $type) {
    // SQL query to select records for the current table and user ID, inner join with all_parking_tickets table
    $sql = "SELECT u.*, a.region 
            FROM $table u 
            INNER JOIN all_parking_tickets a ON u.parking_ticket_id = a.id 
            WHERE u.user_id = '$user_id'";
    $result = $con->query($sql);

    if ($result->num_rows > 0) {
        // Fetch all results into an associative array
        while($row = $result->fetch_assoc()) {
            // Append the ticket type to the row data
            $row['ticket_type'] = $type;
            // Add the row to the user_parking_tickets array
            $user_parking_tickets[] = $row;
        }
    } else {
        // If no results found for the user and table, add a row with number set to 0
        $empty_row = array(
            'user_id' => $user_id,
            'parking_ticket_id' => '0',
            'number' => '0',
            'ticket_type' => $type,
            'region' => '' // Assuming region is a string
        );
        $user_parking_tickets[] = $empty_row;
    }
}

$con->close();

// JSON encode the array of results
echo json_encode($user_parking_tickets, JSON_PRETTY_PRINT);
?>
