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
  [input-stream password salt]
  (let [data (ByteArrayOutputStream.)]
    (io/copy input-stream data)
    (->> data
         .toByteArray
         (crypt/decrypt password salt)
         read-string)))
