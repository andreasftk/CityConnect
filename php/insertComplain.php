<?php
include_once('config.php');

if($_SERVER['REQUEST_METHOD']=='POST'){
    // Check if all parameters are provided
    
        // Get parameters values
        $complainId = $_POST['complainId'];
        $title = $_POST['title'];
        $description = $_POST['description'];
        $suggestions = $_POST['suggestions'];
        $photo = $_POST['photo'];
        $totalRating = $_POST['totalRating'];
        $date = $_POST['date'];
        $location = $_POST['location'];
        $userId = $_POST['userId'];
        $totalReviews = $_POST['totalReviews'];



        // Correct SQL query format
        $query = "INSERT INTO Complain (complainId, title, description, suggestions, photo, totalRating, location, userId) VALUES ('$complainId', '$title', '$description', '$suggestions', '$photo', '$totalRating' , '$location', '$userId',$totalReviews)";
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
