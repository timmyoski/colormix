;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.
;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.



(defn rand-color-num []
  [(rand-int 256)
   (rand-int 256)
   (rand-int 256)]
)

(defn nb3 [n]
  (vec (for [x (range n)]
  (vec (for [y (range n)]
    (with-meta (rand-color-num) {:key [x y]}))))))

(def board-size 5)

;; -------------------------
;; ALL HAIL THE ATOM
;new-board should take a min value of 2 or else that will fuck up all the neighbor calls... will it?

(defonce app-state
  (atom {:text "...blend away your troubles...."
         :board (nb3 board-size)
         }))
;;ATOM
;;--------------------------
;;--------------------------
;;MOAR Functions

(defn px-str [anumb]
  (str anumb "px"))

(def block {:size (- (:total-size block) (* (:margin block) 2))
            :margin 2
            :total-size 100
            })

(defn rgb-str [rgbvals]
  (let [[[r g b]] [rgbvals]]
  (str "rgb(" r "," g "," b ")")
  )
)

(defn rand-color []
  (rgb-str [(rand-int 256)
            (rand-int 256)
            (rand-int 256)])
)

(defn get-sides [x size];reliant on sq board, min-size = 2x2
  (let [last-index (- size 1)];readability
    (cond
      (and (< 0 x (- size 1))) [(dec x) (inc x)];if not along edge
      (= x 0) [(inc x)];if block on left/top edge
      (= x (- size 1)) [(dec x)];if block on right/bottom edge
      :default nil
  )))

(defn get-all-sides [x y size]
  (let [last-index (- size 1)];readability
  (if (and (<= 0 x last-index) (<= 0 y last-index))
    (vec
      (concat
        (map #(vector % y) (get-sides x size))
        (map #(vector x %) (get-sides y size)))))))

(defn get-xy [n]
  [(int (/ n board-size)) (mod n board-size)])

(defn get-block-n [n]
  (let [[x y] (get-xy n)]
    (get-in @app-state [:board x y])
  ))

(defn get-block-xy [x y]
  (get-in @app-state [:board x y])
  )

; required nonsense...._________________________________________________

(def ff [[5 5][4 4][3 3]])

(apply map + ff)

(def rgb (let [[x y] (get-xy 6)]
      (vec
        (for [[xx yy] (get-all-sides x y board-size)]
            (get-block-xy xx yy)))))
(def g [1 2 3])
rgb
(apply map + rgb)

(get-block-n 5)

(concat rgb)

(def rgb2 (repeat 4 [122 122 122]))
rgb2

(defn blend [n]
  (as-> rgb v
       (concat (repeat 4 (get-block-n n)) v)
       (apply map + v)
       (map #(int (/ % 8)) v);FIXXXX the int rounding, decide math/floor vs ceil
  ));;                       ;;ie will colorvals trend longterm to white/black

(blend 5)

(let [x 5
      f (* x 2)]
  (str x f))





