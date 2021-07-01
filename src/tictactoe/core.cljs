(ns tictactoe.core
    (:require 
              [reagent.core :as reagent :refer [atom]]
              [reagent.dom :as rd]))

(enable-console-print!)

(println "This text is printed from src/tictactoe/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defn make-morpion []
  (vec (take 3 (repeat 0))))

(defn new-board [n]
  (vec (repeat n (vec (repeat n 0)))))

(defonce app-state (atom {:text "TIC TAC TOE!"
                          :board (new-board 3)}))


(defn tictactoe []
      [:div
       [:h1 (:text @app-state)]
       (into
       [:svg
        {:view-box "0 0 3 3"
         :width 500
         :height 500}
        (for [i (range (count (:board @app-state)))
              j (range (count (:board @app-state)))]
          [:rect {:width 0.9
                  :height 0.9
                  :fill (if (zero? (get-in @app-state [:board j i]))
                          "red"
                          "blue")
                  :x i
                  :y j
                  :on-click
                  (fn rect-click [e]
                    (prn "you clicked me!" i j)
                    (prn (:board @app-state))
                    (prn (swap! app-state assoc-in [:board j i] 1 )))
                    }])])])

(rd/render [tictactoe]
           (. js/document (getElementById "app")))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
   (swap! app-state assoc-in [:text] "TIC TAC TOE!")
)
