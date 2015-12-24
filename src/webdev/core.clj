(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]

            [webdev.item.model :as items]
            [webdev.item.handler :as handler]
            ))

(def db "jdbc:postgresql://localhost/webdev?user=postgres")

;Handlers!
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

(defroutes routes
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (GET "/about" [] about)
  (GET "/request" [] handle-dump)
  (GET "/yo/:name" [] yo)
  (GET "/calc/:num1/:op/:num2" [] calc)
  (GET "/item" [] handler/handle-index-items)
  (not-found "<h1>*** Incorrect address!!! ***</h1>")
  )

(defn wrap-db 
  "Adds the db to the req map.  The assoc function adds key-val pair to a map"
  [hdlr]
  (fn [req]
   (hdlr (assoc req :webdev/db db))))

(defn wrap-server-header 
  "Adds Server header to the response.  Therefore it's adding something after the request has been handled.
   Header is the {'HeaderName' 'ServerName'}.  The assoc-in adds to the response, 
   a keyword map :headers to a Header -> {:headers {'HeaderName' 'ServerName'}}"
  [hdlr]
  (fn [req]
    (assoc-in (hdlr req) [:headers "Server"] "PaulsServer")))

(def app "Middleware!"
  (wrap-server-header (wrap-db (wrap-params routes))))

(defn -main [port]
  (items/create-table db)
  (jetty/run-jetty app {:port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table db)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)})
  )
