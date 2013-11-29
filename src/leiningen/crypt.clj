(ns leiningen.crypt
  (:refer-clojure :exclude [load])
  (:require [cryptlj.disk :as disk]
            [cryptlj.crypt :as crypt]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]))

(declare help configured default-password load show store update)

(defn crypt
  "Store encrypted edn data that you can access at runtime."
  [project & [option args]]
  {:pre [(even? (count args))]}
  (let [opts (->> args
                  (partition-all 2)
                  (map vec)
                  (into {}))
        environment (System/getenv)
        file (configured "file" project environment opts "cryptlj-keys.aes128")
        salt (configured "salt" project environment opts "Mr. Peanut")
        password (configured "password" project environment opts
                             (or (get opts "password")
                                 (default-password (:name project))))]
    (case option
      "show" (show file password salt)
      "store" (do (println "ready for new data: ")
                  (when-let [data (read *in*)]
                    (store data file password salt)))
      "update" (do (println "ready for update function: ")
                   (when-let [data (read *in*)]
                     (update (eval data) file password salt)))
      "help" (help)
      (help))))

(defn help
  [& rest]
 (do (println)
     (println "lein crypt:")
     (println "  available subtasks")
     (println "  show")
     (println "    displays the stored data")
     (println "  store [password replacement]")
     (println "    stores a new set of data and overrides the password")
     (println "  update")
     (println "    takes a function that will transform the stored data as edn")
     (println "  help")
     (println)))

(defn configured
  [key project environment opts default]
  (or (get opts key)
      (get environment key)
      (get-in project [:cryptlj (keyword key)])
      default))

(defn default-password
  [project]
  (try
    (->
     "HOME"
     System/getenv
     (str "/.cryptlj-keys")
     slurp
     read-string
     (#(and (associative? %) (get % keyword project))))
    (catch Throwable t
      (println "error with finding a default password" t)
      "hunter2")))

(defn load
  [file password salt]
  (disk/from (io/input-stream file) password salt))

(def show (comp pprint/pprint load))

(defn store
  [data file password salt]
  (disk/to file password salt data))

(defn update
  [f file password salt]
  (store
   (f (load file password salt))
   file password salt))

