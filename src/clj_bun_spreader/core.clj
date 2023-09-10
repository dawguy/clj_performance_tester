(ns clj-bun-spreader.core
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :refer [run-jetty]]
            [muuntaja.core :as m]
            [reitit.ring.coercion :as rrc]
            [reitit.coercion.malli :as coe]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [ring.util.response :as resp]
            [nrepl.server :refer [start-server stop-server]])
)

(def global-q (atom []))
(def hit-count (atom 0))
(defn empty-queue! [q]
  (let [curTime (java.time.Instant/now)
        dequeueTime (.minusSeconds curTime 15)]              ; Make 60 configurable seconds
    (loop [vals q
           dequeueCount 0]                                           ; Note: swap! passes value of atom, not reference to atom
      (if (= (count vals) 0)
        []
        (if (= 1000 dequeueCount)
          (do (prn "returning early") vals)
          (if (> 0 (.compareTo (:time (first vals)) dequeueTime))
            (recur (rest vals) (inc dequeueCount))
            vals))))))

(comment ""
  (reset! global-q [
   (.minusSeconds (java.time.Instant/now) 80)
   (.minusSeconds (java.time.Instant/now) 20)
   (java.time.Instant/now)])

   (empty-queue! @global-q)
   (swap! global-q empty-queue!)
   (> 0 (.compareTo (:time (first @global-q)) (java.time.Instant/now)))
  .)

(defn get-status [{:keys [] :as request}]
  (prn "GET Status" (count @global-q))
  (swap! global-q empty-queue!)
  (resp/response (str (count @global-q)))
)

(defn time-in-queue [{:keys [parameters] :as request}]
  (swap! global-q empty-queue!)
  (swap! global-q conj {:time (java.time.Instant/now) :message parameters})
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
  (reset! server-inst (run-jetty #'handler {:port 8889, :join? false}))
  (.start @server-inst)
  (defonce server (start-server :port 8888))
  )