(ns clj-performance-tester.core
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :refer [run-jetty]]
            [muuntaja.core :as m]
            [reitit.ring.coercion :as rrc]
            [reitit.coercion.malli :as coe]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [ring.util.response :as resp]
            [nrepl.server :refer [start-server stop-server]]
            [clj-async-profiler.core :as prof])
)

(def global-q (atom []))
(def hit-count (atom 0))
(defn empty-queue! [q]
  (let [curTime (java.time.Instant/now)
        dequeueTime (.toEpochMilli (.minusSeconds curTime 5))] ; Make 60 configurable seconds
    (loop [vals q
           dequeueCount 0]                                           ; Note: swap! passes value of atom, not reference to atom
      (if (= (count vals) 0)
        []
        (if (< (:time (first vals)) dequeueTime)
          (recur (vec (rest vals)) (inc dequeueCount))
          vals)))))

(defn fill-global-queue [n]
  (reset! global-q [])
  (dotimes [i n]
    (swap! global-q conj {:time (.toEpochMilli (.minusSeconds (java.time.Instant/now) 15)) :body {:a i}})
))

(comment ""
   (fill-global-queue 1)
   (fill-global-queue 100)
   (fill-global-queue 1000)
   (fill-global-queue 10000)
   (fill-global-queue 100000)
   (fill-global-queue 200000)

   (prof/profile (empty-queue! @global-q))
   (prof/profile (dotimes [i 100] (empty-queue! @global-q)))
   (prof/profile (dotimes [i 5000000] (rest @global-q)))
   (prof/serve-ui 8080)
  .)

(defn get-status [{:keys [] :as request}]
  (prn "GET Status" (count @global-q))
  (swap! global-q empty-queue!)
  (resp/response (str (count @global-q)))
)

(defn time-in-queue [{:keys [parameters] :as request}]
  (swap! global-q empty-queue!)
  (swap! global-q conj {:time (.toEpochMilli (java.time.Instant/now)) :message parameters})
  (swap! hit-count inc)
  (resp/response "time in queue will be 60")
)

(defn routes []
  ["" [
       ["/status" {:get {:handler get-status}}]
       ["/json/:time-in-queue" {:post {:handler    time-in-queue
                                       :coercion   coe/coercion
                                       :parameters {:path [:map [:time-in-queue pos-int?]]}}}]]]
)

(def handler
  (ring/ring-handler
    (ring/router ["" [(routes)]]
                 {:data {:muuntaja m/instance
                         :middleware [muuntaja/format-middleware
                                      rrc/coerce-request-middleware
                                      rrc/coerce-response-middleware]}})
    (ring/routes
      (ring/create-default-handler {:not-found {:status 404 :body "Not found."}}))))

(def server-inst (atom {}))
(defn -main []
  (defonce server (start-server :port 8888))
  (reset! server-inst (run-jetty #'handler {:port 8889, :join? false}))
  (.start @server-inst)
  )
