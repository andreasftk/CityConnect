<?php
include_once('config.php');

if($_SERVER['REQUEST_METHOD']=='POST'){
    // Check if all parameters are provided
    
        // Get parameters values
        $userId = $_POST['userId'];
        $star = $_POST['star'];
        $complainId = $_POST['complainId'];



        // Correct SQL query format
        $query = "INSERT INTO Review (userId, star, complainId) VALUES ('$userId', '$star', '$complainId') ON DUPLICATE KEY UPDATE star = $star";
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
