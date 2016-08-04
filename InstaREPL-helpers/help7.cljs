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
    (vec (concat
        (map #(vector % y) (get-sides x size))
        (map #(vector x %) (get-sides y size)))))))

(defn get-xy [n]
  [(int (/ n board-size)) (mod n board-size)])

(def rgb (let [[x y] (get-xy 6)]
      (vec
        (for [[xx yy] (get-all-sides x y board-size)]
            (get-block-xy xx yy)))))

rgb
(count rgb)

(def g [1,3])
(concat (vec (repeat 2 g)) (vec (repeat 2 g)))

(defn get-block-xy [x y]
  (get-in @app-state [:board x y])
  )


(get-xy 6)
(get-all-sides 1 1 5)



;;;;;;

(def rgb (let [[x y] (get-xy 6)]
      (vec
        (for [[xx yy] (get-all-sides x y board-size)]
            (get-block-xy xx yy)))))

rgb
(count rgb)

(+ 3 3)











