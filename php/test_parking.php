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

    $stmt = $con->prepare("SELECT user_id, out_time FROM `parking_live` WHERE user_id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $stmt->bind_result($temp, $out_time);
    $stmt->fetch();
    $stmt->close();

    if ($temp == $user_id) {
        if ($out_time > $current_time) {
            $delete_stmt = $con->prepare("DELETE FROM `parking_live` WHERE user_id = ?");
            $delete_stmt->bind_param("i", $user_id);
            $delete_stmt->execute();
            $delete_stmt->close();
            return 4;
        } else if ($out_time <= $current_time) {
            $current_time1 = DateTime::createFromFormat('Y-m-d H:i:s', $current_time);
            $out_time1 = DateTime::createFromFormat('Y-m-d H:i:s', $out_time);
            $extra_time = $out_time1->diff($current_time1);
            $extra_time_string = $extra_time->format('%R%a days %h hours %i minutes %s seconds');

            $stmt = $con->prepare("INSERT INTO `parking_fines`(`parking_id`, `user_id`, `extra_time`, `duration_bought`) VALUES (?, ?, ?, ?)");
            $stmt->bind_param("iiss", $parking_id, $user_id, $extra_time_string, $ticket_duration);
            $stmt->execute();
            $stmt->close();

            $delete_stmt = $con->prepare("DELETE FROM `parking_live` WHERE user_id = ?");
            $delete_stmt->bind_param("i", $user_id);
            $delete_stmt->execute();
            $delete_stmt->close();
            return 5;
        }
    } else {
        if ($user_cat == 'amea') {
            $stmt = $con->prepare("SELECT available_amea_spaces FROM `parkings` WHERE id = ?");
        } else {
            $stmt = $con->prepare("SELECT available_spaces FROM `parkings` WHERE id = ?");
        }
        $stmt->bind_param("i", $parking_id);
        $stmt->execute();
        $stmt->bind_result($num);
        $stmt->fetch();
        $stmt->close();

        if (intval($num) > 0) {
            $stmt = $con->prepare("SELECT category_id FROM `parkings` WHERE id = ?");
            $stmt->bind_param("i", $parking_id);
            $stmt->execute();
            $stmt->bind_result($area);
            $stmt->fetch();
            $stmt->close();

            $stmt = null;
            $sql = "";
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

            if ($sql) {
                $stmt = $con->prepare($sql);
                $stmt->bind_param("ssi", $area, $user_cat, $user_id);
                $stmt->execute();
                $stmt->bind_result($apt_id, $number);
                $stmt->fetch();
                $stmt->close();
            }

            if (intval($number) > 0) {
                if ($ticket_duration == 'Daily') {
                    $out_time = date('Y-m-d H:i:s', strtotime('+1 day', strtotime($current_time)));
                } elseif ($ticket_duration == '5 hours') {
                    $out_time = date('Y-m-d H:i:s', strtotime('+5 hours', strtotime($current_time)));
                } elseif ($ticket_duration == '1 hour') {
                    $out_time = date('Y-m-d H:i:s', strtotime('+1 hour', strtotime($current_time)));
                } elseif ($ticket_duration == '3 hours') {
                    $out_time = date('Y-m-d H:i:s', strtotime('+3 hours', strtotime($current_time)));
                } elseif ($ticket_duration == 'Weekly') {
                    $out_time = date('Y-m-d H:i:s', strtotime('+1 week', strtotime($current_time)));
                }

                $stmt = $con->prepare("INSERT INTO `parking_live`(`user_id`, `parking_id`, `in_time`, `out_time`) VALUES (?, ?, ?,?)");
                $stmt->bind_param("iiss", $user_id, $parking_id, $current_time, $out_time);
                $stmt->execute();
                $stmt->close();

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

                $stmt->bind_param("ii", $user_id, $apt_id);
                $stmt->execute();
                $stmt->close();
                return 3;
            } else {
                return 2;
            }
        } else {
            return 1;
        }
    }
}

// Decode the incoming JSON data
$data = file_get_contents("php://input");
$json = json_decode($data, true);

// Validate and process the input data
if (isset($json['qrData']) && isset($json['userId']) && isset($json['duration'])) {
    $parking_id = $json['qrData'];
    $user_id = $json['userId'];
    $ticket_duration = $json['duration'];

    // Check the ticket validity
    $result = check_ticket_validity($con, $parking_id, $user_id, $ticket_duration);

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
