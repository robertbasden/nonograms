(ns nonograms.puzzle-select
  (:require
   [sablono.core :as sab :include-macros true])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(def puzzles
  [{:complete true :id "a832a4d5-73e2-4efe-a078-60133536a8a0"}
   {:complete false :id "98e2d18e-bcdc-44d4-b7c1-0787de73d649"}
   {:complete true :id "eca12f3f-393f-4b02-b9db-a6cc7ac39296"}])

(def complete-icon
  [:span {:class "icon icon-complete has-text-success"} [:i {:class "fas fa-2x fa-check"}]])

(def incomplete-icon
  [:span {:class "icon icon-incomplete"} [:i {:class "fas fa-2x fa-question"}]])

(defn puzzle-list-item [puzzle]
  [:li { :id (puzzle :id) :class (if (puzzle :complete) "puzzle-select-item complete" "puzzle-select-item")}
   complete-icon
   incomplete-icon
   [:img {:src "https://bulma.io/images/placeholders/128x128.png"}]])

(defn puzzle-select [puzzles]
  [:ul {:class "puzzle-select cf"} (map puzzle-list-item puzzles)])

(defcard puzzle-select
  "This component is used for displaying a list of puzzles for the user to select from,
  the main change here is how complete / incomplete puzzles are displayed, so given the following data: we could expect this result:"
  (sab/html (puzzle-select puzzles)))
