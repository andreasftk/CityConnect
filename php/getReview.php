<?php

include_once('config.php');

// SQL query to select data from the "Review" table
$sql = "SELECT userId, star, complainId FROM Review";

// Execute the query
$result = $con->query($sql);

$review= array();

if ($result->num_rows > 0) {
    // Output data of each row
    while($row = $result->fetch_assoc()) {
        $review[] = $row;
    }
    
} else {
    echo json_encode(array("message" => "No reviews found"));
    $con->close();
    exit();
}
$con->close();
echo json_encode($review);
?>