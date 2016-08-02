;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.
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

(defn rand-color-num [n]
  ^{:key n}
  [(rand-int 256)
   (rand-int 256)
   (rand-int 256)]
)

;(defn new-board [n]
;  (vec (repeat n (vec (repeat n (rand-color))))))

(defn nb3 [n]
  (vec (for [x (range n)]
  (vec (for [x (range n)]
    (rand-color-num n))))))

(def v (nb3 2))

(let [r ((v 1) 0)]
   (meta r))

(def tt (vec (for [x (range 10)]
     (with-meta
        (str "my name is " "Timmy " "No. " x)
        {:doc "xgadgadsga"}))))

(def f [(tt 2)])

(def d (with-meta f {:doc "wwwww"}))

(meta d)




