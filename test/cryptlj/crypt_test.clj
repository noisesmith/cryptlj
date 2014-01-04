(ns cryptlj.crypt-test
  (:require [clojure.test :as test :refer [deftest is]]
            [cryptlj.crypt :as crypt]))

;; a generated key is more secure, and should be used if possible
(deftest generated-key
  (let [key (crypt/genkey (crypt/aes-keygen))
        plain "The eagle flies at midnight."
        cipher (crypt/encrypt- key plain)]
    (is (not (= plain cipher)))
    (is (= (crypt/decrypt- key cipher)
           plain))))

;; password encryption needs a salt
(deftest password-encryption
  (let [salt "THE SALT WE ARE USING"
        message "Purple Hairy Spiders"
        password "1234"
        secret (crypt/encrypt password salt message)]
    (is (= (crypt/decrypt password salt secret)
           message))))

;; conversion of encrypted format to/from string
(deftest conversion
  (let [string "✈ this went all the way through ☃"]
    (= string
       (crypt/as-string (crypt/from-string string)))))

;; establishing behavior with the canonical aes test vectors

(defn key-bytes
  "takes hex string of key and returns a byte array"
  [keystr]
  (byte-array
   (map (fn make-byte [[high low]]
          (byte (- (read-string (str "16r" high low)) 128)))
        (partition 2 keystr))))

(defn bytes-key
  "takes a byte array and returns a hex string"
  [bytes]
  (apply str
         (map #(format "%x" (+ 128 %))
              bytes)))

(def enc-key (key-bytes "2b7e151628aed2a6abf7158809cf4f3c"))

(def vectors
  "standard vectors to verify 128 bit aes
 http://www.inconteam.com/software-development/41-encryption/55-aes-test-vectors"
  [{:plain "6bc1bee22e409f96e93d7e117393172a"
    :cypher "3ad77bb40d7a3660a89ecaf32466ef97"}
   {:plain "ae2d8a571e03ac9c9eb76fac45af8e51"
    :cypher "f5d3d58503b9699de785895a96fdbaaf"}
   {:plain "30c81c46a35ce411e5fbc1191a0a52ef"
    :cypher "43b1cd7f598ece23881b00e3ed030688"}
   {:plain "f69f2445df4f9b17ad2b417be66c3710"
    :cypher "7b0c785e27e8ad3f8223207104725dd4"}])
 
(deftest key-bytes-test
  (is (= (:plain (first vectors))
         (bytes-key (key-bytes (:plain (first vectors)))))))

;; this test will not work because padding!
#_
(deftest test-vectors
  (doseq [{:keys [plain cypher]} vectors]
    (is (= (bytes-key (crypt/encrypt- enc-key
                                      (crypt/as-string (key-bytes plain))))
           cypher))))
