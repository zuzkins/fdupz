(ns fdupz.ui.components
  (:require [om.core :as om]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as dom :include-macros true]

            [cljs.core.async :refer [<! >! put! chan close! alts!]]

            [fdupz.node.fs :as fs])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(def counter (atom 0))

(defn next-id []
  (swap! counter inc))

(defn scan-dir [path state]
  (om/update! state :files [])
  (om/update! state :total-size 0)
  (om/update! state :total-count 0)
  (let [ch (fs/walk path)]
    (go-loop [acc []]
      (let [item (<! ch)]
        (when item
          (if (= :file (:type item))
            (let [complete (assoc item :id (next-id))]
              (om/transact! state :total-count inc4)
              (om/transact! state :total-size
                (fn [s]
                  (+ s (or (-> item :stats .-size) 0))))
              (recur (conj acc complete)))
            (recur acc)))))))

(defcomponent directory-input [data owner]
  (init-state [_]
    {:path "/Users/zuzkins/Downloads"})
  (render-state [_ {:keys [scan-chan path]}]
    (dom/form
      {:on-submit (fn [e]
                    (.preventDefault e)
                    (when path (put! scan-chan path)))}
      (dom/input {:placeholder "Directory to scan"
                  :value path
                  :on-change (fn [e]
                               (let [v (-> e .-target .-value)]
                                 (om/set-state! owner :path v)))})
      (dom/button "Scan!"))))

(defcomponent scan-progress [data owner]
  (render-state [_ state]
    (dom/span (str "files found: " (:total-count data) ", total size: " (:total-size data) " bytes"))))

(defcomponent scan-view [data owner]
  (init-state [_]
    {:scan-chan (chan)
     :kill-chan (chan)})
  (render-state [_ {:keys [scan-chan]}]
    (condp = (:stage data)
      :scanning
      (om/build scan-progress data)

      ;;(dom/h1 "Default")
      (om/build directory-input data {:init-state {:scan-chan scan-chan}})
      ))

  (did-mount [_]
    (let [{:keys [scan-chan kill-chan]} (om/get-state owner)]
      (go-loop []
        (let [[v c] (alts! [scan-chan kill-chan])]
          (when (= c scan-chan)
            (scan-dir v data)
            (om/update! data :inspect-path v)
            (om/update! data :stage :scanning))))))
  (did-unmount [_]
    (put! (om/get-state owner :kill-chan) :die)))
