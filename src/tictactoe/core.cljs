(ns tictactoe.core
    (:require 
              [reagent.core :as reagent :refer [atom]]
              [reagent.dom :as rd]))

(enable-console-print!)

(println "This text is printed from src/tictactoe/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "TIC TAC TOE!"}))


(defn tictactoe []
      [:center
       [:h1 (:text @app-state)]
       (into
         [:svg
          {:view-box (str "0 0 " board-size " " board-size)
           :width 500
           :height 500}]
         )])

(rd/render [tictactoe]
           (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
   (swap! app-state assoc-in [:text] "HI")
)
