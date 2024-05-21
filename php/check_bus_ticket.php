<?php
include_once('config.php');

function check_ticket_validity($con, $route, $user_id) {
    $current_time = date('Y-m-d H:i:s');

    // Check in user_monthly_bus_tickets
    $query = "
        SELECT umt.expiration_time
        FROM user_monthly_bus_tickets umt
        INNER JOIN monthly_bus_tickets mbt ON umt.bus_ticket_id = mbt.id
        WHERE mbt.route = $route
        AND umt.user_id = $user_id
    ";
    $result = mysqli_query($con, $query);

    if ($result) {
        $row = mysqli_fetch_assoc($result);
        if ($row) {
            $expiration_time = $row['expiration_time'];
            if ($expiration_time > $current_time) {
                return 1; // Monthly ticket is valid
            }
        }
    } else {
        echo "Error checking monthly tickets: " . mysqli_error($con);
    }

    // Check in user_weekly_bus_tickets
    $query = "
        SELECT uwt.expiration_time
        FROM user_weekly_bus_tickets uwt
        INNER JOIN weekly_bus_tickets wbt ON uwt.bus_ticket_id = wbt.id
        WHERE wbt.route = $route
        AND uwt.user_id = $user_id
    ";
    $result = mysqli_query($con, $query);

    if ($result) {
        $row = mysqli_fetch_assoc($result);
        if ($row) {
            $expiration_time = $row['expiration_time'];
            if ($expiration_time > $current_time) {
                return 2; // Weekly ticket is valid
            }
        }
    } else {
        echo "Error checking weekly tickets: " . mysqli_error($con);
    }

    // Check in user_bus_tickets
    $query = "
        SELECT ubt.number
        FROM user_bus_tickets ubt
        INNER JOIN bus_tickets bt ON ubt.bus_ticket_id = bt.id
        WHERE bt.route = $route
        AND ubt.user_id = $user_id
    ";
    $result = mysqli_query($con, $query);

    if ($result) {
        $row = mysqli_fetch_assoc($result);
        if ($row) {
            $num= $row['number'];
            if ($num > '0') {
                return 3; // Single ticket is valid
            }
        }
    } else {
        echo "Error checking single tickets: " . mysqli_error($con);
    }

    return 0; // No valid ticket found
}

// Example usage
$route = 1; // Replace with the actual route number
$user_id = 21; // Replace with the actual user ID

$result = check_ticket_validity($con, $route, $user_id);

echo $result;

?>
