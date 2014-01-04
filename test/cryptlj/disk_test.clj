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
        password "it's hard coming up with passwords isn't it?"
        file "test/cryptlj/test-data.aes128"]
    (crypt-disk/to file password salt data)
    (= data
       (crypt-disk/from file password salt))))
