(ns cryptlj.crypt
  (:import (javax.crypto KeyGenerator SecretKey Cipher SecretKeyFactory)
           (javax.crypto.spec SecretKeySpec PBEKeySpec)))

(def ^:dynamic *salt* "BIND SALT IN APP")

(defn cipher- [] (. Cipher getInstance "AES"))

(defn aes-keyspec [rawkey] (new SecretKeySpec rawkey "AES"))
 
(defn aes-keygen [] (. KeyGenerator getInstance "AES"))

(defn genkey
  [keygen]
  (. keygen init  128)
  (. (. keygen generateKey ) getEncoded))

(defn encrypt-
  [rawkey plaintext]
  (let [cipher (cipher-)
        mode (. Cipher ENCRYPT_MODE)]
    (. cipher init mode (aes-keyspec rawkey))
    (. cipher doFinal (. plaintext getBytes))))
     
(defn decrypt-
  [rawkey ciphertext]
  (let [cipher (cipher-)
        mode (. Cipher DECRYPT_MODE)]
    (. cipher init mode (aes-keyspec rawkey))
    (new String (. cipher doFinal ciphertext))))

(defn passkey
  [password salt & [iterations size]]
  (let [keymaker (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA1")
        pass (.toCharArray password)
        salt (.getBytes *salt*)
        iterations (or iterations 1000)
        size (or size 128)
        keyspec (PBEKeySpec. pass salt iterations size)]
    (-> keymaker (.generateSecret keyspec) .getEncoded)))

(defn encrypt
  [password salt plaintext]
  (encrypt- (passkey password salt) plaintext))

(defn decrypt
  [password salt cyphertext]
  (decrypt- (passkey password salt) cyphertext))

(defn as-string
  [bytes]
  (String. bytes "UTF-8"))

(defn from-string
  [string]
  (.getBytes string "UTF-8"))
