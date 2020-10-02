(ns csv-create-parse.core
  (:gen-class)
  (:use faker.name faker.lorem))

(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])

(defn name-vec [] [(first-name) (last-name)])

(def name-csv 
		(with-open [writer (io/writer "first-and-last-names.csv")]
  		(csv/write-csv writer
                 		(repeatedly 5000 name-vec))))

(def filename "first-and-last-names.csv")

(def name-keys [:first-name :last-name])

(def conversions {:first-name identity
                  :last-name identity})

(defn convert
 [name-key value]
 ((get conversions name-key) value))

(defn parse
  "Convert a CSV into rows of columns"
  [string]
  (map #(clojure.string/split % #",")
       (clojure.string/split string #"\n")))

(defn mapify
  "Return a seq of maps like {:first-name \"Rupert\" :last-name \"Jackson\"}"
  [rows]
  (map (fn [unmapped-row]
         (reduce (fn [row-map [name-key value]]
                   (assoc row-map name-key (convert name-key value)))
                 {}
                 (map vector name-keys unmapped-row)))
       rows))

(defn name-filter
  [key name records]
  (filter #(= (key %) name) records))

;; call with (name-filter :first-name "Rey" (mapify (parse (slurp filename))))