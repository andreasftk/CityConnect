<?php
header('Content-Type: application/json');
include_once('config.php');

// Define the query
$sql = "SELECT id, username, email, password, name FROM User";
$result = $con->query($sql);

// Check if there are results
if ($result->num_rows > 0) {
    $users = array();

    // Fetch the results into an array
    while ($row = $result->fetch_assoc()) {
        $users[] = array(
            'id' => (int)$row['id'],
            'username' => $row['username'],
            'email' => $row['email'],
            'password' => $row['password'],
            'name' => $row['name']
        );
    }

    // Encode array to JSON and output it
    echo json_encode($users);
} else {
    echo json_encode(array());
}

// Close the connection
$con->close();
?>
