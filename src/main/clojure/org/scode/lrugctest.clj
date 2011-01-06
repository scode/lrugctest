(ns org.scode.lrugctest
  (:gen-class)
  (:require [clojure.contrib.duck-streams :as duck-streams]
            [org.scode.plru :as plru])
  (:use compojure.core
        compojure.response
        ring.adapter.jetty))

(def SIZE 1000)
(def FACTOR 2.0)
(def MAXPAUSE 10000)
(def CHUNKSIZE 1000)
(def DISPLAYINTERVAL 1000)
(def SLEEPTIME 10)

(defn put-chunk
  [c]
  (loop [c c
         count 0]
    (if (< count CHUNKSIZE)
      (do
        (recur (plru/lru-put c (int (rand (* SIZE FACTOR))) (str "val" (rem count 1000)))
               (+ 1 count)))
      c)))

(defn main-loop
  []
  (loop [so-far 0
         c (plru/make-lru SIZE)
         last-display (. System currentTimeMillis)]
    (let [now (. System currentTimeMillis)
          new-last-display (if (> (- now last-display) DISPLAYINTERVAL)
                             (do
                               (println "PUTS: " so-far)
                               now)
                           last-display)]
      ;(. Thread sleep SLEEPTIME)
      (let [start-time (. System currentTimeMillis)]
        (let [new-c (put-chunk c)]
          (let [elapsed (- (. System currentTimeMillis) start-time)]
            (if (> elapsed MAXPAUSE)
              (println "PAUSE <=: " elapsed "ms"))
            (recur (+ so-far CHUNKSIZE)
              new-c
              (long new-last-display))))))))

(defn -main [& args]
  (main-loop))



