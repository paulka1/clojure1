(ns tictactoe.core
    (:require 
              [reagent.core :as reagent :refer [atom]]
              [reagent.dom :as rd]))

(enable-console-print!)

(defn make-morpion []
  (vec (take 3 (repeat 0))))

(defn new-board [n]
  (vec (repeat n (vec (repeat n 0)))))

(defonce app-state (atom {:text "TIC TAC TOE"
                          :board (new-board 3)}))

;(defn computer-move []
;      (swap! app-state assoc-in [:board @app-state]
;             remaining-spots (for [i (range board-size)
;                                   j (range board-size)
;                                   :when (= (get-in board [j i])2)]
;                                  [i j])
;                                  move (rand-nth remaining-spots)
;                                  path (into [:board] (reverse move))s
;                                  (swap! app-state assoc-in path 2)))

(def header-links
  [:div#header-links
   [:a {:href "/"} "1vs1"]
   " | "
   [:a {:href "/game_machine"} "1vsMachine"]
   ])

(defn blank [i j]
      [:rect
       {:width 0.9
        :height 0.9
        :fill "grey"
        :x (+ 0.05 i)
        :y (+ 0.05 j)
        :on-click
        (fn rect-click [e]
            (swap! app-state update-in [:board j i]inc)
            ;(computer-move)
            )}])

(defn circle [i j]
      [:circle
       {:r 0.45
        :fill "green"
        :cx (+ 0.5 i)
        :cy (+ 0.5 j)
        }])

(defn cross [i j]
      [:g {:stroke "darkred"
           :stroke-width 0.2
           :strokelincap "round"
           :transform
           (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ")"
                "scale(0.35)")}
       [:line {:x1 -1 :y1 -1 :x2 1 :y2 1}]
       [:line {:x1 1 :y1 -1 :x2 -1 :y2 1}]])

(defn tictactoe []
      [:div#main
       header-links
       [:h1#title (:text @app-state)]
       (into
       [:svg
        {:view-box "0 0 3 3"
         :width 500
         :height 500}]
        (for [i (range (count (:board @app-state)))
              j (range (count (:board @app-state)))]
             (case (get-in @app-state [:board j i])
                   0 [blank i j]
                   1 [circle i j]
                   2 [cross i j])))
       [:p
        [:button#newGame
         {
          :on-click
          (fn new-game-click [e]
              (swap! app-state assoc :board (new-board 3)))
          } "NEW GAME"]]])

(rd/render [tictactoe]
           (. js/document (getElementById "app")))

(defn on-js-reload []
   (swap! app-state assoc-in [:text] "TIC TAC TOE")
)
