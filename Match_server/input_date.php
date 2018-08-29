<?php

include_once 'config.php';
include'Crypt/RSA.php';

$db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
$ID_User = "";
$Input_data = "";
$Option = "";
$privkey = file_get_contents('private_key.pem');

if (isset($_POST['ID_User'])) {
    $ID_User = $_POST['ID_User'];
}

if (isset($_POST['Input_data'])) {
    $Input_data = $_POST['Input_data'];
}

if (isset($_POST['Option'])) {
    $Option = $_POST['Option'];
}

if (!empty($ID_User) && !empty($Input_data) && !empty($Option)) {
    //deszyfracja 
    $ID_User= base64_decode($ID_User);
    openssl_private_decrypt($ID_User, $decryptedID_User, $privkey);
    
    $Input_data= base64_decode($Input_data);
    openssl_private_decrypt($Input_data, $decryptedInput_data, $privkey);
    
    $Option= base64_decode($Option);
    openssl_private_decrypt($Option, $decryptedOption, $privkey);
    
    //opracje umieszczaj¹ce dane w tabeli 
    $json = array();
    $query = "insert into input_data (ID_user, Input_data, Select_option) values ('$decryptedID_User', '$decryptedInput_data', '$decryptedOption')";
    $inserted = mysqli_query($db, $query);
    if ($inserted == 1) {
        $json['success'] = 1;
        $json['message'] = "Done";
    } else {
        $json['success'] = 0;
        $json['message'] = "Error";
    }
    mysqli_close($db);
    //szyfrowanie danych za pomoc¹ klucza z aplikacji mobilnej
    $rsa = new Crypt_RSA();
    $t = file_get_contents('upload/public.key');
    $rsa->loadKey($t); // pubic key form server
    $pom = $rsa->encrypt(json_encode($json));
    echo base64_encode($pom);
}