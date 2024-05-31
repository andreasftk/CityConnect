<?php
include_once('config.php');

// Function to check ticket validity
function check_ticket_validity($con, $parking_id, $user_id, $ticket_duration,$user_cat) {
    $current_time = date('Y-m-d H:i:s');
    


  
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
    




// Prepare and execute the statement to get number for user_daily_parking_tickets
$stmt_daily = $con->prepare("
    SELECT apt.id, uohpt.number
    FROM user_daily_parking_tickets uohpt
    JOIN daily_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
    JOIN all_parking_tickets apt ON oht.id = apt.id
    WHERE apt.region = ?
    AND oht.user_category = ?
    AND uohpt.user_id = ?
");
if ($stmt_daily) {
    $stmt_daily->bind_param("sss", $area, $user_cat, $user_id);
    $stmt_daily->execute();
    $stmt_daily->bind_result($apt_id_daily, $number_daily);
    $stmt_daily->fetch();
    $stmt_daily->close();
}

// Prepare and execute the statement to get number for user_five_hours_parking_tickets
$stmt_five_hours = $con->prepare("
    SELECT apt.id, uohpt.number
    FROM user_five_hours_parking_tickets uohpt
    JOIN five_hours_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
    JOIN all_parking_tickets apt ON oht.id = apt.id
    WHERE apt.region = ?
    AND oht.user_category = ?
    AND uohpt.user_id = ?
");
if ($stmt_five_hours) {
    $stmt_five_hours->bind_param("sss", $area, $user_cat, $user_id);
    $stmt_five_hours->execute();
    $stmt_five_hours->bind_result($apt_id_five_hours, $number_five_hours);
    $stmt_five_hours->fetch();
    $stmt_five_hours->close();
}

// Repeat the process for other ticket categories...


// Prepare and execute the statement to get number for user_one_hour_parking_tickets
$stmt_one_hour = $con->prepare("
    SELECT apt.id, uohpt.number
    FROM user_one_hour_parking_tickets uohpt
    JOIN one_hour_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
    JOIN all_parking_tickets apt ON oht.id = apt.id
    WHERE apt.region = ?
    AND oht.user_category = ?
    AND uohpt.user_id = ?
");
if ($stmt_one_hour) {
    $stmt_one_hour->bind_param("sss", $area, $user_cat, $user_id);
    $stmt_one_hour->execute();
    $stmt_one_hour->bind_result($apt_id_one_hour, $number_one_hour);
    $stmt_one_hour->fetch();
    $stmt_one_hour->close();
}

// Prepare and execute the statement to get number for user_three_hours_parking_tickets
$stmt_three_hours = $con->prepare("
    SELECT apt.id, uohpt.number
    FROM user_three_hours_parking_tickets uohpt
    JOIN three_hours_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
    JOIN all_parking_tickets apt ON oht.id = apt.id
    WHERE apt.region = ?
    AND oht.user_category = ?
    AND uohpt.user_id = ?
");
if ($stmt_three_hours) {
    $stmt_three_hours->bind_param("sss", $area, $user_cat, $user_id);
    $stmt_three_hours->execute();
    $stmt_three_hours->bind_result($apt_id_three_hours, $number_three_hours);
    $stmt_three_hours->fetch();
    $stmt_three_hours->close();
}

// Prepare and execute the statement to get number for user_weekly_parking_tickets
$stmt_weekly = $con->prepare("
    SELECT apt.id, uohpt.number
    FROM user_weekly_parking_tickets uohpt
    JOIN weekly_parking_ticket oht ON uohpt.parking_ticket_id = oht.id
    JOIN all_parking_tickets apt ON oht.id = apt.id
    WHERE apt.region = ?
    AND oht.user_category = ?
    AND uohpt.user_id = ?
");
if ($stmt_weekly) {
    $stmt_weekly->bind_param("sss", $area, $user_cat, $user_id);
    $stmt_weekly->execute();
    $stmt_weekly->bind_result($apt_id_weekly, $number_weekly);
    $stmt_weekly->fetch();
    $stmt_weekly->close();
}

$numbers = [
    'number1' => $number_one_hour ?? 0,
    'number2' => $number_three_hours ?? 0,
    'number3' => $number_five_hours ?? 0,
    'number4' => $number_daily ?? 0,
    'number5' => $number_weekly ?? 0,
];

// Encode the array as JSON
$json_data = json_encode($numbers);












    return $json_data;
    
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


 echo check_ticket_validity($con, '1','21', 'user_weekly_parking_tickets','amea');
?>