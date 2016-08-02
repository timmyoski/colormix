;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.
(def block {:size 100
            :size-str (str (:size block) "px")})

block

(defn px-str [anumb]
  (str anumb "px"))

(px-str 45)

(defn rgb-str [rgbvals]
  (let [[[r g b]] [rgbvals]]
  (str "rgb(" r "," g "," b ")")
  )
)

(rgb-str [122 30 30])

(rand-int 256)

(defn rand-color []
  (rgb-str [(rand-int 256)
            (rand-int 256)
            (rand-int 256)])
)

(rand-color)