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

;(defn new-board [n]
;  (vec (repeat n (vec (repeat n (rand-color))))))

(defn nb3 [n]
  (vec (for [x (range n)]
  (vec (for [y (range n)]
    (with-meta (rand-color-num) {:key [x y]}))))))

(defonce v (nb3 2))
v
(let [r ((v 1) 0)]
   (meta r))

(:key (meta (get-in v [1 0])))

;to blend
;swap! (map blend board)



(defn nb4 [n]
  (vec (for [x (range n)]
  (vec (for [y (range n)]
    (with-meta [x y] {:key [x y]}))))))

(nb4 4)

 (defn get-neighbors [x y n]
   [[x (dec y)]
    [x (inc y)]
    [y (dec x)]
    [y (inc x)]])

 (get-neighbors 1 2 3)

 (defn get-sides [x size];reliant on sq board, min-size = 2x2
   (let [last-index (- size 1)];readability
   (cond
   (and (< 0 x (- size 1))) [(dec x) (inc x)];if not along edge
   (= x 0) [(inc x)];if block on left/top edge
   (= x (- size 1)) [(dec x)];if block on right/bottom edge
   :default nil
   )));///////will return nil if out of range

 (defn get-all-sides [x y size]
   (let [last-index (- size 1)];readability
   (if (and (<= 0 x last-index) (<= 0 y last-index))
   (concat
      (map #(vector % y) (get-sides x size))
      (map #(vector x %) (get-sides y size))))))


 (get-all-sides 0 0 6)

 (let [x 0 y 6]
   (def x-sides (get-sides x 3))
   (def y-sides (get-sides y 3)))


  (def xxx (for [x-sides (get-sides 1 3)
             :let [y-sides (get-sides 1 3)]]
     [x-sides 1]))
   (def x 100)
   (def y 33)
   (concat (map #(vector % y) [4,4]) (map #(vector x %) [1,2]))
(vec [[1,2] [3,4]])


(vec (concat nil [1,2]))

(< 9 7 9)
(def s 5)
(def ss (vec (range s)))

(count ss)






