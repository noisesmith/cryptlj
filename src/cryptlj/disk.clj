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
  [file-name password salt]
  (let [data (ByteArrayOutputStream.)]
    (-> file-name
         FileInputStream.
         (io/copy data))
    (->> data
         .toByteArray
         (crypt/decrypt password salt)
         read-string)))
