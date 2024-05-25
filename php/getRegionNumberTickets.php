<?php
include_once('config.php');

// Function to check ticket validity
function check_ticket_validity($con, $parking_id, $user_id, $ticket_duration,$user_cat) {
    $current_time = date('Y-m-d H:i:s');
    
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
    
    return5;

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
    

  
  
  
  
  
  
  
  
  
  // Prepare the SQL query to get both apt.id and number based on the ticket duration
$stmt = null;
if ($ticket_duration == 'user_daily_parking_tickets') {
    $sql = "
        SELECT apt.id, uohpt.number
        FROM user_daily_parking_tickets uohpt
        JOIN daily_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
        JOIN all_parking_tickets apt ON oht.id = apt.id
        WHERE apt.region = ?
        AND oht.user_category = ?
        AND uohpt.user_id = ?
    ";
} elseif ($ticket_duration == 'user_five_hours_parking_tickets') {
    $sql = "
        SELECT apt.id, uohpt.number
        FROM user_five_hours_parking_tickets uohpt
        JOIN five_hours_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
        JOIN all_parking_tickets apt ON oht.id = apt.id
        WHERE apt.region = ?
        AND oht.user_category = ?
        AND uohpt.user_id = ?
    ";
} elseif ($ticket_duration == 'user_one_hour_parking_tickets') {
    $sql = "
        SELECT apt.id, uohpt.number
        FROM user_one_hour_parking_tickets uohpt
        JOIN one_hour_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
        JOIN all_parking_tickets apt ON oht.id = apt.id
        WHERE apt.region = ?
        AND oht.user_category = ?
        AND uohpt.user_id = ?
    ";
} elseif ($ticket_duration == 'user_three_hours_parking_tickets') {
    $sql = "
        SELECT apt.id, uohpt.number
        FROM user_three_hours_parking_tickets uohpt
        JOIN three_hours_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
        JOIN all_parking_tickets apt ON oht.id = apt.id
        WHERE apt.region = ?
        AND oht.user_category = ?
        AND uohpt.user_id = ?
    ";
} elseif ($ticket_duration == 'user_weekly_parking_tickets') {
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
if ($sql) {
    $stmt = $con->prepare($sql);
    if ($stmt) {
        $stmt->bind_param("sss", $area, $user_cat, $user_id);
        $stmt->execute();
        $stmt->bind_result($apt_id, $number);
        $stmt->fetch();
        $stmt->close();
    }
}
if(intval($number)>0){
    
        if ($ticket_duration == 'user_daily_parking_tickets') {
            // Calculate out_time for daily parking ticket
            $out_time = date('Y-m-d H:i:s', strtotime('+1 day', strtotime($current_time)));
            // Query to insert into parking_live table
            $stmt = $con->prepare("INSERT INTO `parking_live`(`user_id`, `parking_id`, `in_time`, `out_time`) VALUES (?, ?, ?, ?)");
        } elseif ($ticket_duration == 'user_five_hours_parking_tickets') {
            // Calculate out_time for five hours parking ticket
            $out_time = date('Y-m-d H:i:s', strtotime('+5 hours', strtotime($current_time)));
            // Query to insert into parking_live table
            $stmt = $con->prepare("INSERT INTO `parking_live`(`user_id`, `parking_id`, `in_time`, `out_time`) VALUES (?, ?, ?, ?)");
        } elseif ($ticket_duration == 'user_one_hour_parking_tickets') {
            // Calculate out_time for one hour parking ticket
            $out_time = date('Y-m-d H:i:s', strtotime('+1 hour', strtotime($current_time)));
            // Query to insert into parking_live table
            $stmt = $con->prepare("INSERT INTO `parking_live`(`user_id`, `parking_id`, `in_time`, `out_time`) VALUES (?, ?, ?, ?)");
        } elseif ($ticket_duration == 'user_three_hours_parking_tickets') {
            // Calculate out_time for three hours parking ticket
            $out_time = date('Y-m-d H:i:s', strtotime('+3 hours', strtotime($current_time)));
            // Query to insert into parking_live table
            $stmt = $con->prepare("INSERT INTO `parking_live`(`user_id`, `parking_id`, `in_time`, `out_time`) VALUES (?, ?, ?, ?)");
        } elseif ($ticket_duration == 'user_weekly_parking_tickets') {
            // Calculate out_time for weekly parking ticket
            $out_time = date('Y-m-d H:i:s', strtotime('+1 week', strtotime($current_time)));
            // Query to insert into parking_live table
            $stmt = $con->prepare("INSERT INTO `parking_live`(`user_id`, `parking_id`, `in_time`, `out_time`) VALUES (?, ?, ?, ?)");
        }

        // Execute the query to insert into parking_live table
        if ($stmt) {
            $stmt->bind_param("iiss", $user_id, $parking_id, $current_time, $out_time);
            $stmt->execute();
            $stmt->close();
            $valid = true; // Ticket is valid
        }
        
        
         // Now, decrement the user's ticket count
            if ($ticket_duration == 'user_daily_parking_tickets') {
                $stmt = $con->prepare("UPDATE user_daily_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            } elseif ($ticket_duration == 'user_five_hours_parking_tickets') {
                $stmt = $con->prepare("UPDATE user_five_hours_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            } elseif ($ticket_duration == 'user_one_hour_parking_tickets') {
                $stmt = $con->prepare("UPDATE user_one_hour_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            } elseif ($ticket_duration == 'user_three_hours_parking_tickets') {
                $stmt = $con->prepare("UPDATE user_three_hours_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            } elseif ($ticket_duration == 'user_weekly_parking_tickets') {
                $stmt = $con->prepare("UPDATE user_weekly_parking_tickets SET number = number - 1 WHERE user_id = ? AND parking_ticket_id = ?");
            }
            
            if ($stmt) {
                $stmt->bind_param("ii", $user_id, $apt_id);
                $stmt->execute();
                $stmt->close();
            }
            
    
    
}

else{return 2;}
    return 3;
    }
    else{
        return 1;
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
 $current_time = date('Y-m-d H:i:s');
    $valid = false;
        $apt_id = null;
        $number = 0;
        $out_time = null;
        $num=0;

 echo check_ticket_validity($con, '1','21', 'user_weekly_parking_tickets','amea');
?>
