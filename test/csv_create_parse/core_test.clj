(ns csv-create-parse.core-test
  (:require [clojure.test :refer :all]
            [csv-create-parse.core :refer :all]))

(require '[clojure.data.csv :as csv]
         '[clojure.java.io :as io])

(def filename-test "first-and-last-names-test.csv")

(def parse-data '(["Wilburn" "Hettinger"] ["Leif" "Robel"] ["Ansel" "Ullrich"]))

(def map-data '({:first-name "Wilburn", :last-name "Hettinger"} {:first-name "Leif", :last-name "Robel"} {:first-name "Ansel", :last-name "Ullrich"}))

(deftest name-vec-string-test
  (testing "Ensure function returns a vector of strings"
    (is (every? string? (name-vec)))))

(deftest name-vec-vector-test
		(testing "Ensure function returns a vector"
				(is (vector? (name-vec)))))

(deftest name-matcher-boolean-true
		(testing "Ensure function returns true if name present"
				(is (true? (clojure.string/includes? (slurp filename-test) "Wilburn")))))

(deftest name-matcher-boolean-false
		(testing "Ensure function returns false if name not present"
				(is (false? (clojure.string/includes? (slurp filename-test) "Nick")))))

(deftest name-matcher-test-present
		(testing "Ensure name is returned if present"
				(is (= "Wilburn" (re-find (name-matcher "Wilburn" filename-test))))))

(deftest name-matcher-test-nil
		(testing "Ensure nil is returned if name not present"
				(is (nil? (re-find (name-matcher "Nick" filename-test))))))

(deftest parse-test
		(testing "Ensures data is parsed correctly"
				(is (= parse-data (parse (slurp filename-test))))))

(deftest mapify-test
		(testing "Ensures first and last names are mapped correctly"
				(is (= map-data (mapify (parse (slurp filename-test)))))))

(deftest name-filter-test-first-name
		(testing "Ensures matching on first name returns whole dataset"
				(is (= '({:first-name "Wilburn", :last-name "Hettinger"}) (name-filter :first-name "Wilburn" (mapify (parse (slurp filename-test))))))))

(deftest name-filter-test-first-name-not-found
		(testing "Ensures no data is returned when first name not found"
				(is (= '() (name-filter :first-name "Nick" (mapify (parse (slurp filename-test))))))))

(deftest name-filter-test-last-name
		(testing "Ensures matching on last name returns whole dataset"
				(is (= '({:first-name "Ansel", :last-name "Ullrich"}) (name-filter :last-name "Ullrich" (mapify (parse (slurp filename-test))))))))

(deftest name-filter-test-last-name-not-found
		(testing "Ensures no data is returned when last name not found"
				(is (= '() (name-filter :last-name "Nick" (mapify (parse (slurp filename-test))))))))