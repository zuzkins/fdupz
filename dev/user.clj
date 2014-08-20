(ns user
  (:require [cemerick.austin.repls :refer [browser-repl-env browser-connected-repl-js cljs-repl]]
            [cemerick.austin :as austin]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [compojure.core :refer (GET ANY POST defroutes)]
            [compojure.route :as route]
            [ring.adapter.jetty :refer (run-jetty)]
            [ring.middleware.resource :refer [wrap-resource]]
            [clojure.java.io :as io]))

(def repl (atom nil))

(defn repl-start []
  (reset! repl (reset! cemerick.austin.repls/browser-repl-env (cemerick.austin/repl-env)))
  (spit "assets/dev/browser_repl.js" (browser-connected-repl-js))
  (cljs-repl @repl))

(defroutes app
  (route/resources "/" :root "/")
  (ANY "*" req (fn [h]
                 {:body "Ahoj" :status 200})))

(def html (-> "assets/index.html"
            io/resource
            slurp))

(defroutes empty-routes
  (GET "/assets/app"
    request
    (.replace html "//repl" (browser-connected-repl-js)))
  )

(def all-resources (-> empty-routes (wrap-resource "/")))

(def system
  "A Var containing an object representing the application under
  development."
  nil)

(defn init
  "Creates and initializes the system under development in the Var
  #'system."
  []
  (alter-var-root
   #'system
   (fn [system]
     (if-not (:server system)
       {:server (run-jetty #'all-resources {:port 3000 :join? false})
        :repl-env (reset! browser-repl-env (cemerick.austin/repl-env :ip "0.0.0.0"
                                                                     :port 9001))}
       (do (.start (:server system)) system)))))

(defn start
  "Starts the system running, updates the Var #'system."
  []
  (cemerick.austin.repls/cljs-repl (:repl-env system) :optimizations :none))

(defn stop
  "Stops the system if it is currently running, updates the Var
  #'system."
  []
  (when (try (.stop (:server system))
             (catch Throwable e false))
    true))

(defn go
  "Initializes and starts the system running."
  []
  (init)
  (start)
  :ready)

(defn reset
  "Stops the system, reloads modified source files, and restarts it."
  []
  (stop)
  (refresh-all :after 'user/go))
