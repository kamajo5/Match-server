<?php

// Create the keypair
$res = openssl_pkey_new(array(
    "digest_alg" => "sha512",
    "private_key_bits" => 1024,
    "private_key_type" => OPENSSL_KEYTYPE_RSA,
        ));
// Get private key
openssl_pkey_export($res, $privkey);
echo $privkey;
// Get public key
$pubkey = openssl_pkey_get_details($res);
$pubkey = $pubkey["key"];
echo $pubkey;
openssl_public_encrypt("hello world", $crypted, $pubkey);
echo $crypted;
echo "\n";
echo base64_encode($crypted);
echo "\n";
openssl_private_decrypt($crypted, $decrypted, $privkey);
echo $decrypted;
