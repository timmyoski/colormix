(ns colormix.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Functions

(def board-size 5)

(defn px-str [anumb]
  (str anumb "px"))

(def block {:size 100
            :margin 2
            :total-size (+ (:size block) (* (:margin block) 2))
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

(defn rand-color-num []
  [(rand-int 256)
   (rand-int 256)
   (rand-int 256)]
)

(defn new-board [n]
  (vec (repeat n (vec (repeat n (rand-color))))))


(defn nb [n]
  (vec(repeatedly n rand-color)))

(defn nb3 [n]
  (vec (for [x (range n)]
  (vec (for [x (range n)]
    (rand-color))))))

;; -------------------------
;; ALL HAIL THE ATOM

(defonce app-state
  (atom {:text "ColorMix... blend away your troubles"
         :board (nb3 board-size)
         }))

;; -------------------------
;; Views


(defn about-page []
  [:div [:h2 "About colormix"]
   [:div [:a {:href "/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

(defn get-xy [n]
  [(int (/ n board-size)) (mod n board-size)])

(defn get-block [n]
  (let [[x y] (get-xy n)]
    (get-in @app-state [:board x y])
  ))



(defn home-page []
  [:div [:h2 (:text @app-state)]
   [:div [:a {:href "/about"} "go to about page"]]
     [:div.content {:style {:max-width "600px"}}
      (for [x (range (* board-size board-size))]
          ^{:key x}
           [:div {:style {:background-color (get-block x)
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
           (get-block x)])
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
    (prn (:board @app-state))
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
