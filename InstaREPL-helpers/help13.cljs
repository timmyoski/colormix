;; Anything you type in here will be executed
;; immediately with the results shown on the
;; right.


;->[[33 33 33] ... [] ... []] [vector of [vector of 3 int color vals]]
;=>[44 55 222] [vector of avgs]
(defn avg-colors[ & color-vecs ] ;;if supply 2/1 val vecs will return avg of only rb/r respectively
  (vec (map #(int (/ % (count color-vecs))) (vec (apply map + color-vecs)))))
;;;;;;;;;JANKY INT rounding


(avg-colors [22 22 22] [222 222 222] [1 1 1] [2 3 800])

(defn weight-by [n block-color rgb-side-colors];;returns a new [vec of [co lor vecs]] with ratio of
  (vec (concat (repeat (* n (count rgb-side-colors)) block-color);; bcolor/ncolor = n
          rgb-side-colors)))


(def bcolor [255 255 255])
(def ncolors [[1 1 1] [1 1 1] [1 1 1] [1 1 1]])


(apply avg-colors (weight-by 3 bcolor ncolors))


(defn tester []
  (->> (+ 3 3)
       (- 5)))

(tester)

(range 5)





