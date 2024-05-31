<?php
include_once('config.php');

// Function to check ticket validity
function check_ticket_validity($con, $route, $user_id) {
    $current_time = date('Y-m-d H:i:s');

    // Check in user_monthly_bus_tickets
    $query = "
        SELECT umt.expiration_time
        FROM user_monthly_bus_tickets umt
        INNER JOIN monthly_bus_tickets mbt ON umt.bus_ticket_id = mbt.id
        WHERE mbt.route = ?
        AND umt.user_id = ?
    ";
    $stmt = $con->prepare($query);
    $stmt->bind_param("ii", $route, $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        if ($row['expiration_time'] > $current_time) {
            return 1; // Monthly ticket is valid
        }
    }

    // Check in user_weekly_bus_tickets
    $query = "
        SELECT uwt.expiration_time
        FROM user_weekly_bus_tickets uwt
        INNER JOIN weekly_bus_tickets wbt ON uwt.bus_ticket_id = wbt.id
        WHERE wbt.route = ?
        AND uwt.user_id = ?
    ";
    $stmt = $con->prepare($query);
    $stmt->bind_param("ii", $route, $user_id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        if ($row['expiration_time'] > $current_time) {
            return 2; // Weekly ticket is valid
        }
    }

    // Check in user_bus_tickets
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
        if ($row['number'] > 0) {
            return 3; // Single ticket is valid
        }
    }

    return 0; // No valid ticket found
}

// Decode the incoming JSON data
$data = file_get_contents("php://input");
$json = json_decode($data, true);

// Validate and process the input data
if (isset($json['qrData']) && isset($json['userId'])) {
    $route = $json['qrData'];
    $user_id = $json['userId'];

    // Check the ticket validity
    $result = check_ticket_validity($con, $route, $user_id);

    // Return the result as JSON
    echo json_encode([
        'status' => 'success',
        'result' => $result
    ]);
} else {
    // Return an error response if the input is invalid
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input'
    ]);
}
?>
