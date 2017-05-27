(ns me.rossputin.diskops
  (:require [clojure.java.io :refer [as-relative-path copy delete-file file]]
            [clojure.string :refer [join split]])
  (:refer-clojure :exclude [name])
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

(defn file? [p] (when (.isFile (file p)) p))

(defn dir? [p] (when (.isDirectory (file p)) p))

(defn parent [f] (.getParent f))

(defn parentFile [f] (.getParentFile f))

(defn path [f] (.getPath f))

(defn name [f] (.getName f))

(defn parentPath [f] (path (parentFile f)))

(defn paths [p] (.listFiles (file p)))

(defn files [p] (filter #(file? %) (paths p)))

(defn files-recursive [src] (remove #(dir? %) (file-seq (file src))))

(defn directories [p] (filter #(dir? %) (paths p)))

(defn exists? [p] (when (.exists (file p)) p))

(defn exists-dir? [p] (when (and (exists? p) (dir? p)) p))

(defn file-with-path
  "Look in a directory structure 'data-path' for a file 'f-name' with given ext"
  [data-path f-name ext]
  (let [fs (file-seq (file data-path))]
    (first (filter #(= (name %) (str f-name ext)) fs))))

(defn mkdir [f] (.mkdir f))

(defn mkdirs [f] (.mkdirs f))

(defn copy-recursive [src dest]
  (let [parent (parent (file src))
        idx (if (nil? parent) 0 (.length parent))]
    (doseq [f (files-recursive src)]
      (let [dest-file (file (str dest (fs) (subs (path f) idx)))]
        (mkdirs (parentFile dest-file))
        (copy f dest-file)))))

(defn copy-file-children [src dest] (shallow-copy (files src) dest))

(defn delete-directory [d]
  (doseq [f (reverse (file-seq (file d)))] (delete-file f)))

(defn has-ext? [file exts]
  (let [ext-pattern (join "|" exts)
        complete-pattern (str "^.+\\.(" ext-pattern ")$")
        exts-reg-exp (re-pattern complete-pattern)]
    (when (re-find exts-reg-exp (name file)) file)))

(defn filter-exts [files exts] (filter #(has-ext? % exts) files))

(defn delete-exts [path exts]
  (let [fs (filter-exts (file-seq (file path)) exts)]
    (doseq [f fs] (delete-file f))))

(defn slurp-pun [x] (if x (slurp x) nil))

(defn as-relative [path] (try (as-relative-path path) (catch Exception e nil)))

(defn filename [f] (if f (first (split (name f) #"\.")) nil))

(defn extension [f] (if f (last (split (name f) #"\.")) nil))
