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
  (vec (for [x (range n)]
    (with-meta (rand-color-num) {:key n}))))))

(def v (nb3 2))
v
(let [r ((v 1) 0)]
   (meta r))

;to blend
;swap! (map blend board)