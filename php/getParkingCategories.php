<?php

include_once('config.php');

// SQL query to select all bus routes
$sql = "SELECT * FROM parking_categories";
$result = $con->query($sql);

 $cat = array();

if ($result->num_rows > 0) {
    // Output data of each row
    while($row = $result->fetch_assoc()) {
        $cat[] = $row;
    }
    
} else {
    echo json_encode(array("message" => "No route found"));
    $con->close();
    exit();
}
$con->close();
echo json_encode($cat);
?>
