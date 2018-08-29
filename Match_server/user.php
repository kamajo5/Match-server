<?php

include_once 'config.php';
include 'Crypt/RSA.php';
$db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
$Login = "";
$Password = "";
$privkey = file_get_contents('private_key.pem');

if (isset($_POST['Login'])) {
    $Login = $_POST['Login'];
}

if (isset($_POST['Password'])) {
    $Password = $_POST['Password'];
}

if (!empty($Login) && !empty($Password)) {
    //deszyfracja 
    $Login = base64_decode($Login);
    openssl_private_decrypt($Login, $decryptedLogin, $privkey);

    $Password = base64_decode($Password);
    openssl_private_decrypt($Password, $decryptedPassword, $privkey);

    //opracje pobierajace dane z bazy danych
    $decryptedPassword = md5($decryptedPassword);
    $json = array();
    $query = "select ID, Login, Password, Email ,Activation from user where Login = '$decryptedLogin' and Password = '$decryptedPassword' Limit 1";
    $result = mysqli_query($db, $query);
    $row = $result->fetch_array(MYSQLI_NUM);
    if (mysqli_num_rows($result) > 0) {
        $json['success'] = 1;
        $json['message'] = "Done";
        $json['m_i'] = $row[0];
        $json['m_l'] = $row[1];
        $json['m_p'] = $row[2];
        $json['m_e'] = $row[3];
        $json['token'] = $row[4];
    } else {
        $json['success'] = 0;
        $json['message'] = "Wrong Input";
    }
    
    mysqli_close($db);

    $rsa = new Crypt_RSA();

    $t = file_get_contents('upload/public.key');
    $rsa->loadKey($t); // pubic key form server
    $pom = $rsa->encrypt(json_encode($json));
    echo base64_encode($pom);
}