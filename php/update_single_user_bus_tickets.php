<?php
include_once('config.php');

// Function to update ticket number and return the updated number
function update_number($con, $route, $user_id) {
    // Update user_bus_tickets
    $query = "
        UPDATE user_bus_tickets ubt
        INNER JOIN bus_tickets bt ON ubt.bus_ticket_id = bt.id
        SET ubt.number = ubt.number - 1
        WHERE bt.route = ?
        AND ubt.user_id = ?
        AND ubt.number > 0
    ";
    $stmt = $con->prepare($query);
    $stmt->bind_param("ii", $route, $user_id);
    $stmt->execute();

    // Get the updated number
    $updated_number = $con->affected_rows;

    // Fetch the new number of tickets
    $query = "
        SELECT ubt.number
        FROM user_bus_tickets ubt
        INNER JOIN bus_tickets bt ON ubt.bus_ticket_id = bt.id
        WHERE bt.route = ?
        AND ubt.user_id = ?
    ";
    $stmt = $con->prepare($query);
    $stmt->bind_param("ii", $route, $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        return $row['number'];
    } else {
        return 0; // Return 0 if no ticket found after update
    }
}

// Decode the incoming JSON data
$data = file_get_contents("php://input");
$json = json_decode($data, true);

// Validate and process the input data
if (isset($json['qrData']) && isset($json['userId'])) {
    $route = $json['qrData'];
    $user_id = $json['userId'];

    // Update the ticket number and get the updated number
    $updated_number = update_number($con, $route, $user_id);

    // Return the updated number as JSON
    echo json_encode([
        'status' => 'success',
        'result' => $updated_number
    ]);
} else {
    // Return an error response if the input is invalid
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input'
    ]);
}
?>
