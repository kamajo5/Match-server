<?php

include_once 'config.php';
include'Crypt/RSA.php';
$db_table = "user";
$db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
$ID = "";
$Token = "";
$privkey = file_get_contents('private_key.pem');


if (isset($_POST['ID'])) {
    $ID = $_POST['ID'];
}

if (isset($_POST['Token'])) {
    $Token = $_POST['Token'];
}

if (!empty($ID) && !empty($Token)) {
    //deszyfracja 
    $ID = base64_decode($ID);
    openssl_private_decrypt($ID, $decryptedID, $privkey);

    $Token = base64_decode($Token);
    openssl_private_decrypt($Token, $decryptedToken, $privkey);
    file_put_contents("Token.txt", $decryptedID);
    //opracje pobierajace dane z bazy danych
    $json = array();
    $query = "select Token from user where ID = '$decryptedID' Limit 1";
    $result = mysqli_query($db, $query);
    $row = $result->fetch_array(MYSQLI_NUM);
    if ($row[0] == $decryptedToken) {
        $query = "UPDATE user SET Activation = 1 WHERE ID = '$decryptedID'";
        $result = mysqli_multi_query($db, $query);
        $json['success'] = 1;
        $json['message'] = "Active";
        $json['isActive'] = 1;
    } else {
        $json['success'] = 0;
        $json['message'] = "Wrong token";
    }
    mysqli_close($db);
    //szyfrowanie danych za pomoc¹ klucza z aplikacji mobilnej
    $rsa = new Crypt_RSA();
    $t = file_get_contents('upload/public.key');
    $rsa->loadKey($t); // pubic key form server
    $pom = $rsa->encrypt(json_encode($json));
    echo base64_encode($pom);
}