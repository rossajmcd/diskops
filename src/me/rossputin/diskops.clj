(ns me.rossputin.diskops
  (:require [clojure.java.io :refer [copy delete-file file]])
  (import java.io.File))

(defn delete-directory
  [d]
  (doseq [f (reverse (file-seq (file d)))] (delete-file f)))

(defn copy-recursive
  [src dest]
  (doseq [f (remove #(.isDirectory %) (file-seq (file src)))]
    (let [dest-file (file dest f)]
      (.mkdirs (.getParentFile dest-file))
      (copy f dest-file))))
