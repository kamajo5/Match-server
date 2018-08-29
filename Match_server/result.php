<?php

include_once 'config.php';
include'Crypt/RSA.php';
$db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
$ID = "";
$Input = "";
$privkey = file_get_contents('private_key.pem');

if (isset($_POST['ID'])) {
    $ID = $_POST['ID'];
}

if (isset($_POST['Input'])) {
    $Input = $_POST['Input'];
}

if (!empty($ID) && !empty($Input)) {
    //deszyfracja 
    $ID = base64_decode($ID);
    openssl_private_decrypt($ID, $decryptedID, $privkey);

    $Input = base64_decode($Input);
    openssl_private_decrypt($Input, $decryptedInput, $privkey);

    //opracje pobierajace dane z bazy danych
    $json = array();
    $query = "select Output_data from input_data where Input_data = '$decryptedInput' and ID_User = '$decryptedID'";
    $result = mysqli_query($db, $query);
    $row = $result->fetch_array(MYSQLI_NUM);
    if (mysqli_num_rows($result) > 0) {
        $json['success'] = 1;
        $json['message'] = $row[0];
    } else {
        $json['succes'] = 0;
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