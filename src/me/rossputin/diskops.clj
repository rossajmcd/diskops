(ns me.rossputin.diskops
  (:require [clojure.java.io :refer [copy delete-file file]])
  (import java.io.File))

;; =============================================================================
;; Helper functions
;; =============================================================================

(defn perform-copy
  [f-seq dest]
  (doseq [f f-seq]
    (let [dest-file (file dest f)]
      (.mkdirs (.getParentFile dest-file))
      (copy f dest-file))))

(defn perform-shallow-copy
  [f-seq dest]
  (doseq [f f-seq] (copy f (file dest (.getName f)))))


;; =============================================================================
;; Disk based IO operations
;; =============================================================================

(defn path-list
  [p]
  (.listFiles (file p)))

(defn recursive-list
  [src]
  (remove #(.isDirectory %) (file-seq (file src))))

(defn file?
  [p]
  (.isFile (file p)))

(defn dir?
  [p]
  (.isDirectory (file p)))

(defn files-list
  [p]
  (filter #(file? %) (path-list p)))

(defn exists?
  [p]
  (.exists (file p)))

(defn exists-dir?
  [p]
  (and (exists? p) (dir? p)))

(defn copy-recursive
  [src dest]
  (perform-copy (recursive-list src) dest))

(defn copy-file-children
  [src dest]
  (perform-shallow-copy (files-list src) dest))

(defn delete-directory
  [d]
  (doseq [f (reverse (file-seq (file d)))] (delete-file f)))

(defn has-ext? [file exts]
  (let [ext-pattern (clojure.string/join "|" exts)
        complete-pattern (str "^.+\\.(" ext-pattern ")$")
        exts-reg-exp (re-pattern complete-pattern)]
    (if (re-find exts-reg-exp (.getName file))
      true
      false)))

(defn filter-exts [files exts]
  (filter #(has-ext? % exts) files))
