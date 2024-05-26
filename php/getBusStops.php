<?php

include_once('config.php');



// SQL query to select all bus routes
$sql = "SELECT * FROM bus_stops";
$result = $con->query($sql);

$bus_stops = array();

if ($result->num_rows > 0) {
    // Output data of each row
    while($row = $result->fetch_assoc()) {
        $bus_stops[] = array(
            "route" => intval($row["route"]),
            "longitude" => floatval($row["longitude"]),
            "latitude" => floatval($row["latitude"]),
            "busStopId" => $row["bus_stop_id"],
            "name" => $row["name"],
            "startPoint" => intval($row["start_point"]),
            "endPoint" => intval($row["end_point"])
        );
    }
} else {
    echo json_encode(array("message" => "No bus stops found"));
    $con->close();
    exit();
}
$con->close();
echo json_encode($bus_stops);
?>
