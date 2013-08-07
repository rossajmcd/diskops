(ns me.rossputin.diskops
  (:require [clojure.java.io :refer [copy delete-file file]])
  (import java.io.File))

(defn delete-directory
  [d]
  (doseq [f (reverse (file-seq (file d)))] (delete-file f)))

(defn recursive-copy-input
  [src]
  (remove #(.isDirectory %) (file-seq (file src))))

(defn perform-copy
  [f-seq dest]
  (doseq [f f-seq]
    (let [dest-file (file dest f)]
      (.mkdirs (.getParentFile dest-file))
      (copy f dest-file))))

(defn copy-recursive
  [src dest]
  (perform-copy (recursive-copy-input src) dest))

(defn perform-shallow-copy
  [f-seq dest]
  (doseq [f f-seq] (copy f (file dest (.getName f)))))

(defn copy-recursive-children
  [src dest]
  (let [c-set (rest (recursive-copy-input src))]
    (perform-shallow-copy c-set dest)))
