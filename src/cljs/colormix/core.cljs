(ns colormix.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))


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
;;ATOM
;;--------------------------
;;--------------------------
;;MOAR Functions

(defn px-str [anumb]
  (str anumb "px"))

(def block {:size (- (:total-size block) (* (:margin block) 2))
            :margin 2
            :total-size 100
            })

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
  )))

(defn get-all-sides [x y size]
  (let [last-index (- size 1)];readability
  (if (and (<= 0 x last-index) (<= 0 y last-index))
  (concat
    (map #(vector % y) (get-sides x size))
    (map #(vector x %) (get-sides y size))))))

(defn get-xy [n]
  [(int (/ n board-size)) (mod n board-size)])

(defn get-block [n]
  (let [[x y] (get-xy n)]
    (get-in @app-state [:board x y])
  ))


;; -------------------------
;; Views


(defn about-page []
  [:div [:h2 "About colormix"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;;---------------------------
;; Main view

(defn home-page []
  [:div {:class "react-container"} [:h2 (:text @app-state)]
   [:div {:class "jankynav"} [:a {:href "/about"} "go to about page"]]
     [:div {:class "content"
            :style {:max-width (str (* (:total-size block) board-size) "px")}}
      (doall (for [x (range (* board-size board-size))]
          ^{:key x}
           [:div {:style {:background-color (rgb-str (get-block x))
                          :color (rand-color)
                          :margin (px-str (:margin block))
                          :width (px-str (:size block))
                          :height (px-str (:size block))
                          :float "left"
                          :word-wrap "break-word"}
                  :class "colorbox"
                  :on-click (fn [e]
                              (prn (.remove (.-target e))))
                  }
           (rgb-str (get-block x))]))
            ;figure out destruct better and use to set color of block
    ]
   ])

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
  (mount-root))
