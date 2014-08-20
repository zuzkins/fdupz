(ns fdupz.core
  (:require [om.core       :as om]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom  :as dom :include-macros true]
            [clojure.browser.repl :as repl]

            [fdupz.ui.components :as comps]))

(enable-console-print!)

(def app-state (atom {}))



(defcomponent fdupz-app [app owner]
  (render [_]
    (om/build comps/scan-view app)))

(om/root fdupz-app app-state {:target (.getElementById js/document "container")})
