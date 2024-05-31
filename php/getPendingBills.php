<?php
include_once('config.php');

// Get citizenId from the request
$citizenId = $_GET['citizenId'] ?? null;

// Check if citizenId is provided
if ($citizenId !== null) {
    $sql = "SELECT * FROM pending_bills WHERE citizenId = $citizenId";
    $result = $con->query($sql);
    
    $pending_bills = array();
    
    // Fetch result

    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $pending_bills[] = $row;
        }
    }
    
    else {
        echo json_encode(array("message" => "No pending bills found"));
        $con->close();
        exit();
    }

} else {
    echo "No citizenId provided.";
}

echo json_encode($pending_bills);
$con->close();
?>