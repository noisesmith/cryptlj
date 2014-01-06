(ns cryptlj.disk-test
  (:require [cryptlj.crypt :as crypt]
            [cryptlj.disk :as crypt-disk]
            [clojure.java.io :as io]
            [clojure.test :as test :refer [deftest is]]))

(test/use-fixtures :once
  (fn [run]
    (run)
    (doseq [f ["test/cryptlj/test-data.aes128"]]
      (.delete (io/file f)))))

(deftest serialization
  (let [salt "a salt helps keep things more secure"
        data (zipmap (range 3000) (reverse (range 3000)))
        data2 (zipmap (range 666) (reverse (range 666)))
        password "it's hard coming up with passwords isn't it?"
        file "test/cryptlj/test-data.aes128"
        resource "cryptlj/test-data.aes128"
        invalid "nonexistent invalid source of input"]
    (crypt-disk/to file password salt data)
    (is (re-matches #".*file must exist for decryption\n.*"
           (try (crypt-disk/from-disk invalid password salt)
                (catch AssertionError e (.getMessage e)))))
    (is (re-matches #".*resource must exist for decryption\n.*"
           (try (crypt-disk/from-resource invalid password salt)
                (catch AssertionError e (.getMessage e)))))
    (is (= data
           (crypt-disk/from-disk file password salt)))
    (is (= data
           (crypt-disk/from-resource resource password salt)))
    (crypt-disk/to file password salt data2)
    (is (= data2
           (crypt-disk/from-disk file password salt)))
    (is (= data2
           (crypt-disk/from-resource resource password salt)))))
