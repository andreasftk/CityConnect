<?php
header('Content-Type: application/json');

include_once('config.php');


// SQL query to select all parkings
$sql = "SELECT * FROM parkings";
$result = $con->query($sql);

$parkings = array();

if ($result->num_rows > 0) {
    // Output data of each row
    while($row = $result->fetch_assoc()) {
        $parkings[] = $row;
    }
} else {
    echo json_encode(array("message" => "No parkings found"));
    $con->close();
    exit();
}

$con->close();

// Output the parkings data as JSON
echo json_encode($parkings);
?>