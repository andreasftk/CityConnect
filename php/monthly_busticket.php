<?php
header('Content-Type: application/json');

include_once('config.php');


// SQL query to select all records from the bus_tickets table
$sql = "SELECT * FROM monthly_bus_tickets";
$result = $con->query($sql);

$bus_tickets = array();

if ($result->num_rows > 0) {
    // Fetch all results into an associative array
    while($row = $result->fetch_assoc()) {
        $bus_tickets[] = $row;
    }
} else {
    echo "0 results";
}
$con->close();

// JSON encode the array of results
echo json_encode($bus_tickets, JSON_PRETTY_PRINT);
?>