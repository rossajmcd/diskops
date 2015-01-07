(ns me.rossputin.diskops
  (:require [clojure.java.io :refer [copy delete-file file]])
  (:import java.io.File))

;; =============================================================================
;; Helper functions
;; =============================================================================

(defn- shallow-copy [f-seq dest]
  (doseq [f f-seq] (copy f (file dest (.getName f)))))


;; =============================================================================
;; Disk based IO operations
;; =============================================================================

(defn fs [] (File/separator))

(defn pwd [] (. (file ".") getCanonicalPath))

(defn path-list [p] (.listFiles (file p)))

(defn recursive-list [src] (remove #(.isDirectory %) (file-seq (file src))))

(defn file? [p] (.isFile (file p)))

(defn dir? [p] (.isDirectory (file p)))

(defn files-list [p] (filter #(file? %) (path-list p)))

(defn exists? [p] (.exists (file p)))

(defn exists-dir? [p] (and (exists? p) (dir? p)))

(defn copy-recursive [src dest]
  (let [parent (.getParent (file src))
        idx (if (nil? parent) 0 (.length parent))]
    (doseq [f (recursive-list src)]
      (let [dest-file (file (str dest (fs) (subs (.getPath f) idx)))]
        (.mkdirs (.getParentFile dest-file))
        (copy f dest-file)))))

(defn copy-file-children [src dest] (shallow-copy (files-list src) dest))

(defn delete-directory [d]
  (doseq [f (reverse (file-seq (file d)))] (delete-file f)))

(defn has-ext? [file exts]
  (let [ext-pattern (clojure.string/join "|" exts)
        complete-pattern (str "^.+\\.(" ext-pattern ")$")
        exts-reg-exp (re-pattern complete-pattern)]
    (if (re-find exts-reg-exp (.getName file))
      true
      false)))

(defn filter-exts [files exts] (filter #(has-ext? % exts) files))

(defn slurp-pun [x] (if x (slurp x) nil))
