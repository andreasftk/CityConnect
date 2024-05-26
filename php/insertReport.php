<?php
include_once('config.php');

if($_SERVER['REQUEST_METHOD']=='POST'){
    // Check if all parameters are provided
    
        // Get parameters values
        $complainId = $_POST['complainId'];
        $userId = $_POST['userId'];
        $text = $_POST['text'];



        // Correct SQL query format
        $query = "INSERT INTO Report (complainId, userId, text) VALUES ('$complainId', '$userId', '$text') ON DUPLICATE KEY UPDATE text = '$text'";
        $result = mysqli_query($con, $query);

        if(!$result){
            // Provide detailed error message
            $response["success"] = false;
            $response["message"] = "Error: " . mysqli_error($con);
            echo json_encode($response);
        } else {
            $response["success"] = true;
            $response["message"] = "Successfully inserted";
            echo json_encode($response);
        }
   

    // Close connection
    mysqli_close($con);
}
?>
