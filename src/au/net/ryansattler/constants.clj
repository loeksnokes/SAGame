(ns au.net.ryansattler.constants)

(def debug false)

(def max-fps 100)
(def min-millis-per-frame (long (/ 1000 max-fps)))
(def window-width 850)
(def window-height 600)
(def end-screen-time 6000)
(def start-screen-time 3000) 

(def wall-width 16) ;pixels, should stay on 16 now that graphics are used
(def maze-size 25) ;odd number
(def maze-top-margin 100)
(def maze-left-margin 350)

(def initial-player-millis-per-move 75)
(def bomb-delay 500)
(def starting-bombs 2)
(def bombs-per-level 0)
(def num-bomb-pickups 2)
(def free-bomb-per 5000) 

(def initial-minotaur-millis-per-move 220)
(def minotaur-speed-up 0.92)
(def minotaur-start-delay 4000) 
 

(def num-treasures 8) 
(def treasure-score-constant 50) 

