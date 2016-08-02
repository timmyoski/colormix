;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.
(* 5 6)

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

(defn new-board [n]
  (vec (repeat n (vec (repeatedly n (rand-color))))))

(defn nb [n]
  (vec(repeatedly n rand-color)))

(nb 10)

(defn nb3 [n]
  (vec (for [x (range n)]
  (vec (for [x (range n)]
    (rand-color))))))

(nb3 3)


(defonce app-state
  (atom {:text "ColorMix... blend away your troubles"
         :board (nb3 board-size)
         }))

(int (/ 8 5))
(mod 8 5)

(mod 5 8)

(def board-size 5)

(defn get-xy [n]
  [(int (/ n board-size)) (mod n board-size)])

;(get-xy 8 3)

(defn get-block [n]
  (let [[x y] (get-xy n)]
    (get-in @app-state [:board x y])
  ))

(get-xy 8)

(get-in @app-state [:board 1 1])


(get-block 4)

(not= 3 4)

(defn get-neighbors [n]
  (set [(if (= (mod n board-size) 0);if on left edge
     (get-block n)
     (get-block (dec n))
    )
   (if (= (mod n board-size) 4);if on right edge
     (get-block n)
     (get-block (inc n))
   )
   (if (< n board-size);if top level
     (get-block n)
     (get-block (- n board-size))
   )
   (if (> n (* board-size (dec board-size)));if on bottom
     (get-block n)
     (get-block (+ n board-size))
    )
  ])
)

(defn pp [n]
  (if (> n (* board-size (dec board-size)));if on bottom
     (get-block n)
     (get-block (- n board-size))
    ))
(pp 0)
(get-block 0)

(> 0 (* board-size (dec board-size)))

(get-neighbors 0)
(get-block 0)

(get-block -3)
(get-xy -3)

(int (/ -3 5))

(< 3 5)


