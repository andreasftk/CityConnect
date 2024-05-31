<?php

include_once('config.php');

// SQL query to select all bus routes
$sql = "SELECT * FROM route";
$result = $con->query($sql);

$bus_stops = array();

if ($result->num_rows > 0) {
    // Output data of each row
    while($row = $result->fetch_assoc()) {
        $bus_stops[] = $row;
    }
    
} else {
    echo json_encode(array("message" => "No route found"));
    $con->close();
    exit();
}
$con->close();
echo json_encode($bus_stops);
?>
