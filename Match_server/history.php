<?php

include_once 'config.php';
include'Crypt/RSA.php';
$db = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);
$ID = "";
$privkey = file_get_contents('private_key.pem');

if (isset($_POST['ID'])) {
    $ID = $_POST['ID'];
}

if (!empty($ID)) {
    //deszyfracja 
    $ID = base64_decode($ID);
    openssl_private_decrypt($ID, $decrypted, $privkey);

    //opracje pobierajace dane z bazy danych
    $json = array();
    $query = "select Input_data, Output_data from input_data where ID_user = '$decrypted'";
    $result = mysqli_query($db, $query);
    if (mysqli_num_rows($result) > 0) {
        $json['success'] = 1;
        $json['message'] = "success";
        $json['count'] = mysqli_num_rows($result);
        $i = 0;
        while ($row = mysqli_fetch_array($result)) {
            $json[$i] = $row['Input_data'];
            $i++;
            $json[$i] = $row["Output_data"];
            $i++;
        }
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