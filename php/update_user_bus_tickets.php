<?php
header('Content-Type: application/json');
include_once('config.php');

// Check if the request method is POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Check if the 'user_id', 'bus_ticket_id', and 'number' parameters are set
    if (isset($_POST['user_id']) && isset($_POST['bus_ticket_id']) && isset($_POST['number'])) {
        // Get the parameters from the POST data
        $user_id = $_POST['user_id'];
        $bus_ticket_id = $_POST['bus_ticket_id'];
        

        // Construct the SQL query to decrement the 'number' column
        $query = "UPDATE user_bus_tickets SET number = number - 1 WHERE user_id = $user_id AND bus_ticket_id = $bus_ticket_id";

        // Execute the query
        $result = mysqli_query($con, $query);

        if ($result) {
            // Respond with a success message
            echo json_encode(array('message' => 'Number decremented successfully'));
        } else {
            // Respond with an error message if update fails
            echo json_encode(array('error' => 'Failed to decrement number'));
        }
    } else {
        // Respond with an error message if any required parameter is missing
        echo json_encode(array('error' => 'Required parameters are missing'));
    }
} else {
    // Respond with an error message if request method is not POST
    echo json_encode(array('error' => 'Invalid request method'));
}
?>
