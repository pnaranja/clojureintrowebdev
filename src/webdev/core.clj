(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            ))

(defn greet' "Simple Handler" [req]
  (case (:uri req)
    "/"             {:status 200 :body "<h1>Greetings everybody!</h1>" :headers {}}
    "/goodbye"      {:status 200 :body "<h1>Goodbye Cruel World!</h1>" :headers {}} 
    {:status 404 :body "<h1>*** Incorrect address!!! ***</h1>" :hearder {}})
  )

(defn greet [req] {:status 200 :body "<h1>Greetings everybody!</h1>" :headers {}})
(defn goodbye [req] {:status 200 :body "<h1>Goodbye Cruel World!</h1>" :headers {}})

(defroutes app
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (not-found "<h1>*** Incorrect address!!! ***</h1>)")
  )

(defn -main [port]
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)})
  )
