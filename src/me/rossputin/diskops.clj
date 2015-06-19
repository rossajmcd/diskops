(ns me.rossputin.diskops
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))
;; =============================================================================
;; Helper functions
;; =============================================================================

(defn- p->f [p] (if (instance? java.io.File p) p (io/file (str p))))


;; =============================================================================
;; Disk based IO operations
;; =============================================================================

(defn fs [] (java.io.File/separator))

(defn pwd [] (. (io/file ".") getCanonicalPath))

(defn exists? [p] (when (.exists (p->f p)) p))

(defn exists-file? [p] (when (.isFile (p->f p)) p))

(defn exists-dir? [p] (when (.isDirectory (p->f p)) p))

(defn paths [p] (.listFiles (p->f p)))

(defn files [p] (filter #(exists-file? %) (paths p)))

(defn files-recursive [src] (remove #(exists-dir? %) (file-seq (p->f src))))

(defn copy-recursive [src dest]
  (let [parent (.getParent (p->f src))
        idx (if (nil? parent) 0 (.length parent))]
    (doseq [f (files-recursive src)]
      (let [dest-file (p->f (str dest (fs) (subs (.getPath f) idx)))]
        (.mkdirs (.getParentFile dest-file))
        (io/copy f dest-file)))))

(defn copy-file-children [src dest]
  (doseq [f (files src)]
    (let [dest-file (p->f (str dest (fs) (.getName f)))]
      (.mkdirs (.getParentFile dest-file))
      (io/copy f dest-file))))

(defn delete [p]
  (if (exists-file? p)
    (io/delete-file p)
    (doseq [p (reverse (file-seq (p->f p)))]
      (io/delete-file p))))

(defn has-ext? [p exts]
  (let [file (p->f p)
        ext-pattern (s/join "|" exts)
        complete-pattern (str "^.+\\.(" ext-pattern ")$")
        exts-reg-exp (re-pattern complete-pattern)]
    (when (re-find exts-reg-exp (.getName file)) p)))

(defn filter-exts [files exts] (filter #(has-ext? % exts) files))

(defn slurp-pun [p] (if p (slurp p) nil))

(defn as-relative [p] (try (io/as-relative-path p) (catch Exception e nil)))

(defn filename [p]
  (when p (s/join "." (drop-last (s/split (.getName (p->f p)) #"\.")))))

(defn extension [p] (when p (last (s/split (.getName (p->f p)) #"\."))))
