(ns cryptlj.crypt
  (:import (javax.crypto KeyGenerator SecretKey Cipher SecretKeyFactory)
           (javax.crypto.spec SecretKeySpec PBEKeySpec)))

(def cipher- (constantly (Cipher/getInstance "AES")))

(defn aes-keyspec [rawkey] (SecretKeySpec. rawkey "AES"))
 
(def aes-keygen (constantly (KeyGenerator/getInstance "AES")))

(defn genkey
  [keygen]
  (.init keygen 128)
  (.getEncoded (.generateKey  keygen)))

(defn encrypt-
  [rawkey plaintext]
  (let [cipher (cipher-)
        mode Cipher/ENCRYPT_MODE]
    (.init cipher mode (aes-keyspec rawkey))
    (.doFinal cipher (.getBytes plaintext))))

(defn decrypt-
  [rawkey ciphertext]
  (let [cipher (cipher-)
        mode (. Cipher DECRYPT_MODE)]
    (. cipher init mode (aes-keyspec rawkey))
    (new String (. cipher doFinal ciphertext))))

(defn passkey
  [password raw-salt & [iterations size]]
  (let [keymaker (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA1")
        pass (.toCharArray password)
        salt (.getBytes raw-salt)
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
