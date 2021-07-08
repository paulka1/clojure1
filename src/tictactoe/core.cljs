(ns tictactoe.core
  (:require
    [reagent.core :as reagent :refer [atom]]
    [reagent.dom :as rd]))

(enable-console-print!)

(defn make-morpion []
      (vec (take 9 (repeat 0))))

;; ; -------------------------------------------
;; ; Renvoie le symbole present dans  
;; ; la case (x,y) du plateau
;; ; x et y sont donnes en coordonnees de 0 a 2
(defn get-case-morpion [plateau i j]
      (get plateau (+ i (* 3 j))))

; -------------------------------------------
; Affiche le plateau

(defn set-case-morpion [plateau player i j]
      (assoc plateau (+ i (* 3 j)) player))

(defn clean-case-morpion [plateau i j]
      (assoc plateau (+ i (* 3 j)) 0))

(defn exchange [player]
      (- 3 player))

(defn inc1 [num]
      (+ num 1))

(defn winning-morpion? [plateau]
      (defn test-win [a b c] (and (not= a 0) (= a b) (= a c)))
      (or (test-win (get-case-morpion plateau 0 0) (get-case-morpion plateau 0 1) (get-case-morpion plateau 0 2))
          (test-win (get-case-morpion plateau 1 0) (get-case-morpion plateau 1 1) (get-case-morpion plateau 1 2))
          (test-win (get-case-morpion plateau 2 0) (get-case-morpion plateau 2 1) (get-case-morpion plateau 2 2))

          (test-win (get-case-morpion plateau 0 0) (get-case-morpion plateau 1 0) (get-case-morpion plateau 2 0))
          (test-win (get-case-morpion plateau 0 1) (get-case-morpion plateau 1 1) (get-case-morpion plateau 2 1))
          (test-win (get-case-morpion plateau 0 2) (get-case-morpion plateau 1 2) (get-case-morpion plateau 2 2))

          (test-win (get-case-morpion plateau 0 0) (get-case-morpion plateau 1 1) (get-case-morpion plateau 2 2))
          (test-win (get-case-morpion plateau 2 0) (get-case-morpion plateau 1 1) (get-case-morpion plateau 0 2))))

(defn legal-move-morpion? [plateau i j]
      (and (>= i 0)
           (<= i 2)
           (>= j 0)
           (<= j 2)
           (= (get-case-morpion plateau i j) 0)))

(defn end-morpion? [plateau]
      (empty? (filter (fn [x] (zero? x)) plateau)))


(defonce app-state (atom {:text          "TIC TAC TOE"
                          :board         (make-morpion)
                          :player        1
                          :win           false
                          :computer      false
                          :coup          0
                          :scorePlayer1  0
                          :scorePlayer2  0
                          :scoreComputer 0
                          :bestScore     -9999
                          :player1Name   "player 1"
                          :x             -1
                          :y             -1}))

(def header-links
  [:div#header-links
   [:a#a {:on-click
          (fn [b] {
                   (swap! app-state assoc-in [:computer] false)
                   (swap! app-state assoc-in [:win] false)
                   (swap! app-state assoc-in [:text] (str "1 vs 1"))
                   (swap! app-state assoc :board (make-morpion))
                   })
          } "1vs1"]
   "  |  "
   [:a#a {:on-click
          (fn [b] {
                   (swap! app-state assoc-in [:computer] true)
                   (swap! app-state assoc-in [:win] false)
                   (swap! app-state assoc-in [:text] (str "Vs Machine"))
                   (swap! app-state assoc :board (make-morpion))
                   (swap! app-state assoc-in [:bestScore] -9999)
                   (swap! app-state assoc-in [:player] 1)
                   })
          } "1vsMachine"]
   ])


(defn computer-move [board]
      (let [remaining-spots (for [i [0 1 2]
                                  j [0 1 2]
                                  :when (= (get-case-morpion board i j) 0)]
                                 [i j])
            move (when (seq remaining-spots)
                       (rand-nth remaining-spots))]
           (when (legal-move-morpion? board (get move 0) (get move 1))
                 (swap! app-state assoc-in [:board] (set-case-morpion (:board @app-state) 2 (get move 0) (get move 1))))))


(defn blank [i j]
      [:rect
       {:width  0.9
        :height 0.9
        :fill   "grey"
        :x      (+ 0.05 i)
        :y      (+ 0.05 j)
        :on-click
                (fn rect-click [e]
                    (when (false? (:win @app-state))

                          ;; if 1 vs 1
                          (when (false? (:computer @app-state))
                                (swap! app-state assoc-in [:board] (set-case-morpion (:board @app-state) (:player @app-state) i j))
                                ;; Victory
                                (when (winning-morpion? (:board @app-state))
                                      (swap! app-state assoc-in [:text] (str "Player " (:player @app-state) " win"))
                                      (swap! app-state assoc-in [:win] true)

                                      ;; Score
                                      (case (:player @app-state)
                                            1 (swap! app-state assoc-in [:scorePlayer1] (inc1 (:scorePlayer1 @app-state)))
                                            2 (swap! app-state assoc-in [:scorePlayer2] (inc1 (:scorePlayer2 @app-state)))))

                                ;; match Nul
                                (when (false? (:win @app-state))
                                      (when (end-morpion? (:board @app-state))
                                            (swap! app-state assoc-in [:text] (str "Match Nul"))
                                            (swap! app-state assoc-in [:win] true)))

                                (swap! app-state assoc-in [:player] (exchange (:player @app-state)))
                                )

                          ;; if Machine Game
                          (when (true? (:computer @app-state))
                                (swap! app-state assoc-in [:board] (set-case-morpion (:board @app-state) (:player @app-state) i j))
                                ;; Player Victory
                                (when (winning-morpion? (:board @app-state))
                                      (swap! app-state assoc-in [:text] (str "Player " (:player @app-state) " win"))
                                      (swap! app-state assoc-in [:win] true)
                                      (swap! app-state assoc-in [:scorePlayer1] (inc1 (:scorePlayer1 @app-state))))
                                (swap! app-state assoc-in [:player] (exchange 1))
                                (when (false? (winning-morpion? (:board @app-state)))
                                      (computer-move (:board @app-state))
                                      ;; Computer Victory
                                      (when (winning-morpion? (:board @app-state))
                                            (swap! app-state assoc-in [:text] (str "Computer win"))
                                            (swap! app-state assoc-in [:win] true)
                                            (swap! app-state assoc-in [:scoreComputer] (inc1 (:scoreComputer @app-state))))
                                      ;; Match Nul
                                      (when (false? (:win @app-state))
                                            (when (end-morpion? (:board @app-state))
                                                  (swap! app-state assoc-in [:text] (str "Match Nul"))
                                                  (swap! app-state assoc-in [:win] true)))

                                      (swap! app-state assoc-in [:player] (exchange 2))
                                      )

                                )

                          ))
        }])
;; (defn minimax [board depth isMaximizing]
;;   ()
;;   )

;; (defn AI-move []
;;   (swap! app-state assoc-in [:bestScore] -9999)
;;   (swap! app-state assoc-in [:x] -1)
;;   (swap! app-state assoc-in [:y] -1)
;;   (for [i [0 1 2]
;;        j [0 1 2]]
;;        (when (legal-move-morpion? (:board @app-state) i j)
;;          (set-case-morpion (:board @app-state) 2 i j)
;;          (def score (minimax (:board @app-state) 0 true))
;;          (clean-case-morpion (:board @app-state) i j)
;;          (when (> score (:bestScore @app-state))
;;            (swap! app-state assoc-in [:bestScore] score)
;;            (swap! app-state assoc-in [:x] i)
;;            (swap! app-state assoc-in [:y] j))
;;        )
;;   )
;; )

(defn circle [i j]
      [:circle
       {:r    0.45
        :fill "green"
        :cx   (+ 0.5 i)
        :cy   (+ 0.5 j)
        }])

(defn cross [i j]
      [:g {:stroke       "darkred"
           :stroke-width 0.2
           :strokelincap "round"
           :transform
                         (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ")"
                              "scale(0.35)")}
       [:line {:x1 -1 :y1 -1 :x2 1 :y2 1}]
       [:line {:x1 1 :y1 -1 :x2 -1 :y2 1}]])

;;(defn text-field
;;  "Creates a new text input field."
;;   ([name] (text-field name nil))
;;    ([name value] (input-field name value)))

(defn tictactoe []
      [:div#main
       header-links
       [:h1#title (:text @app-state)]
       [:div#info
        [:div#playerDiv [:p.resultTitle "Your Name"]
         [:p.result {:id "child-one"} "Player 1: "]
         ;;  (text-field (:player1Name @app-state) "paul")
         [:input
         ;;        {  }
          ]
          [:p.result {:id "child-one"} "Player 2: "]
          [:input
         ;;     {     }
           ]
         ]
        [:div#resultatDiv [:p.resultTitle "Ranking"]
         [:p.result {:id "child-one"} "Player 1: " (:scorePlayer1 @app-state)]
         [:p.result {:id "child-one"} "Player 2: " (:scorePlayer2 @app-state)]
         [:p.result {:id "child-one"} "Computer: " (:scoreComputer @app-state)]
         ]
        ]
       (into
         [:svg
          {:view-box "0 0 3 3"
           :width    500
           :height   500}]
         (for [i [0 1 2]
               j [0 1 2]]
              (case (get-case-morpion (:board @app-state) i j)
                    0 [blank i j]
                    1 [circle i j]
                    2 [cross i j])))
       [:p
        [:button#newGame
         {
          :on-click
          (fn new-game-click [e]
              (swap! app-state assoc :board (make-morpion))
              (swap! app-state assoc-in [:win] false)
              (swap! app-state assoc-in [:computer] false)
              (swap! app-state assoc-in [:coup] 0)
              (swap! app-state assoc-in [:player] 1)
              (swap! app-state assoc-in [:text] (str "1vs1"))
              )
          } "NEW GAME"]]])

(rd/render [tictactoe]
           (. js/document (getElementById "app")))

(defn on-js-reload []
      (swap! app-state assoc-in [:text] "TIC TAC TOE")
      )
