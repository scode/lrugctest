(ns org.scode.lrugctest
  (:gen-class)
  (:require [clojure.contrib.duck-streams :as duck-streams]
            [org.scode.plru :as plru])
  (:use compojure.core
        compojure.response
        ring.adapter.jetty))

(def SIZE 1000)
(def FACTOR 2.0)
(def CHUNKSIZE 100)
(def DISPLAYINTERVAL 1000)
(def SLEEPTIME 10)

(def global-cache (ref (plru/make-lru SIZE)))

(defn put-chunk
  [lru-ref chunk-size]
  (loop [count 0]
    (if (< count chunk-size)
      (do
        (dosync (alter lru-ref #(plru/lru-put %1 (rand (* SIZE FACTOR)) (str "val" (rem count 1000)))))
        (recur (+ 1 count))))))

(defn main-loop
  []
  (loop [so-far 0
         last-display (. System currentTimeMillis)]
    (let [now (. System currentTimeMillis)
          new-last-display (if (> (- now last-display) DISPLAYINTERVAL)
                             (let [lru-snap @global-cache]
                               (println (str (:size lru-snap)
                                             " entries, "
                                             (* 100.0 (/ (:size lru-snap) (:max-size lru-snap)))
                                             "% full"))
                               now)
                           last-display)]
      (if (> SLEEPTIME 0)
        (. Thread sleep SLEEPTIME))
      (let [start-time (. System currentTimeMillis)]
        (put-chunk global-cache CHUNKSIZE)
        (let [elapsed (- (. System currentTimeMillis) start-time)]
          (recur (+ so-far CHUNKSIZE)
              (long new-last-display)))))))

(defroutes http-routes
  (GET "/set-size" [& args]
    (let [target-size (Long/parseLong (args "size"))]
      ;; Don't resize in one go since it can be a potentially very expensive operation, and we don't
      ;; want to do that within a dosync txn. Instead, do a txn per decrement. (This exploits the fact that
      ;; we know that re-sizing the lru upwards is O(1), but resizing downwards is O(n) with respect to the number
      ;; of dropped entries).
      (loop []
        (let [cur-size (:size @global-cache)]
          (if (> cur-size target-size)
            (do
              (dosync (alter global-cache #(plru/lru-resize %1 (dec cur-size))))
              (recur))
            (do
              (dosync (alter global-cache #(plru/lru-resize %1 target-size)))
              "resized")))))))

(defn -main [& args]
  (.start (Thread. main-loop))
  (run-jetty http-routes {:port 9191}))



