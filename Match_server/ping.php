<?php

if (isset($_FILES["file"])) {
    $file_path = "upload/" . basename($_FILES["file"]["name"]);

    if (move_uploaded_file($_FILES["file"]["tmp_name"], $file_path)) {
        echo "Success!";
    } else {
        echo "Failed!";
    }
} else {
    die("file is not set!");
}