<?php
include_once('config.php');

// Function to check ticket validity
function buy_parking($con, $parking_id, $user_id ,$user_cat,$duration,$number) {


$query="SELECT one_hour_parking_ticket.id
    FROM one_hour_parking_ticket
    JOIN all_parking_tickets ON one_hour_parking_ticket.id = all_parking_tickets.id
    JOIN parkings ON all_parking_tickets.region = parkings.category_id
    WHERE one_hour_parking_ticket.user_category = ? AND parkings.id = ?";


// Prepare the statement
  $stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("si", $user_cat, $parking_id);

// Execute the statement
$stmt->execute();

// Bind result variables
$stmt->bind_result($id1);

// Fetch the result
$stmt->fetch();

// Close the statement
$stmt->close();



if($duration == 'one hour'){
    
$query = "SELECT  `user_id` FROM `user_one_hour_parking_tickets` WHERE parking_ticket_id =? and user_id=? ;";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("ii", $id1,$user_id);

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
    $query = "UPDATE `user_one_hour_parking_tickets` SET `number` = `number` + ? WHERE `user_id` = ? AND `parking_ticket_id` = ?";
    
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

    $query = "INSERT INTO `user_one_hour_parking_tickets`(`user_id`, `parking_ticket_id`, `number`) VALUES (?, ?, ?)";

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



    
   
  
  
 else if($duration == 'three hours'){
      
      $id3=$id1;    
  $id3 .= '0'; 
    
$query = "SELECT  `user_id` FROM `user_three_hours_parking_tickets` WHERE parking_ticket_id =? and user_id=? ;";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("ii", $id3,$user_id);

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
    $query = "UPDATE `user_three_hours_parking_tickets` SET `number` = `number` + ? WHERE `user_id` = ? AND `parking_ticket_id` = ?";
    
    // Prepare the statement
    $stmt = $con->prepare($query);
    

    
    
    // Bind parameters
    $stmt->bind_param("iii", $number, $user_id, $id3);
    
    // Execute the statement
    $stmt->execute();
    
    // Close the statement
    $stmt->close();
}
else {

    $query = "INSERT INTO `user_three_hours_parking_tickets`(`user_id`, `parking_ticket_id`, `number`) VALUES (?, ?, ?)";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iii", $user_id, $id3, $number);

// Execute the statement
$stmt->execute();

// Close the statement
$stmt->close();
}
    
}

   
 else if($duration == 'five hours'){
      
      $id3=$id1;    
  $id3 .= '00'; 
    
$query = "SELECT  `user_id` FROM `user_five_hours_parking_tickets` WHERE parking_ticket_id =? and user_id=? ;";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("ii", $id3,$user_id);

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
    $query = "UPDATE `user_five_hours_parking_tickets` SET `number` = `number` + ? WHERE `user_id` = ? AND `parking_ticket_id` = ?";
    
    // Prepare the statement
    $stmt = $con->prepare($query);
    

    
    
    // Bind parameters
    $stmt->bind_param("iii", $number, $user_id, $id3);
    
    // Execute the statement
    $stmt->execute();
    
    // Close the statement
    $stmt->close();
}
else {

    $query = "INSERT INTO `user_five_hours_parking_tickets`(`user_id`, `parking_ticket_id`, `number`) VALUES (?, ?, ?)";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iii", $user_id, $id3, $number);

// Execute the statement
$stmt->execute();

// Close the statement
$stmt->close();
}
    
}


  
else  if($duration == 'daily'){
      
      $id3=$id1;    
  $id3 .= '000'; 
    
$query = "SELECT  `user_id` FROM `user_daily_parking_tickets` WHERE parking_ticket_id =? and user_id=? ;";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("ii", $id3,$user_id);

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
    $query = "UPDATE `user_daily_parking_tickets` SET `number` = `number` + ? WHERE `user_id` = ? AND `parking_ticket_id` = ?";
    
    // Prepare the statement
    $stmt = $con->prepare($query);
    

    
    
    // Bind parameters
    $stmt->bind_param("iii", $number, $user_id, $id3);
    
    // Execute the statement
    $stmt->execute();
    
    // Close the statement
    $stmt->close();
}
else {

    $query = "INSERT INTO `user_daily_parking_tickets`(`user_id`, `parking_ticket_id`, `number`) VALUES (?, ?, ?)";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iii", $user_id, $id3, $number);

// Execute the statement
$stmt->execute();

// Close the statement
$stmt->close();
}
    
}  
  
    
 else if($duration == 'weekly'){
      
      $id3=$id1;    
  $id3 .= '0000'; 
    
$query = "SELECT  `user_id` FROM `user_weekly_parking_tickets` WHERE parking_ticket_id =? and user_id=? ;";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("ii", $id3,$user_id);

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
    $query = "UPDATE `user_weekly_parking_tickets` SET `number` = `number` + ? WHERE `user_id` = ? AND `parking_ticket_id` = ?";
    
    // Prepare the statement
    $stmt = $con->prepare($query);
    

    
    
    // Bind parameters
    $stmt->bind_param("iii", $number, $user_id, $id3);
    
    // Execute the statement
    $stmt->execute();
    
    // Close the statement
    $stmt->close();
}
else {

    $query = "INSERT INTO `user_weekly_parking_tickets`(`user_id`, `parking_ticket_id`, `number`) VALUES (?, ?, ?)";

// Prepare the statement
$stmt = $con->prepare($query);

// Bind parameters
$stmt->bind_param("iii", $user_id, $id3, $number);

// Execute the statement
$stmt->execute();

// Close the statement
$stmt->close();
}
    
}


}


if (isset($_POST['parking_id']) && isset($_POST['user_id']) && isset($_POST['user_cat']) && isset($_POST['duration']) && isset($_POST['number'])) {
    $parking_id = $_POST['parking_id'];
    $user_id = $_POST['user_id'];
    $user_cat = $_POST['user_cat'];
    $duration = $_POST['duration'];
    $number = $_POST['number'];

    // Call the buy_parking function with provided parameters
    buy_parking($con, $parking_id, $user_id, $user_cat, $duration, $number);

    // Return a success message
    echo json_encode([
        'status' => 'success',
        'message' => 'Parking ticket purchased successfully'
    ]);
} else {
    // Return an error response if the required parameters are missing
    echo json_encode([
        'status' => 'error',
        'message' => 'Missing required parameters'
    ]);
}
?>