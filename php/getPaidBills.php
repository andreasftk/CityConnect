<?php
include_once('config.php');

// Get citizenId from the request
$citizenId = $_GET['citizenId'] ?? null;

// Check if citizenId is provided
if ($citizenId !== null) {
    $sql = "SELECT * FROM paid_bills WHERE citizenId = $citizenId";
    $result = $con->query($sql);
    
    $paid_bills = array();
    
    // Fetch result

    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $paid_bills[] = $row;
        }
    }
    
    else {
        echo json_encode(array("message" => "No paid bills found"));
        $con->close();
        exit();
    }

} else {
    echo "No citizenId provided.";
}

echo json_encode($paid_bills);
$con->close();
?>