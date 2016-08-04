(ns colormix.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))


;; -------------------------
;; GLOBALS/INITIAL STATE
;;---------- this all needs to go in app-state
;;---------- step 2 - dynamic blocks


(def board-size 10)
(def block-w-border-size 75)
(def margin-size 2)

(def block {:margin margin-size
            :total-size block-w-border-size
            :size (- block-w-border-size (* margin-size 2))
            })

(def content-width (str (* (:total-size block) board-size) "px"))


;; -------------------------
;; Functions that the ATOM relies on

(defn rand-color-num []
  [(rand-int 256)
   (rand-int 256)
   (rand-int 256)]
)

(defn new-board [n]
  (vec (for [y (range n)]
  (vec (for [x (range n)]
    (with-meta (rand-color-num) {:key [x y]}))))))

;; -------------------------
;; -------------------------
;; ALL HAIL THE ATOM
;new-board should take a min value of 2 or else that will fuck up all the neighbor calls... will it?

(defonce app-state
  (atom {:text "...blend away your troubles...."
         :board (new-board board-size)
         }))
;;
;;--------------------------
;;--------------------------
;;MOAR Functions

(defn px-str [anumb]
  (str anumb "px"))

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
  )));;->[5 7]=>[4 6]

(defn get-all-sides [x y size]
  (let [last-index (- size 1)];readability
  (if (and (<= 0 x last-index) (<= 0 y last-index))
    (vec
      (concat
        (map #(vector % y) (get-sides x size))
        (map #(vector x %) (get-sides y size))))))
);->[2 3 6]=>[[3 3] [1 3] [2 4] [2 2]]

(defn get-xy [n]
  [(int (/ n board-size)) (mod n board-size)])

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
  (vec (map #(get-in board %) (get-all-sides x y board-size))));-----GLOBAL

;;---------------------------------
;; Mutators

;;should it like just take a vector of colors and return the blended color?
;;swap can be in sep function?
;;blend colors or neighbors (##### FUNCTIONAL PROG####) -> blend colors->build up more complex f->reuse code
;;can call "blend" on all colors -> new board
;;swap/reset entire thing
;;
;;oh yea and make the sizes mutable by sliders
;;-global state width/block
;;


(defn blend [n board]
  (let [;board (get-in @app-state [:board]);;------DEREF!!!!!!!!!!!!!!!!!!!
        [x y] (get-xy n)
        side-rgb-nvals (rgb-side-vals x y board)
        num-side-vals (count side-rgb-nvals)
        weighted-ratio 1
        ;weighted-color [0 0 255]
        num-repeats (* weighted-ratio num-side-vals)
        ]
        (as-> side-rgb-nvals v
              (concat (repeat num-repeats (get-in board [x y])) v)
              (apply map + v)
              (vec (map #(int (/ % (+ num-repeats num-side-vals))) v));FIXXXX the int rounding, decide math/floor vs ceil
              (swap! app-state assoc-in [:board x y] v)
  )))

(defn blend-all [e]
  (let [board (get-in @app-state [:board])
        first-row (get-in board [1])];;------DEREF!!!!!!!!!!!!!!!!!!!
  (.preventDefault e "false")
  (prn (.-keyCode e))
  (prn "reached them all")
  (doall (for [color first-row]
    (blend ((:key (meta color)) 1) board);(prn (meta color))
  ))))

(defn render-colormix []
  [:div {:class "react-container"} [:h2 (:text @app-state)];;------DEREF!!!!!!!!!!!!!!!!!!!
   [:div {:class "jankynav"} [:a {:href "/about"} "go to about page"]]
   [:div {:class "content"
          :style {:width content-width}
          }
       (doall (for [n (range (* board-size board-size))] ^{:key n}
         [:div {:style {:background-color (rgb-str (get-block-n n));;------DEREF!!!!!!!!!!!!!!!!!!!
                        :color "black"
                        :margin (px-str (:margin block))
                        :width (px-str (:size block))
                        :height (px-str (:size block))
                        :float "left"
                        :font-family "Arial"
                        :word-wrap "break-word"}
                :class "colorbox"
                :on-mouse-move (fn [e] (.preventDefault e "false");stops text/mouse highlighting
                                       (blend n (get-in @app-state [:board])));;------DEREF!!!!!!!!!!!!!!!!!!!
                :on-click (fn [e] (do (blend n (get-in @app-state [:board]))));;------DEREF!!!!!!!!!!!!!!!!!!!
                }
                [:p (rgb-str (get-block-n n))]]));;------DEREF!!!!!!!!!!!!!!!!!!!
   ]])

;;--------------------------
;; Listeners

(defn key-handlers [e]
  (cond
    (= (.-keyCode e) 32) (blend-all e)
  ))

(defn add-listeners []
  (.addEventListener js/window "keyup" key-handlers))

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
