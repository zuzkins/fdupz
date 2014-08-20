(defproject fdupz "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2311"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [om "0.7.1"]
                 [prismatic/om-tools "0.3.0"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]

  :source-paths ["src"]
  :profiles {:dev
             {:source-paths ["dev" "./"]
              :dependencies    [[ring "1.3.0"]
                                [compojure "1.1.8"]
                                [org.clojure/tools.namespace "0.2.5"]]
              :plugins         [[com.cemerick/austin "0.1.4"]]}}

  :cljsbuild {
    :builds [{:id "fdupz"
              :source-paths ["src"]
              :compiler {
                :output-to "fdupz.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
