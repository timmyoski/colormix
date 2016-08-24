(ns colormix.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; to do
;; 1 - refactor xy-n data structure (like every single function nbd) (faster? isn't x-y more dictionary-ish than n?) although map blend n is sexy
;; 2- game "writes itself"
;; 3 - gmae - 999 each block w if gray/brown = make white ..?
;; 4 -?
;; 5 - #1 - white board w/ color "seeds" "g" and spaces
;; make board lookup a funtion of row-size x num-cols y so it can be a rect (and n lookup while youre at it
;; some block don't blend -permanent color-water-falls...
;;on shiiiit
;;only blend in one direction = waterfall = pattern





;; -------------------------
;; GLOBALS/INITIAL STATE
;;---------- this all needs to go in app-state
;;---------- step 2 - dynamic blocks

(def content-width (.-innerHeight js/window));(str (* (:total-size block) num-tiles) "px"))

(def num-tiles 10)
(def block-w-border-size 50)
(def margin-size .5)
(def screen-percent (/ 75 100.0))
;(def weighted-color [20 246 150])
(def weighted-ratio-g 4)

;fucking w dynamic num-tiles fucks with the model (dependent on numtiles)
;can model still get deref? will it auto update?

;; design rerender of one ?diff atom at "janky nav" or any div that goes
;; across the screen so that it triggers at @deref and thus starts a
;; designed chain of derefs/calls to rerender (blocks/divs, width)
;; how make parts of atom recursive/self-dpeendent?

(defn get-block [num-tiles]
  {:margin margin-size
   :total-size (/ (* (.-innerHeight js/window) screen-percent 100) num-tiles)
   :size (- (/ (* (.-innerHeight js/window) screen-percent) num-tiles) (* margin-size 2))
    });;GROSS DOUBLE CODE


;{:margin margin-size
                 ;:total-size block-w-border-size
                 ;:size (- block-w-border-size (* margin-size 2))
                ;}

;; -------------------------
;; Functions that the ATOM builds from

(defn rand-color-num []
  ;[(rand-int 256)
  ; (rand-int 256)
  ; (rand-int 256)]
  [255 255 255]
)

(defn new-board [n]
  (vec (for [y (range n)]
  (vec (for [x (range n)]
    (with-meta (rand-color-num) {:key [x y]
                                 :mutable true}))))))

;; -------------------------
;; -------------------------
;; ALL HAIL THE ATOM
;new-board should take a min value of 2 or else that will fuck up all the rgb-side calls... will it?

(defonce app-state
  (atom {:text "...blend away your troubles...."
         :board (new-board num-tiles)
         :width (* (.-innerHeight js/window) screen-percent);def once....
         :block (get-block num-tiles)
         :background-color [255 255 255]
         :weighted-color [20 246 150]
         }))
;;
;;--------------------------
;;--------------------------
;;MOAR Functions

(defn px-str [anumb]
  (str anumb "px"))

(defn rgb-str [rgbvals]
  (let [[r g b] rgbvals]
  (str "rgba(" r "," g "," b ", .90)");;chaaaaaaaaaaaaaaaaaanged
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
  )));;->[5 7]=>[4 6]

(defn get-all-sides [x y size]
  (let [last-index (- size 1)];readability
  (if (and (<= 0 x last-index) (<= 0 y last-index))
    (vec
      (concat
        (map #(vector % y) (get-sides x size))
        (map #(vector x %) (get-sides y size))))))
);->[2 3 6]=>[[3 3] [1 3] [2 4] [2 2]]

(defn get-xy [n];;wtffffff this is really get-yx
  [(int (/ n num-tiles)) (mod n num-tiles)])

;;---------------------------------
;; Derefs

(defn get-block-n [n]
  (let [[x y] (get-xy n)]
    (get-in @app-state [:board x y]);;------DEREF!!!!!!!!!!!!!!!!!!!
  ))

(defn get-block-xy [x y]
  (get-in @app-state [:board x y]);;------DEREF!!!!!!!!!!!!!!!!!!!
  )

(defn rgb-side-vals [x y board];->[[3 3] [1 3] [2 4] [2 2]]=>[[50 255 0]... etc
  (vec (map #(get-in board %) (get-all-sides x y num-tiles))));-----GLOBAL


(defn set-mutability [n truthy]
  ;(get-in @app-state x
  ;(swap! app-state assoc-in [:board x y]
  (let [block-model (get-block-n n)
        new-meta-info (assoc (meta block-model) :mutable truthy)
        [x y] (get-xy n)
        block (get-in app-state [:board x y])
        ]
    ;(swap! app-state assoc-in [:board x y meta] truthy)
    ;(assoc-in meta-info [:mutable] truthy)
    (prn new-meta-info)
    ;(alter-meta! block-model
    )

  )

;;---------------------------------
;; Mutators

;;should it like just take a vector of colors and return the blended color?
;;swap can be in sep function?
;;blend colors or rgb-sides (##### FUNCTIONAL PROG####) -> blend colors->build up more complex f->reuse code
;;can call "blend" on all colors -> new board
;;swap/reset entire thing
;;
;;oh yea and make the sizes mutable by sliders
;;-global state width/block


;->[[33 33 33] ... [] ... []] [vector of [vector of 3 int color vals]]
;=>[44 55 222] [vector of avgs]
(defn avg-colors[ & color-vecs ] ;;if supply 2/1 val vecs will return avg of only rb/r respectively
  (vec (map #(int (/ % (count color-vecs))) (vec (apply map + color-vecs)))))
;;;;;;;;;JANKY INT rounding

(defn weight-by [n block-color rgb-side-colors];;returns a new [vec of [co lor vecs]] with ratio of
  (vec (concat (repeat (* n (count rgb-side-colors)) block-color);; bcolor/ncolor = n
          rgb-side-colors)))

;;swap new-color with proper cursor (create that too)
(defn blend-refactor [n board];;USE POLYMORPHISM??? SO THAT...ahem... when if you supply additional args it will use that as weighted color?
  (let [[x y] (get-xy n);board (get-in @app-state [:board]);;------DEREF!!!!!!!!!!!!!!!!!!!
        rgb-side-colors (rgb-side-vals x y board)
        block (get-block-n n)
        meta-info (meta block)]
    (if (:mutable (meta ((board y) x)))
      (swap! app-state assoc-in [:board x y] ;;CURSOR ME PLZZZZ
           (with-meta
             (apply avg-colors (weight-by 30 (get-in board [x y]) rgb-side-colors))
             {:key [x y] :mutable (:mutable meta-info)}
             ))
                  ;;ratio of "move"/"click"? 2???
  )))

;;(if (:mutable (meta ((board y) x)))
;;                (swap! app-state assoc-in [:board x y] (with-meta v {:key [x y]
;;                                                                     :mutable (:mutable meta-info)}))




;;also, more important point, you simply deref the part of the atom you'd like to react to in
;;the given component in the component function or one of the functions within it and voila!
;;every time some part of the program swap! or reset! that part of the atom (via cursor)
;;the component functions that deref that part of the atom will be re-run

;;aka don't need to deref in swap
;;deref at component and swap will activate re-render


;;made a game by accident
;;click = avg w "your" color---- only get like 2 clicks per round
;;move = blend
;;if you notice moving has a shiload of strat (keep "pure" blocks and blend off that"
;;players get liek 3x moves a round
;;every like 5 "rounds" blendall is called



(defn blend [n board];;delete after polymorphism on blend-refactor
                    ;;only used as a placeholder for onclick handler
  (let [;board (get-in @app-state [:board]);;------DEREF!!!!!!!!!!!!!!!!!!!
        [x y] (get-xy n);;REALLY Y X
        rgb-side-vals (rgb-side-vals x y board)
        num-side-vals (count rgb-side-vals)
        weighted-ratio 5
        weighted-c (get-in @app-state [:weighted-color])
        num-repeats (* weighted-ratio num-side-vals)
        block (get-block-n n)
        meta-info (meta block)
        ]
        (as-> rgb-side-vals v
              (concat (repeat 5 weighted-c) (repeat num-repeats (get-in board [x y])) v);;WHY NOT WORK???
              (apply map + v)
              (vec (map #(int (/ % (+ num-repeats num-side-vals 1))) v));FIXXXX the int rounding, decide math/floor vs ceil
              (if (:mutable (meta ((board y) x)))
                (swap! app-state assoc-in [:board x y] (with-meta v {:key [x y]
                                                                     :mutable (:mutable meta-info)}))
                (prn (meta ((board y) x)) " is the meta from: " ((board y) x) " from " x ","y))
  )))

(defn blend-all [e]
  (let [board (get-in @app-state [:board])
        first-row (get-in board [1])];;------DEREF!!!!!!!!!!!!!!!!!!!
  (.preventDefault e "false")
  (comment
    (prn (.-keyCode e) " : keycode")
    (prn (js-keys e) " : js-keys")
    (prn (.-id (.-target e)) " : target-id?")
    ;;is e.id the same as the ^{key} meta info we put on it?
    (prn "reached them all")
  )
  (doall (for [i (range (* num-tiles num-tiles))]
    (blend-refactor i board);(prn (meta color))
  ))
    (.stopPropagation e)
    ))

(def startcolor 0)

;;
;; WHOA duuuuuuude
;; css to provide filters like aftereffects for videos insta? apply globally not to each frame

;; WHOAAAA
;; "...blend away your troubles..." acts like a max/container for the y axis
;; once that's centered then we have a y-centered "base" to build from (flexbox plz)

;;gradient composer -> pattern to blend

(defn rgb-input [color-type]
  (let [rgb ["red" "green" "blue"]]
    [:input {:class "rgb-input"
             :type "text"
             :max-length 3
             :name color-type
             :placeholder color-type
             :style {:border-color color-type}
             ;; :on-key-press (fn [e] (if (< 47 (.-keyCode e) 58) (do (prn (.-keyCode e)) e)))
             :on-change (fn [e] (swap! app-state assoc-in
                                   [:weighted-color (.indexOf rgb color-type)]
                                   (int (.-value (.-target e)))))
             }]))

(defn block-html [n state];; state = @app-state
  (let [block (:block state)]
    ^{:key n}
    [:div { :class "colorbox"
            :style {:color "black"
                    :float "left"
                    :font-family "Arial"
                    :word-wrap "break-word"
                    :background-color (rgb-str (get-block-n n));;------DEREF!!!!!!!!!!!!!!!
                    :margin (px-str (:margin block))
                    :width (px-str (:size block))
                    :height (px-str (:size block))
                    }
            :on-mouse-move (fn [e] (.preventDefault e "false");stops text/mouse highlighting
                                   (blend-refactor n (get-in @app-state [:board])));(prn (js-keys (.-style (.-target e))))
                                   ;(swap! app-state assoc-in [:background-color] (rgb-str (get-in @app-state [:background-color])))
            ;;                ;;------DEREF!!!!!!!!!!!!!!!!!!!
            :on-click (fn [e] (do (blend n (:board state))));;------DEREF!!!!!!!!!!!!!!!!!!!
            }
      ])
  )


;;jquery reagent .css set/get... sass?

(defn render-colormix []
  [:div {:class "react-container"}
    [:div {:class "title-wrapper"} [:h2 {:class "tagline"} (doall (for [chr (:text @app-state)
                              :let [len (count (:text @app-state))
                                   ind (range len)]]
                                    ^{:key (random-uuid)}[:span {:style {:color (rgb-str (vec (repeat 3 (* ind (int (/ 255 len))))))}} chr]))]
     ];;------DEREF!!!!!!!!!!!!!!!!!!!
   ;[:div {:class "jankynav"} [:a {:href "/about"} "go to about page"]]
   [:div {:class "inputs"}
    [:div {:class "submit-button"
                   :style {:background-color (rgb-str (get-in @app-state [:weighted-color]))}}

    (rgb-input "red");onkeypress='return event.charCode >= 48 && event.charCode <= 57'>
    (rgb-input "green");onenter-> sw focus to next?
    (rgb-input "blue");add some sort of button that has a :on method that incs/decs the color input
                       ;just have it dec the val, start that chain of derrefs/renders

                  ]];on-click->submit
   [:div {:class "content"
          :style {:width (get-in @app-state [:width])}}
       (doall
         (for [n (range (* num-tiles num-tiles))]
           (block-html n @app-state)
           ;;[:div {:class "colorbox"
            ;;      :style {:background-color (rgb-str (get-block-n n));;------DEREF!!!!!!!!!!!!!!!!!!!
            ;;             :color "black"
            ;;              :margin (px-str (get-in @app-state [:block :margin]))
            ;;              :width (px-str (get-in @app-state [:block :size]))
            ;;            :height (px-str (get-in @app-state [:block :size]))
            ;;              :float "left"
            ;;              :font-family "Arial"
            ;;              :word-wrap "break-word"}
                  ;;:on-mouse-move (fn [e] (.preventDefault e "false");stops text/mouse highlighting
                  ;;                       (blend-refactor n (get-in @app-state [:board]));(prn (js-keys (.-style (.-target e))))
                                         ;(swap! app-state assoc-in [:background-color] (rgb-str (get-in @app-state [:background-color])))
                  ;;                );;------DEREF!!!!!!!!!!!!!!!!!!!
            ;;      :on-click (fn [e] (do (blend n (get-in @app-state [:board]))));;------DEREF!!!!!!!!!!!!!!!!!!!
            ;;      }
            ;;]
           )
         )
      ;;[:div {:class "submit-button" :style {:background-color (rgb-str (vec (map #(int (* % 9/10)) (get-in @app-state [:weighted-color]))))}}]
    ]])

;;--------------------------
;; Listeners

(defn key-handlers [e]
  (do
    ;(prn (.-keyCode e))
    (cond
      (= (.-keyCode e) 32) (blend-all e);;stop propigation() AAAAAND stopDefault()
      (= (.-keyCode e) 71) (doall (for [i (range 10)] (blend-all e)))
    )
  ))

(defn update-content-width []
  (swap! app-state assoc-in [:width] (* (.-innerHeight js/window) (/ 75 100.0)))
  (swap! app-state assoc-in [:block] (get-block num-tiles))
)

(defn add-listeners []
  (.addEventListener js/window "keyup" key-handlers)
  (.addEventListener js/window "onresize" update-content-width)
)

;; -------------------------
;; Views


(defn about-page []
  [:div [:h2 "About colormix"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; Main view
(defn home-page []
  (render-colormix))

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))


;; -------------------------
;; Initialize app

(defn mount-root []
  (do
    ;(prn (:board @app-state))
    (reagent/render [current-page] (.getElementById js/document "app"))
    ))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root)
  (add-listeners))
