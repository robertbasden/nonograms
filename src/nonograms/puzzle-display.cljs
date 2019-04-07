(ns nonograms.puzzle-display
  (:require
   [sablono.core :as sab :include-macros true]
   [cljs.test :as test]
   [reagent.core :as reagent])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)

(defn puzzle-square-class [type]
  (case type 
    :box "puzzle-square puzzle-square-box"
    :cross "puzzle-square puzzle-square-cross"
    "puzzle-square"
    ))

(defn puzzle-square [[x y] type]
  [:g {:transform (str "translate (" x ", " y ")") :class (puzzle-square-class type)}
   [:rect {:x 0 :y 0 :width 32 :height 32}]
   [:path {:d "M8 8 L24 24 M24 8 L8 24"}]])

(defn display-test-squares []
  (sab/html [:svg { :class "puzzle" :width 116 :height 32 }
             (puzzle-square [0 0] nil)
             (puzzle-square [32 0] :box)
             (puzzle-square [64 0] :cross)]))

(defcard puzzle-square
  (display-test-squares))

(deftest puzzle-square-tests
  (test/testing "Getting correct class attribute"
    (test/is (= (puzzle-square-class nil) "puzzle-square"))
    (test/is (= (puzzle-square-class :box) "puzzle-square puzzle-square-box"))
    (test/is (= (puzzle-square-class :cross) "puzzle-square puzzle-square-cross")))
  (test/testing "Setting position correctly"
    (test/is (= (puzzle-square-class nil) "puzzle-square"))
    (test/is (= (puzzle-square-class :box) "puzzle-square puzzle-square-box"))
    (test/is (= (puzzle-square-class :cross) "puzzle-square puzzle-square-cross"))))

(def test-board
  [[nil :cross nil nil] [:box :box :box nil] [:cross :box nil :cross] [nil :box nil :cross]])

(defn board [board]
  (map-indexed (fn [x col] (map-indexed (fn [y element] (puzzle-square [(* x 32) (* y 32)] element)) col)) board))

(defn display-test-board []
  (sab/html [:svg {:class "puzzle" :width 128 :height 128}
             (board test-board)]))

(defcard board
  (display-test-board))

;; (+ (* idx 32) 16)
(defn row-clue [idx clue total-clues]
  [:text {:x (+ (- 160 (* 32 (- total-clues idx))) 16) :y 18 :text-anchor "middle" :alignment-baseline "middle" } clue])

(defn row-clues [row clues]
  [:g { :transform (str "translate (0, " (* row 32) ")") } (map-indexed (fn [idx clue] (row-clue idx clue (count clues))) clues)])

(defn col-clue [idx clue total-clues]
  [:text {:x 16 :y (+ (- 160 (* 32 (- total-clues idx))) 18) :text-anchor "middle" :alignment-baseline "middle" } clue])

;; we need to split each clue into a seperate text element and position them manually ;(
(defn col-clues [col clues]
  [:g {:transform (str "translate (" (* col 32) ", 0)")} (map-indexed (fn [idx clue] (col-clue idx clue (count clues))) clues)])

(defcard row-clues
  (sab/html [:svg {:class "clues" :width 128 :height 128}
             (row-clues 0 [1 1 1])
             (row-clues 1 [1 2])
             (row-clues 2 [3])
             ]))

(defcard col-clues
  (sab/html [:svg {:class "clues" :width 128 :height 128}
             (col-clues 0 [1 1 1])
             (col-clues 1 [1 2])
             (col-clues 2 [3])]))

(def row-clues-data [
                     [1 10 6]
                     [2]
                     [1 3]])

(def col-clues-data [[1 1 1]
                     [2 1 1]
                     [1 3]])


;;:on-click (fn [event] (reset! mouse-position {:x (.-clientX event) :y (.-clientY event)}))
;; :onMouseMove (fn [event] (console/log ((.getBoundingClientRect (-> event .-currentTarget)) .-left)) (reset! mouse-position {:x (- (.-pageX event) (-> event .-currentTarget .-offsetLeft)) :y (.-clientY event)}))
;; :onMouseMove (fn [event] (println "asd"))

(defn round-position [{x :x y :y}]
  { :x (Math/floor (/ x 32)) :y (Math/floor (/ y 32)) })

(defn get-relative-position [event]
  (let [elementX (.-x (.getBoundingClientRect (.-currentTarget event)))
        mouseX (.-clientX event)
        relativeX (- mouseX elementX)
        elementY (.-y (.getBoundingClientRect (.-currentTarget event)))
        mouseY (.-clientY event)
        relativeY (- mouseY elementY)]
    { :x relativeX :y relativeY }))

(defn map-position-value [position-value]
  (Math/floor (/ (- position-value 160) 32)))

(defn map-position-values [{x :x y :y}]
  { :x (map-position-value x) :y (map-position-value y)})

(defn remove-negative-value [{x :x y :y :as position}]
  (if (or (< x 0) (< y 0)) nil position))

(defn map-position [event]
  (-> event
      get-relative-position
      map-position-values
      remove-negative-value))

(defn cursor-display [position]
  (if (nil? position)
    [:g {:class "cursor"}]
    [:g {:class "cursor"}
     [:rect {:x (* (get (round-position position) :x) 32) :y 0 :width 32 :height 500 :fill "#2980b9" :opacity 0.1}]
     [:rect {:x 0 :y (* (get (round-position position) :y) 32) :width 500 :height 32 :fill "#2980b9" :opacity 0.1}]]))

(defn display [row-clues-data col-clues-data click-handler]
  (let [mouse-position (reagent/atom nil)]
    (fn [row-clues-data col-clues-data]
      [:div { :class "nonogram-wrapper"
             :style { :width 480 :height 480 }
             :on-click (fn [event] (click-handler (map-position event)))
             :onMouseOut (fn [event] (reset! mouse-position nil))
             :onMouseMove (fn [event]
                            (let [position (map-position event)
                                  positionX (position :x)
                                  positionY (position :y)
                                  markerX (+ 160 (* 32 positionX))
                                  markerY (+ 160 (* 32 positionY))]
                              (reset! mouse-position {:x markerX :y markerY}))) }
       [:svg {:class "nonogram" :width 480 :height 480 }
        [:g {:class "row-clues" :transform "translate(0, 160)"} (map-indexed row-clues row-clues-data)]
        [:g {:class "column-clues" :transform "translate(160, 0)"} (map-indexed col-clues col-clues-data)]
        [:defs
         [:pattern {:id "grid" :width 32 :height 32 :patternUnits "userSpaceOnUse"}
          [:path {:d "M0 0 H32 V32 H-32 V-32" :fill "none" :stroke "#CCC" :stroke-width "1"}]]]
        (cursor-display @mouse-position)
        [:g {:class "grids"}
         [:rect {:x 0 :y 0 :width 480 :height 480 :fill "url(#grid)"}]
         [:rect {:x 0 :y 160 :width 160 :height 400 :stroke "black"}]
         [:rect {:x 160 :y 0 :width 400 :height 160 :stroke "black"}]
         [:rect {:x 0 :y 0 :width 160 :height 160 :fill "#FFF"}]]]])))