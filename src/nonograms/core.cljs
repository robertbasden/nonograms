(ns nonograms.core
  (:require
   [sablono.core :as sab :include-macros true]
   [nonograms.puzzle-select :as puzzle-select]
   [nonograms.puzzle-display :as puzzle-display]
   [cljs.test :as test]
   [cljs-time.core :as time]
   [reagent.core :as reagent]))

;; Data

(def puzzles [{:id "3deb3e23-e59c-46db-a365-df97bda7fad5" :name "Boat", :row-clues [[1 1] [2 2]], :col-clues [[3 4] [5 6]]}
              {:id "2eb36737-8988-4e13-85e1-8bdaf077accb" :name "Plane", :row-clues [[1 1 1 1] [2 3 2]], :col-clues [[3 1 4] [2]]}])

(def complete-puzzles (list "3deb3e23-e59c-46db-a365-df97bda7fad5"))

(enable-console-print!)

;; State

(def state (reagent/atom nil))

(defn start-puzzle [id]
  (reset! state id))

(defn cancel-puzzle []
  (reset! state nil))

(defn get-puzzle [id-to-find]
  (first (filter #(= (get %1 :id) id-to-find) puzzles)))



;; Pages

(defn puzzle-select [puzzles completed-puzzles puzzle-click]
  (let [puzzle-display (map (fn [{id :id :as puzzle}] (if (some #{id} completed-puzzles) (assoc puzzle :complete true) puzzle)) puzzles)]
    (puzzle-select/puzzle-select puzzle-display puzzle-click)))

(defn test-handler [result]
  (js/console.log result))

(defn playing-puzzle [puzzle]
  [:div
   [:div
    [puzzle-display/display (get puzzle :row-clues) (get puzzle :col-clues) test-handler]]
   [:div {:on-click cancel-puzzle} "cancel"]]
  )

(defn page []
  [:div { :class "container" }
   (if (nil? @state)
     (puzzle-select puzzles complete-puzzles #(start-puzzle %1))
     (playing-puzzle (get-puzzle @state)))])

;; Setup and run

(defn main []
  (reagent/render-component [page]
            (.-body js/document)))

(main)