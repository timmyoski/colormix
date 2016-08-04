;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.

;; -------------------------
;; Functions that the ATOM relies on

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

(def rgb (let [[x y] (get-xy 6)]
      (vec
        (for [[xx yy] (get-all-sides x y board-size)]
            (get-block-xy xx yy)))))
(def g [1 2 3])
rgb
(concat rgb (repeat (* (count rgb) 2) g))

(defn blend [n]
  (as-> rgb v
       (map + (v 0) (v 1) (v 2) (v 3) [1 2 3])
       (map #(int (/ % 3)) v);shittest way possible buggy and not precise
  ))

(blend 5)
(take (count rgb) rgb)


(rgb 1)

;;_______________________________________________________
(mapv + [[1 2 3] [4 5 6] [6 7 8]])
(mapv + [1 2 3] [4 5 6] [6 7 8])
;;----------------------$$$$$$$$ this is prob
;;

(map + (rgb 2) (rgb 3) (rgb 1))
(for [x (range (count rgb))] (rgb x))

(let [& rgb] &)


(def g [1,3])
(concat (vec (repeat 2 g)) (vec (repeat 2 g)))
(vec (repeat 2 g))

(def f [[5 5][4 4][3 3]]
f

  ;atom :board-size button to change on the fly, reset board too...



