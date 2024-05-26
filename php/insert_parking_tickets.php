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


/*
// Decode the incoming JSON data
$data = file_get_contents("php://input");
$json = json_decode($data, true);

// Validate and process the input data
if (isset($json['qrData']) && isset($json['userId']) && isset($json['ticketDuration'])) {
    $parking_id = $json['qrData'];
    $user_id = $json['userId'];
    $ticket_duration = $json['ticketDuration'];

    // Check the ticket validity
    $result = check_ticket_validity($con, '1','21', 'user_one_hour_parking_tickets','amea');

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
*/


 echo buy_parking($con, '1','21','amea','one hour',20);
?>