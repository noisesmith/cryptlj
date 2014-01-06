(ns cryptlj.disk
  (:require [cryptlj.crypt :as crypt]
            [clojure.java.io :as io])
  (:import (java.io FileOutputStream FileInputStream ByteArrayOutputStream)))

(defn to
  [file-name password salt object]
  (let [out (FileOutputStream. file-name)]
    (->>
     object
     pr-str
     (crypt/encrypt password salt)
     (.write out))
    (.close out)))

(defn from
  [source password salt]
  (let [data (ByteArrayOutputStream.)]
    (io/copy (io/input-stream source) data)
    (->> data
         .toByteArray
         (crypt/decrypt password salt)
         read-string)))

(defn from-disk [file-name password salt]
  (-> file-name
      io/file
      (from password salt)))

(defn from-resource [resource-location password salt]
  (-> resource-location
      io/resource
      (assert "resource must exist for decryption")
      (from password salt)))
