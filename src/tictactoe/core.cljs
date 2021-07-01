(ns tictactoe.core
    (:require 
              [reagent.core :as reagent :refer [atom]]
              [reagent.dom :as rd]))

(enable-console-print!)

(println "This text is printed from src/tictactoe/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defn make-morpion []
  (vec (take 3 (repeat 0))))

(defonce app-state (atom {:text "TIC TAC TOE!"
                          :board (make-morpion)}))


(defn tictactoe []
      [:div
       [:h1 (:text @app-state)]
         [:svg
          {:view-box "0 0 30 30"
           :width 500
           :height 500}
          (for [i (range (count (:board @app-state)))
                j (range (count (:board @app-state)))]
            [:rect {:width 0.4
                    :height 0.4
                    :x i :y j}])]])

(rd/render [tictactoe]
           (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
   (swap! app-state assoc-in [:text] "TIC TAC TOE!")
)
