<?php

include_once('config.php');

// SQL query to select data from the "Review" table
$sql = "SELECT complainId, userId, text FROM Report";

// Execute the query
$result = $con->query($sql);

$report= array();

if ($result->num_rows > 0) {
    // Output data of each row
    while($row = $result->fetch_assoc()) {
        $report[] = $row;
    }
    
} else {
    echo json_encode(array("message" => "No reports found"));
    $con->close();
    exit();
}
$con->close();
echo json_encode($report);
?>