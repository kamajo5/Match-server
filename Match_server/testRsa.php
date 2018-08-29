<?php

include 'text_cut.php';
$pom = new text();
$pom -> divide_text("kamil");
$pom -> encrypt_text();

// Create the keypair
/*
include('Crypt/RSA.php');
$rsa = new Crypt_RSA();
    $t= file_get_contents('upload/public.key');
$rsa->loadKey($t); // private key
 
 echo $rsa->encrypt('e');
*/

//$t= file_get_contents('upload/rsaprivkey.[');
//$rsa->loadKey($t); // private key
//echo $rsa->decrypt($pom);
/* 
//set_time_limit(0);
$privkey= file_get_contents('private_key.pem');
$crypted = file_get_contents('result.txt');

openssl_private_decrypt($crypted, $decrypted, $privkey);
echo $decrypted;


/*$rsa = new Crypt_RSA();
//$t= file_get_contents('public_key.der');
//$rsa->loadKey($t); // public key

$plaintext = '1';

$rsa->setEncryptionMode(CRYPT_RSA_ENCRYPTION_OAEP);
//$ciphertext = $rsa->encrypt($plaintext);
//file_put_contents('e.txt', $ciphertext);

echo "\n";
$t= file_get_contents('private_key.pem');
$rsa->loadKey($t); // private key
echo $rsa->decrypt(file_get_contents('result.txt'));





/*
$rsa = new Crypt_RSA();
$rsa->setHash('sha1');
$rsa->setMGFHash('sha1');
$rsa->setEncryptionMode(CRYPT_RSA_ENCRYPTION_OAEP);
$rsa->setPrivateKeyFormat(CRYPT_RSA_PRIVATE_FORMAT_PKCS8);
$rsa->setPublicKeyFormat( CRYPT_RSA_PUBLIC_FORMAT_PKCS8);

$res = $rsa->createKey(2048);

$privateKey = $res['privatekey'];
$publicKey = $res['publickey'];


file_put_contents('public.der', $publicKey);
file_put_contents('private.der', $privateKey);
/*
//$letters = array('=');
//$fruit = "";
//$text = $publicKey;
//$output = str_replace($letters, $fruit, $text);
//////////echo base64_encode($publicKey);
echo file_get_contents('public_key.der');
//$publicKey = str_replace($text);
//echo $res['publickey'];
/* //echo base64_encode($privateKey);
   echo base64_encode(file_get_contents('private.key'));
   echo "\n";
   echo file_get_contents('public.key');
    $rsa->loadKey(file_get_contents('public.key'));
    echo "\n";
    echo $rsa;
    */
    //$pom =openssl_pkey_get_public('public.key');
    //function extract_key($pkcs1) {
    # strip out -----BEGIN/END RSA PUBLIC KEY-----, line endings, etc
   /* $temp = preg_replace('#.*?^-+[^-]+-+#ms', '', $pkcs1, 1);
        $temp = preg_replace('#-+[^-]+-+#', '', $temp);
        return str_replace(array("\r", "\n", ' '), '', $temp);
}

$rsa = new Crypt_RSA();
$keysize=2048;
 $pubformat = "CRYPT_RSA_PUBLIC_FORMAT_PKCS1";
 $privformat=  "CRYPT_RSA_PRIVATE_FORMAT_PKCS8";
$rsa->setPrivateKeyFormat(CRYPT_RSA_PRIVATE_FORMAT_PKCS8);
$rsa->setPublicKeyFormat(CRYPT_RSA_PUBLIC_FORMAT_PKCS1);
$d = $rsa->createKey($keysize);
 $Kp = $d['publickey'];
 $Ks = $d['privatekey'];


$rsa = new Crypt_RSA();
$rsa->setPrivateKeyFormat(CRYPT_RSA_PRIVATE_FORMAT_PKCS8);
$rsa->setPublicKeyFormat(CRYPT_RSA_PUBLIC_FORMAT_PKCS1);
$d = $rsa->createKey($keysize);
$Kver = $d['publickey'];
$KSign = $d['privatekey'];

file_put_contents("pub_verify_key.txt",extract_key($Kver));

$plainText = "53965C38-E950-231A-8417-074BD95744A4:22-434-565-54544:".extract_key($Kp);

file_put_contents("plain.txt",$plainText);

// Signing
$hash = new Crypt_Hash('sha256');
$rsa = new Crypt_RSA();    
$rsa->loadKey($KSign);
$rsa->setSignatureMode(CRYPT_RSA_ENCRYPTION_PKCS1);
$rsa->setHash('sha256');

$signature = $rsa->sign($plainText);

$signedHS = base64_encode($signature);

file_put_contents("signedkey.txt", $signedHS);

// Verification

$signature = base64_decode($signedHS);
$rsa->loadKey($Kver);
$status = $rsa->verify($plainText, $signature);

var_dump($status);*/

