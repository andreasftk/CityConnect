<?php

include_once('config.php');

// SQL query to select data from the "Complain" table
$sql = "SELECT complainId, title, description, suggestions, photo, totalRating, date, location, userId, totalReviews FROM Complain ORDER BY date DESC";

// Execute the query
$result = $con->query($sql);

$complains= array();

if ($result->num_rows > 0) {
    // Output data of each row
    while($row = $result->fetch_assoc()) {
        $complains[] = $row;
    }
    
} else {
    echo json_encode(array("message" => "No complains found"));
    $con->close();
    exit();
}
$con->close();
echo json_encode($complains);
?>