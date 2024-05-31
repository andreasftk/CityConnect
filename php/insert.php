insert.php
<?php
include_once('config.php');

if($_SERVER['REQUEST_METHOD']=='POST' ){
    $id = $_POST['id'];
    $username = $_POST['username'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $name = $_POST['name'];

    // Correct SQL query format
    $query = "INSERT INTO User (id, username, email, password, name) VALUES ('$id', '$username', '$email', '$password', '$name')";
    $result = mysqli_query($con, $query);

    if(!$result){
        // Provide detailed error message
        $response["success"] = "false";
        $response["message"] = "Error: " . mysqli_error($con);
        echo json_encode($response);
        mysqli_close($con);
    }else{
        $response["success"] = "true";
        $response["message"] = "Successfully inserted";
        echo json_encode($response);
        mysqli_close($con);
    }
}
?>