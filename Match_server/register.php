<?php

include_once 'config.php';
include 'Crypt/RSA.php';
$db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
$Login = "";
$Password = "";
$Email = "";
$privkey = file_get_contents('private_key.pem');

if (isset($_POST['Login'])) {
    $Login = $_POST['Login'];
}

if (isset($_POST['Password'])) {
    $Password = $_POST['Password'];
}

if (isset($_POST['Email'])) {
    $Email = $_POST['Email'];
}

function isEmailUsernameExist($Login, $Email) {
    $query = "select * from user where Login = '$Login' AND Email = '$Email'";
    $result = mysqli_query(mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME), $query);
    if (mysqli_num_rows($result) > 0) {
        mysqli_close(mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME));
        return true;
    }
    return false;
}

function isValidEmail($Email) {
    return filter_var($Email, FILTER_VALIDATE_EMAIL) !== false;
}

// Registration

if (!empty($Login) && !empty($Password) && !empty($Email)) {
    //deszyfracja 
    $Login = base64_decode($Login);
    openssl_private_decrypt($Login, $decryptedLogin, $privkey);

    $Password = base64_decode($Password);
    openssl_private_decrypt($Password, $decryptedPassword, $privkey);

    $Email = base64_decode($Email);
    openssl_private_decrypt($Email, $decryptedEmail, $privkey);

    $json = array();
    $isExisting = isEmailUsernameExist($decryptedLogin, $decryptedEmail);
    $decryptedPassword = md5($decryptedPassword);
    if ($isExisting) {
        $json['success'] = 0;
        $json['message'] = "Error in registering. Probably the Login/Email already exists";
    } else {
        $isValid = isValidEmail($decryptedEmail);
        if ($isValid) {
            $query = "insert into user (Login, Password, Email) values ('$decryptedLogin', '$decryptedPassword', '$decryptedEmail')";
            $inserted = mysqli_query($db, $query);
            $query = "select ID from user where Login = '$decryptedLogin' and Password = '$decryptedPassword' Limit 1";
            $result = mysqli_query($db, $query);
            $row = $result->fetch_array(MYSQLI_NUM);
            if ($inserted == 1) {
                $json['how_id'] = 1;
                $json['message'] = "Done";
                $json['how_id'] = $row[0];
            } else {
                $json['success'] = 0;
                $json['message'] = "Error in registering. Probably the Login/Email already exists";
            }
        } else {
            $json['success'] = 0;
            $json['message'] = "Error in registering. Email Address is not valid";
        }
    }
    mysqli_close($db);
    //szyfrowanie danych za pomoc¹ klucza z aplikacji mobilnej
    $rsa = new Crypt_RSA();
    $t = file_get_contents('upload/public.key');
    $rsa->loadKey($t); // pubic key form device
    $pom = $rsa->encrypt(json_encode($json));
    echo base64_encode($pom);
}