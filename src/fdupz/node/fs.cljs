(ns fdupz.node.fs
  (:require [cljs.core.async :refer [put! <! >! chan close! alts!]])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def node-fs (js/require "fs"))

(def node-walk (js/require "walk"))

(defn walk [what]
  (let [w     (.walk node-walk what #js {:followLinks false})
        out   (chan 500)]
    (.on w "file"
      (fn [root stats go-next]
        (when stats
          (go
            (>! out {:type :file
                       :path (str root "/" (aget stats "name"))
                       :name (aget stats "name")
                       :size (aget stats "size")})))
        (go-next)))
    (.on w "directory"
      (fn [root stats go-next]
        (when stats
          (go
            (>! out {:type :dir
                     :path (str root "/" (aget stats "name"))
                     :name (aget stats "name")})))
        (go-next)))
    (.on w "error"
      (fn [root errors go-next]
        (put! out {:type :err :err errors})
        (go-next)))
    (.on w "end"
      (fn []
        (close! out)))
    out))
