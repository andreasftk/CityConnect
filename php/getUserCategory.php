<?php
include_once('config.php');

// Check if user_id is set
if (isset($_GET['user_id'])) {
    $user_id = intval($_GET['user_id']);

    // Prepare and bind
    $stmt = $con->prepare("SELECT category FROM users WHERE id = ?");
    $stmt->bind_param("i", $user_id);

    // Execute statement
    $stmt->execute();

    // Bind result variables
    $stmt->bind_result($category);

    // Fetch value
    if ($stmt->fetch()) {
        $response = array("user_id" => $user_id, "category" => $category);
    } else {
        $response = array("error" => "User not found");
    }

    // Close statement
    $stmt->close();
} else {
    $response = array("error" => "User ID not provided");
}

// Close connection
$con->close();

// Output JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
