(ns au.net.ryansattler.main
  (:import
    (java.awt Color Dimension)
    (javax.swing JFrame JOptionPane JPanel)))

(def max-fps 30)
(def min-millis-per-frame (long (/ 1000 max-fps)))
(def window-width 800)
(def window-height 600)

(def wall-width 15)
(def path-width 15)
(def maze-size 25) ;odd number
(def maze-top-margin 100)
(def maze-left-margin 200)

(def player
  {:score 0})

(defstruct maze-cell :col :row :x :y :wall :horiz :vert)
(defn make-maze-cell [col row wall?] (struct maze-cell col row 
                                      (+ maze-left-margin (* wall-width col)) 
                                      (+ maze-top-margin (* wall-width row))
                                      wall?))

(defn initial-maze []
  (for [x (range maze-size) y (range maze-size)] 
    (make-maze-cell x y (or (zero? (rem x 2)) (zero? (rem y 2))))))

(defn gen-level []
  (initial-maze))

(def initial-gamestate {:mouseX 0
                        :mouseY 0
                        :levelnum 1
                        :level (gen-level)
                        :in-wall-piece nil})

(def images nil) ;map from item-image-type to render function? - need to render in right order too

(def color {:blue (Color. 0 61 245)
            :red  (Color. 245 61 0)
            :black (Color. 0 0 0)
            :background (Color. 255 255 255)})

(defn render-background [gfx] 
    (.setColor gfx (color :background))
    (.fillRect gfx 0 0 ( * 2 window-width) (* 2 window-height)))

(defn render-debug [gfx mouseX mouseY in-wall-piece]
  ;(println frame)
  (.setColor gfx (color :black))
  (.drawString gfx (str "X: " mouseX) 50 50)
  (.drawString gfx (str "Y: " mouseY) 50 75)
  (if in-wall-piece
    (.drawString gfx (str "Collided with piece at " (in-wall-piece :x) ", " (in-wall-piece :y)) 50 100)))

(defn render-level [gfx level]
  (doseq [maze-cell level]
    (if (:wall maze-cell)
        (do 
          (.setColor gfx (color :black))
          (.fillRect gfx (maze-cell :x) (maze-cell :y)  wall-width wall-width)))))
    


;(.fillRect gfx (:x maze-cell) (:y maze-cell) wall-width wall-width)))

(defn render [game window]
  (let [gfx (.getGraphics window)]
      (render-background gfx)
      (render-debug gfx (game :mouseX) (game :mouseY) (game :in-wall-piece))
      (render-level gfx (game :level))))

(defn get-mouseX []
  (.x (.getLocation (java.awt.MouseInfo/getPointerInfo))))

(defn get-mouseY []
  (.y (.getLocation (java.awt.MouseInfo/getPointerInfo))))

(defn in-wall-piece? [wall-piece mouseX mouseY]
  (let [wallX (wall-piece :x)
        wallY (wall-piece :y)]
    (and
      (wall-piece :wall)
      (>= mouseY wallY)
      (<= mouseY (+ wallY wall-width))
      (>= mouseX wallX)
      (<= mouseX (+ wallX wall-width)))))

(defn collided-piece [gamestate]
  (let [{:keys [mouseX mouseY level]} gamestate]
        (first (filter #(in-wall-piece? % mouseX mouseY) level))))

(defn update [game frame]
  (assoc game :in-wall-piece (collided-piece game)
              :mouseX (get-mouseX)
              :mouseY (get-mouseY)))
 
(defn current-time []
  (/ (java.lang.System/nanoTime) 1000000))

(defn configure-gui [window panel]
  (doto panel
    (.setPreferredSize (Dimension. window-width window-height))
    (.setFocusable true))
  (doto window
    (.add panel)
    (.pack)
    (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
    (.setVisible true)))

(let [window (JFrame. "You Can't Touch The Walls")
      panel (javax.swing.JPanel.)]
  (configure-gui window panel)
  (loop [gamestate initial-gamestate
         frame 1]
    (let [start-time (current-time)
          updated-gamestate (update gamestate frame)]
         (render gamestate window)
    (let [render-time (- (current-time) start-time)
          wait-time (max (- min-millis-per-frame render-time) 0)]
      (java.lang.Thread/sleep wait-time))
    (recur updated-gamestate (inc frame)))))