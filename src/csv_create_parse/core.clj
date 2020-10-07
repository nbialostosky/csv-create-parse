(ns csv-create-parse.core
  (:gen-class)
  (:use faker.name faker.lorem))

(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])

(defn name-vec [] [(first-name) (last-name)])

;; Creates randomly generated CSV file of 5000 first and last names

(def filename "first-and-last-names.csv")

(def name-csv 
		(with-open [writer (io/writer filename)]
  		(csv/write-csv writer
                 		(repeatedly 5000 name-vec))))

;; Let us know if the data is contained true/false

;; call with something like: (clojure.string/includes? (slurp filename) "Cade")

;; Let us iterate through matched data and return nil if not present

(defn name-matcher
		"Find name in dataset and return it, otherwise return nil if not present"
		[name-string file]
		(re-matcher (re-pattern name-string) (slurp file)))

;; call with something like: (re-find (name-matcher "Cade" filename))

;; Create key-value pairs of first / last name and allow for all instance lookup

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

;; call with (name-filter :first-name "Cade" (mapify (parse (slurp filename))))