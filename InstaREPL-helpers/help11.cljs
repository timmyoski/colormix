;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.
(def f (vec (repeat 5 (vec (repeat 5 [122 122 122])))))

(f 1)
((f 1) 1)


(apply str (f 1))

(def board-size 5)

(defn get-xy [n]
  [(int (/ n board-size)) (mod n board-size)])

(get-xy 8)

(map #(str %) [1 2 3])
(def as 4)



(defn new-board [n]
  (vec (for [x (range n)]
  (vec (for [y (range n)]
    (with-meta (rand-color-num) {:key [x y]}))))))

;; -------------------------
;; -------------------------
;; ALL HAIL THE ATOM
;new-board should take a min value of 2 or else that will fuck up all the neighbor calls... will it?

(defonce app-state
  (atom {:text "...blend away your troubles...."
         :board (new-board board-size)
         }))


(defn get-sides [x size];reliant on sq board, min-size = 2x2
  (let [last-index (- size 1)];readability
    (cond
      (and (< 0 x (- size 1))) [(dec x) (inc x)];if not along edge
      (= x 0) [(inc x)];if block on left/top edge
      (= x (- size 1)) [(dec x)];if block on right/bottom edge
      :default nil
  )));;->[5 7]=>[4 6]

(defn get-all-sides [x y size]
  (let [last-index (- size 1)];readability
  (if (and (<= 0 x last-index) (<= 0 y last-index))
    (vec
      (concat
        (map #(vector % y) (get-sides x size))
        (map #(vector x %) (get-sides y size))))))
);->[2 3 6]=>[[3 3] [1 3] [2 4] [2 2]]
(def tboard (get-in @app-state [:board]))
(vec (map #(get-in board %) (get-all-sides 2 3 5)))

(defn rgb-side-vals [x y board];->[[3 3] [1 3] [2 4] [2 2]]=>[[50 255 0]... etc
  (vec (map #(get-in board %) (get-all-sides x y board-size))))


(rgb-side-vals 1 2 tboard)


