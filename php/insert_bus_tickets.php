<?php
include_once('config.php');

// Function to check ticket validity
function buy_bus($con, $route, $user_id ,$user_cat,$duration,$number) {
$current_time = date('Y-m-d H:i:s');

$query = "SELECT id
          FROM bus_tickets 
          WHERE route = ? 
          AND category = ?";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("is", $route, $user_cat);

// Execute the statement
$stmt->execute();

// Bind result variables
$stmt->bind_result($id1);

// Fetch the result
$stmt->fetch();

// Close the statement
$stmt->close();



if($duration == 'single'){
    
$query = "SELECT user_id
          FROM user_bus_tickets 
          WHERE user_id = ? 
          AND bus_ticket_id = ?";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("ii", $user_id, $id1);

// Execute the statement
$stmt->execute();

// Bind result variables
$stmt->bind_result($temp);

// Fetch the result
$stmt->fetch();

// Close the statement
$stmt->close();

if($temp==$user_id){
    
     // Define the update query
    $query = "UPDATE `user_bus_tickets` SET `number` = `number` + ? WHERE `user_id` = ? AND `bus_ticket_id` = ?";
    
    // Prepare the statement
    $stmt = $con->prepare($query);
    
    // Increment value
    
    
    // Bind parameters
    $stmt->bind_param("iii", $number, $user_id, $id1);
    
    // Execute the statement
    $stmt->execute();
    
    // Close the statement
    $stmt->close();
}
else {

    $query = "INSERT INTO `user_bus_tickets`(`user_id`, `bus_ticket_id`, `number`) VALUES (?, ?, ?)";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iii", $user_id, $id1, $number);

// Execute the statement
$stmt->execute();

// Close the statement
$stmt->close();
}
    
}


else if($duration == 'weekly'){
    
  $id2=$id1;    
  $id2 .= '0';  
  
  
  
  
  $query = "SELECT user_id,expiration_time
          FROM user_weekly_bus_tickets 
          WHERE user_id = ? 
          AND bus_ticket_id = ?";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("is", $user_id, $id2);

// Execute the statement
$stmt->execute();

// Bind result variables
$stmt->bind_result($temp,$expiration_time);

// Fetch the result
$stmt->fetch();

// Close the statement
$stmt->close();

if($temp==$user_id){
  
  
    $new_expiration_time = date('Y-m-d H:i:s', strtotime($expiration_time . ' +1 week'));

    // Define the update query
    $query = "UPDATE `user_weekly_bus_tickets` SET `expiration_time` = ? WHERE `user_id` = ? AND `bus_ticket_id` = ?";

    // Prepare the statement
    $stmt = $con->prepare($query);

    // Bind parameters
    $stmt->bind_param("sii", $new_expiration_time, $user_id, $id2);

    // Execute the statement
    $stmt->execute();

    // Close the statement
    $stmt->close();
  
}

else
{
    // Calculate the expiration time (current time + 1 week)
$expiration_time = date('Y-m-d H:i:s', strtotime('+1 week'));

// Define the insert query
$query = "INSERT INTO `user_weekly_bus_tickets`(`user_id`, `bus_ticket_id`, `expiration_time`) VALUES (?, ?, ?)";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iis", $user_id, $id2, $expiration_time);

// Execute the statement
$stmt->execute();

// Close the statement
$stmt->close();

}
  
  
}
  else if($duration == 'monthly'){
    
  $id3=$id1;    
  $id3 .= '00';  
  
  echo $id3;
  
  
  $query = "SELECT user_id,expiration_time
          FROM user_monthly_bus_tickets 
          WHERE user_id = ? 
          AND bus_ticket_id = ?";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("is", $user_id, $id3);

// Execute the statement
$stmt->execute();

// Bind result variables
$stmt->bind_result($temp,$expiration_time);

// Fetch the result
$stmt->fetch();

// Close the statement
$stmt->close();

if($temp==$user_id){
  
  
  
  
    $new_expiration_time = date('Y-m-d H:i:s', strtotime($expiration_time . ' +1 month'));

    // Define the update query
    $query = "UPDATE `user_monthly_bus_tickets` SET `expiration_time` = ? WHERE `user_id` = ? AND `bus_ticket_id` = ?";

    // Prepare the statement
    $stmt = $con->prepare($query);

    // Bind parameters
    $stmt->bind_param("sii", $new_expiration_time, $user_id, $id3);

    // Execute the statement
    $stmt->execute();

    // Close the statement
    $stmt->close();
  
}

else
{
    // Calculate the expiration time (current time + 1 week)
$expiration_time = date('Y-m-d H:i:s', strtotime('+1 week'));

// Define the insert query
$query = "INSERT INTO `user_monthly_bus_tickets`(`user_id`, `bus_ticket_id`, `expiration_time`) VALUES (?, ?, ?)";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iis", $user_id, $id3, $expiration_time);

// Execute the statement
$stmt->execute();

// Close the statement
$stmt->close();

}
  
  
}
  
  
  
  
  
  
  
  
  



}

// Check if all required parameters are provided in the URL query string
if (isset($_POST['route']) && isset( $_POST['user_id']) && isset( $_POST['user_cat']) && isset($_POST['duration']) && isset( $_POST['number'])) {
    $route = $_POST['route'];
    $user_id = $_POST['user_id'];
    $user_cat = $_POST['user_cat'];
    $duration = $_POST['duration'];
    $number = $_POST['number'];

    // Call the buy_bus function with provided parameters
    buy_bus($con, $route, $user_id, $user_cat, $duration, $number);

    // Return a success message
    
} else {
    // Return an error response if the required parameters are missing
    echo json_encode([
        'status' => 'error',
        'message' => 'Missing required parameters'
    ]);
}
?>