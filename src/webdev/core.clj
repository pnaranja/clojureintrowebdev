(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            ))

(defn greet "Simple Handler" [req]
  (if (= "/" (:uri req))
    {:status 200
     :body "<h1>Greetings everybody!</h1>"
     :headers {}}
    {:status 404
     :body "<h1>*** Incorrect address!!! ***</h1>"
     :hearder {}}
    )
  )

(defn -main [port]
  (jetty/run-jetty greet {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'greet) {:port (Integer. port)})
  )
