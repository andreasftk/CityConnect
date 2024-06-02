<?php
header('Content-Type: application/json');

include_once('config.php');

// Array to store ticket data
$parking_tickets = array();

// Array of table names
$tables = array(
    'daily_parking_ticket',
    'five_hours_parking_ticket',
    'one_hour_parking_ticket',
    'three_hours_parking_ticket',
    'weekly_parking_ticket'
);

// Loop through each table and retrieve records
foreach ($tables as $table) {
    // SQL query to select all records from the current table and inner join with all_parking_tickets table
    $sql = "SELECT t.*, a.region
            FROM $table t
            INNER JOIN all_parking_tickets a ON t.id = a.id";
    $result = $con->query($sql);

    if ($result->num_rows > 0) {
        // Fetch all results into an associative array
        while($row = $result->fetch_assoc()) {
            // Append the table name to the row data
            $row['ticket_type'] = $table;
            // Add the row to the parking_tickets array
            $parking_tickets[] = $row;
        }
    } else {
        // If no results found for the table
        echo "0 results for $table";
    }
}

$con->close();

// JSON encode the array of results
echo json_encode($parking_tickets, JSON_PRETTY_PRINT);
?>
