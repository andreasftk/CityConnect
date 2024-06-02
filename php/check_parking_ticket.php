<?php
include_once('config.php');

// Function to check ticket validity
function check_ticket_validity($con, $parking_id, $user_id, $ticket_duration) {
    $current_time = date('Y-m-d H:i:s');
    
    
    
  
    $stm1 = $con->prepare("SELECT category FROM `User` WHERE id = ?");
    $stm1->bind_param("i", $user_id);
    $stm1->execute();
    $stm1->bind_result($user_cat);
    $stm1->fetch();
    $stm1->close();
    
    
$num = 0; // Initialize $num

 $stmt = $con->prepare("SELECT user_id,out_time FROM `parking_live` WHERE user_id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $stmt->bind_result($temp,$out_time);
    $stmt->fetch();
    $stmt->close();

if($temp==$user_id){




   
    if($out_time >  $current_time){
    
    
    
    $delete_stmt = $con->prepare("DELETE FROM `parking_live` WHERE user_id = ?");
    $delete_stmt->bind_param("i", $user_id);
    $delete_stmt->execute();
    $delete_stmt->close();
    
       
    
      if($user_cat=='amea'){
                         
                    $stmt5 = $con->prepare( "UPDATE parkings SET available_amea_spaces=available_amea_spaces +1 WHERE id=?");
                      $stmt5->bind_param("i", $parking_id);
                $stmt5->execute();
        
                $stmt5->close();
                  }
                else{
                    
                    $stmt5 = $con->prepare( "UPDATE parkings SET available_spaces=available_spaces +1 WHERE id=?");
                      $stmt5->bind_param("i", $parking_id);
                $stmt5->execute();
        
                $stmt5->close();
                }
    
    return 4;
    }

else if($out_time <= $current_time){
    
  $current_time1 = DateTime::createFromFormat('Y-m-d H:i:s', $current_time);
    $out_time1 = DateTime::createFromFormat('Y-m-d H:i:s', $out_time);
    $extra_time = $out_time1->diff($current_time1);

    // Convert DateInterval to a format suitable for storage in the database
    $extra_time_string = $extra_time->format('%R%a days %h hours %i minutes %s seconds');

    

    // Prepare the INSERT statement
    $stmt = $con->prepare("INSERT INTO `parking_fines`(`parking_id`, `user_id`, `extra_time`, `duration_bought`) VALUES (?, ?, ?, ?)");

    // Bind parameters
    // Assuming `extra_time` column in the database is VARCHAR, adjust the data type accordingly
    $stmt->bind_param("iiss", $parking_id, $user_id, $extra_time_string, $ticket_duration);

    // Execute the statement
    $stmt->execute();

    // Close the statement
    $stmt->close();

  $delete_stmt = $con->prepare("DELETE FROM `parking_live` WHERE user_id = ?");
    $delete_stmt->bind_param("i", $user_id);
    $delete_stmt->execute();
    $delete_stmt->close();
    
    
    
      if($user_cat=='amea'){
                         
                    $stmt5 = $con->prepare( "UPDATE parkings SET available_amea_spaces=available_amea_spaces +1 WHERE id=?");
                      $stmt5->bind_param("i", $parking_id);
                $stmt5->execute();
        
                $stmt5->close();
                  }
                else{
                    
                    $stmt5 = $con->prepare( "UPDATE parkings SET available_spaces=available_spaces +1 WHERE id=?");
                      $stmt5->bind_param("i", $parking_id);
                $stmt5->execute();
        
                $stmt5->close();
                }
    
    return 5;

}
    
    
    
}
else{



    if($user_cat=='amea'){
        
    $stmt = $con->prepare("SELECT available_amea_spaces FROM `parkings` WHERE id = ?");
    $stmt->bind_param("i", $parking_id);
    $stmt->execute();
    $stmt->bind_result($num);
    $stmt->fetch();
    $stmt->close();
        
    }
    else{
        
    $stmt = $con->prepare("SELECT available_spaces FROM `parkings` WHERE id = ?");
    $stmt->bind_param("i", $parking_id);
    $stmt->execute();
    $stmt->bind_result($num);
    $stmt->fetch();
    $stmt->close();
    }

    
    if(intval($num)>0){
     $area = null; // Initialize $area

    // Fetch category_id from the parkings table
    $stmt = $con->prepare("SELECT category_id FROM `parkings` WHERE id = ?");
    $stmt->bind_param("i", $parking_id);
    $stmt->execute();
    $stmt->bind_result($area);
    $stmt->fetch();
    $stmt->close();

 $apt_id = null;
    $number = 0;
    $out_time = null;
    

  
  
  
  
  
  
  
  
  
  // Prepare the SQL query to get both apt.id and number based on the ticket duratio
if ($ticket_duration == 'Daily') {
    $sql = "
        SELECT apt.id, uohpt.number
        FROM user_daily_parking_tickets uohpt
        JOIN daily_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
        JOIN all_parking_tickets apt ON oht.id = apt.id
        WHERE apt.region = ?
        AND oht.user_category = ?
        AND uohpt.user_id = ?
    ";
} elseif ($ticket_duration == '5 hours') {
    $sql = "
        SELECT apt.id, uohpt.number
        FROM user_five_hours_parking_tickets uohpt
        JOIN five_hours_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
        JOIN all_parking_tickets apt ON oht.id = apt.id
        WHERE apt.region = ?
        AND oht.user_category = ?
        AND uohpt.user_id = ?
    ";
} elseif ($ticket_duration == '1 hour') {
    $sql = "
        SELECT apt.id, uohpt.number
        FROM user_one_hour_parking_tickets uohpt
        JOIN one_hour_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
        JOIN all_parking_tickets apt ON oht.id = apt.id
        WHERE apt.region = ?
        AND oht.user_category = ?
        AND uohpt.user_id = ?
    ";
} elseif ($ticket_duration == '3 hours') {
    $sql = "
        SELECT apt.id, uohpt.number
        FROM user_three_hours_parking_tickets uohpt
        JOIN three_hours_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
        JOIN all_parking_tickets apt ON oht.id = apt.id
        WHERE apt.region = ?
        AND oht.user_category = ?
        AND uohpt.user_id = ?
    ";
} elseif ($ticket_duration == 'Weekly') {
    $sql = "
        SELECT apt.id, uohpt.number
        FROM user_weekly_parking_tickets uohpt
        JOIN weekly_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
        JOIN all_parking_tickets apt ON oht.id = apt.id
        WHERE apt.region = ?
        AND oht.user_category = ?
        AND uohpt.user_id = ?
    ";
}

// Prepare and execute the statement

    $stmt = $con->prepare($sql);
    if ($stmt) {
        $stmt->bind_param("ssi", $area, $user_cat, $user_id);
        $stmt->execute();
        $stmt->bind_result($apt_id, $number);
        $stmt->fetch();
        
    }
if(intval($number)>0){
    
$stmt = null;

if ($ticket_duration == 'Daily') {
    // Calculate out_time for daily parking ticket
    $out_time = date('Y-m-d H:i:s', strtotime('+1 day', strtotime($current_time)));
} elseif ($ticket_duration == '5 hours') {
    // Calculate out_time for five hours parking ticket
    $out_time = date('Y-m-d H:i:s', strtotime('+5 hours', strtotime($current_time)));
    } elseif ($ticket_duration == '1 hour') {
    // Calculate out_time for one hour parking ticket
    $out_time = date('Y-m-d H:i:s', strtotime('+1 hour', strtotime($current_time)));
} elseif ($ticket_duration == '3 hours') {
    // Calculate out_time for three hours parking ticket
    $out_time = date('Y-m-d H:i:s', strtotime('+3 hours', strtotime($current_time)));
} elseif ($ticket_duration == 'Weekly') {
    // Calculate out_time for weekly parking ticket
    $out_time = date('Y-m-d H:i:s', strtotime('+1 week', strtotime($current_time)));
}

// Close any previously opened statement
if ($stmt != null) {
    $stmt->close();
}

// Check if $out_time is not null
if ($out_time != null) {
    // Query to insert into parking_live table
    $stmt = $con->prepare("INSERT INTO `parking_live`(`user_id`, `parking_id`, `in_time`, `out_time`) VALUES (?, ?, ?,?)");

    // Check if the statement was prepared successfully
    if ($stmt) {
        // Bind parameters
        $stmt->bind_param("iiss", $user_id, $parking_id, $current_time, $out_time);
        
        // Execute the query
        $stmt->execute();
        
        // Close the statement
        $stmt->close();
        
        // Ticket is valid
        $valid = true;
    }
}

        
        
         // Now, decrement the user's ticket count
            if ($ticket_duration == 'Daily') {
                $stmt = $con->prepare("UPDATE user_daily_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            } elseif ($ticket_duration == '5 hours') {
                $stmt = $con->prepare("UPDATE user_five_hours_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            } elseif ($ticket_duration == '1 hour') {
                $stmt = $con->prepare("UPDATE user_one_hour_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            } elseif ($ticket_duration == '3 hours') {
                $stmt = $con->prepare("UPDATE user_three_hours_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            } elseif ($ticket_duration == 'Weekly') {
                $stmt = $con->prepare("UPDATE user_weekly_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            }
            
            if ($stmt) {
                $stmt->bind_param("ii", $user_id, $apt_id);
                $stmt->execute();
                $stmt->close();
                
                
                
                  if($user_cat=='amea'){
                         
                    $stmt5 = $con->prepare( "UPDATE parkings SET available_amea_spaces=available_amea_spaces -1 WHERE id=?");
                      $stmt5->bind_param("i", $parking_id);
                $stmt5->execute();
        
                $stmt5->close();
                  }
                else{
                    
                    $stmt5 = $con->prepare( "UPDATE parkings SET available_spaces=available_spaces -1 WHERE id=?");
                      $stmt5->bind_param("i", $parking_id);
                $stmt5->execute();
        
                $stmt5->close();
                }
                
            }
            
    
    
}

else{return 2;
    
    
    
}
    return 3;
    }
    else{
        return 1;
    }
}
    
}



$data = file_get_contents("php://input");
$json = json_decode($data, true);

// Validate and process the input data
if (isset($json['qrData']) && isset($json['userId']) && isset($json['duration'])) {
    $parking_id = $json['qrData'];
    $user_id = $json['userId'];
    $ticket_duration = $json['duration'];

    $result = check_ticket_validity($con, $parking_id, $user_id, $ticket_duration);

    echo json_encode([
        'status' => 'success',
        'result' => $result
    ]);
} else {
    echo json_encode([
        'status' => 'error',
        'message' => 'Invalid input'
    ]);
}

 
?>