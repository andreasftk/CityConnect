<?php
// Include database configuration
include_once('config.php');

// Get the raw JSON data from the request body
$jsonData = file_get_contents('php://input');

// Decode the JSON data into a PHP array
$requestData = json_decode($jsonData, true);

// Check if "selectedBillIds" key exists in the request data
if (!isset($requestData['selectedBillIds'])) {
    echo json_encode(['error' => 'No selectedBillIds provided']);
    exit;
}

// Retrieve selected bill IDs from the request
$selectedBillIds = $requestData['selectedBillIds'];

// Get current date in 'Y-M-D' format
$currentDate = date('Y-m-d');

// Iterate through selected bill IDs
foreach ($selectedBillIds as $billId) {
    // Select the pending bill with the given ID
    $sql = "SELECT * FROM pending_bills WHERE billId = $billId";
    $result = $con->query($sql);

    // Check if the pending bill exists
    if ($result->num_rows > 0) {
        // Fetch the pending bill data
        $bill = $result->fetch_assoc();

        // Extract bill data
        $title = $bill['title'];
        $category = $bill['category'];
        $amount = $bill['amount'];
        $date = $bill['date'];
        $citizenId = $bill['citizenId'];
        
        // Build receipt text including current date
        $receipt = "Paid: $currentDate";

        // Insert the pending bill into the paid_bills table
        $insertSql = "INSERT INTO paid_bills (title, category, amount, date, citizenId, receipt) VALUES ('$title', '$category', '$amount', '$date', '$citizenId', '$receipt')";
        $con->query($insertSql);

        // Remove the pending bill from the pending_bills table
        $deleteSql = "DELETE FROM pending_bills WHERE billId = $billId";
        $con->query($deleteSql);
    } else {
        // Handle case where selected bill ID does not exist
        echo json_encode(['error' => "Pending bill with ID $billId does not exist"]);
    }
}

// Close the database connection
$con->close();
?>

