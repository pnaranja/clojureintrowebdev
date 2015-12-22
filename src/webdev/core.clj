(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]
            ))

(defn greet' "Simple Handler" [req]
  (case (:uri req)
    "/"             {:status 200 :body "<h1>Greetings everybody!</h1>" :headers {}}
    "/goodbye"      {:status 200 :body "<h1>Goodbye Cruel World!</h1>" :headers {}} 
    {:status 404 :body "<h1>*** Incorrect address!!! ***</h1>" :hearder {}})
  )

(defn greet [req] {:status 200 :body "<h1>Greetings everybody!</h1>" :headers {}})
(defn goodbye [req] {:status 200 :body "<h1>Goodbye Cruel World!</h1>" :headers {}})
(defn about [req] {:status 200 :body "<h1>My name is Paul. This is a sample page</h1>" :headers {}})
(defn request [req] {:status 200 :body (.toString req) :headers {}}) ; Not used.  Replaced by handle-dump

(defn yo' [req] {:status 200 :body (str "Yo!, " (:name (:route-params req)) "!") :headers {}})
(defn yo [req] (let [name (get-in req [:route-params :name])] {:status 200 :body (str "Yo! " name "!") :headers {}}))

(def ops {"+" +, "-" -, "*" *, ":" /})
(defn calc "Does arithmetic on the 3 addresses after 'calc'. Convert the two numbers into Integers and then run the operation on them"
  [req] (let [num1 (read-string (get-in req [:route-params :num1]))
              num2 (read-string (get-in req [:route-params :num2])) 
              op  (ops (get-in req [:route-params :op]))] 
        (if op
           {:status 200 :headers {} :body (str (op num1 num2))}  
           {:status 404 :headers {} :body "<h1>Unknown Operation!</h1>"})))

(defroutes app
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (GET "/about" [] about)
  (GET "/request" [] handle-dump)
  (GET "/yo/:name" [] yo)
  (GET "/calc/:num1/:op/:num2" [] calc)
  (not-found "<h1>*** Incorrect address!!! ***</h1>")
  )

(defn -main [port]
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)})
  )
