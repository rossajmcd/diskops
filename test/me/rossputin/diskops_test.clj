(ns me.rossputin.diskops-test
  (:require [clojure.java.io :as io]
            [clojure.test :refer :all]
            [me.rossputin.diskops :as dsk]))

(def p1 (io/file "test-data/paths/1.txt"))
(def p2 (io/file "test-data/paths/2.edn"))
(def p3 (io/file "test-data/paths/child/1.txt"))
(def p4 (io/file "test-data/paths/child/2.edn"))

(def f1 (io/file p1))
(def f2 (io/file p2))
(def f3 (io/file p3))
(def f4 (io/file p4))

(deftest test-exists?
  (is (= p1 (dsk/exists? p1)))
  (is (= "test-data" (dsk/exists? "test-data")))
  (is (= nil (dsk/exists? "made-up/file.txt")))
  (is (= nil (dsk/exists? "made-up"))))

(deftest test-exists-file?
  (is (= p1 (dsk/exists-file? p1)))
  (is (= nil (dsk/exists-file? "test-data")))
  (is (= nil (dsk/exists-file? "made-up/file.txt")))
  (is (= nil (dsk/exists-file? "made-up"))))

(deftest test-exists-dir?
  (is (= nil (dsk/exists-dir? p1)))
  (is (= "test-data" (dsk/exists-dir? "test-data")))
  (is (= nil (dsk/exists-dir? "made-up/file.txt")))
  (is (= nil (dsk/exists-dir? "made-up"))))

(deftest test-paths
  (is (= [f1 f2 (io/file "test-data/paths/child")] (sort (dsk/paths "test-data/paths")))))

(deftest test-files
  (is (= [f1 f2] (sort (dsk/files "test-data/paths")))))

(deftest test-files-recursive
  (is (= [f1 f2 f3 f4] (sort (dsk/files-recursive "test-data/paths")))))

(deftest test-copy-&-delete
  (dsk/copy-recursive "test-data/paths/" "test-data/new1/")
  (is (= 4 (count (dsk/files-recursive "test-data/new1/"))))
  (dsk/delete "test-data/new1")
  (is (= nil (dsk/exists-dir? "test-data/new1/"))))

(deftest test-copy-file-children-&-delete
  (dsk/copy-file-children "test-data/paths/" "test-data/new2/")
  (is (= 2 (count (dsk/files-recursive "test-data/new2"))))
  (dsk/delete "test-data/new2")
  (is (= nil (dsk/exists-dir? "test-data/new2"))))

(deftest test-has-ext?
  (is (= p1 (dsk/has-ext? p1 ["txt" "doc" "docx"])))
  (is (= nil (dsk/has-ext? p2 ["txt" "doc" "docx"]))))

(deftest test-filter-exts
  (is (= [p1] (dsk/filter-exts [p1 "test-data/paths/1.edn"] ["txt" "doc" "docx"]))))

(deftest test-slurp-pun
  (is (= nil (dsk/slurp-pun nil)))
  (is (= nil (dsk/slurp-pun false)))
  (is (= "" (dsk/slurp-pun p1))))

(deftest test-filename
  (is (= "file.name" (dsk/filename "some/crazy/file.name.edn"))))

(deftest test-extension
  (is (= "edn" (dsk/extension "some/crazy/file.name.edn"))))
