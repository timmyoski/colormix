;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.
(defn multi [ & b]
  (str b))

(multi 8 9 7)

(defn blend [] ())

(* (/ 1 100.0) 7)

(/ 66 100.0)

(def board-size 10)
(def block-w-border-size 75)
(def margin-size 1)

(defonce app-state
  (atom {:text "...blend away your troubles...."
         :board 5;(new-board board-size)
         :width 5;(* (.-innerWidth js/window) (/ 75 100.0))
         :block {:margin margin-size
                 :total-size block-w-border-size
                 :size (- block-w-border-size (* margin-size 2))
                }

         }))

(get-in @app-state [:block :size])


(defn rgb-str [rgbvals]
  (let [[r g b] rgbvals]
  (str "rgb(" r "," g "," b ")")
  )
)



(rgb-str [22 33 44])

(def bj "rgb(233, 102, 164)")

(spl (re-find (re-pattern "\\d+, \\d+, \\d+") bj) ", ")



