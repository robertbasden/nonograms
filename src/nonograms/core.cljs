(ns nonograms.core
  (:require
   [sablono.core :as sab :include-macros true]
   [nonograms.puzzle-select :as puzzle-select]
   [nonograms.puzzle-display]
   [cljs.test :as test]
   [cljs-time.core :as time])

  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(enable-console-print!)

(def row [true true false true false true true true false true])

(defn derive-clues [list]
  "Given a row (or column) in the puzzle extract the clues to display for that particular row, this
  basically amounts to counting the groups of true (filled) squares"
  (first (reduce (fn [[clues count] value]
                   (if value
                     [clues (inc count)]
                     [(conj clues count) 0])) [[] 0] (conj list false))))

(defcard derive-clues
  "Given a row (or column) in the puzzle extract the clues to display for that particular row, this
   basically amounts to counting the groups of true (filled) squares"
  (derive-clues row))

(def puzzles
  [{:complete true :id "a832a4d5-73e2-4efe-a078-60133536a8a0" }
   {:complete false :id "98e2d18e-bcdc-44d4-b7c1-0787de73d649" }
   {:complete true :id "eca12f3f-393f-4b02-b9db-a6cc7ac39296" }])

(def state (atom (time/now)))

(defn count []
  (js/console.log (time/in-seconds (time/interval @state (time/now))))
  (.requestAnimationFrame js/window count))

(defn main []
  ;; conditionally start the app based on whether the #main-app-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (sab/html [:div { :class "container" } (puzzle-select/puzzle-select puzzles)]) node)))



(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html

