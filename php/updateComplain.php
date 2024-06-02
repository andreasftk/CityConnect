<?php
include_once('config.php');

if($_SERVER['REQUEST_METHOD']=='POST'){
    // Check if complainId and title are provided
    if(isset($_POST['complainId']) && isset($_POST['title'])) {
        // Get parameters values
        $complainId = $_POST['complainId'];
        $title = $_POST['title'];

        // Correct SQL query format for updating only the title
        $query = "UPDATE Complain SET title='$title' WHERE complainId='$complainId'";
        $result = mysqli_query($con, $query);

        if(!$result){
            // Provide detailed error message
            $response["success"] = false;
            $response["message"] = "Error: " . mysqli_error($con);
            echo json_encode($response);
        } else {
            $response["success"] = true;
            $response["message"] = "Successfully updated";
            echo json_encode($response);
        }
    } else {
        $response["success"] = false;
        $response["message"] = "complainId and title are required";
        echo json_encode($response);
    }

    // Close connection
    mysqli_close($con);
}
?>
