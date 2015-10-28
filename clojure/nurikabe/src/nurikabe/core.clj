(ns nurikabe.core
  (:gen-class)
  ; (:require clojure.pprint)
  ; (:use clojure.pprint)
  (require  [clojure.set :as s])
  )
(use 'alex-and-georges.debug-repl)


; Tile definitions
(def U nil) ; undefined
(def B 0)   ; path or road
(def W 1)   ; area or garden space



(def puzzle-board-gm-walker-anderson
   [[U U U U U U U U U U]
    [U U 4 U 11 U U U U U]
    [U 5 U U U 3 U U U U]
    [U U U U U U U U U U]
    [U U U U U 2 U U U U]
    [U U U U 3 U U U U U]
    [U U U U U U U U U U]
    [U U U U 2 U U U 8 U]
    [U U U U U 11 U 1 U U]
    [U U U U U U U U U U]]
)

; 32x32
(def puzzle-board-tester
   [[U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U 10 U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]
    [U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U U]]
)


(def puzzle-board
  "Simple nurikabe puzzle"
  [[3 U 1 U 2]
   [U U U U U]
   [U U 1 U U]
   [U U U U 2]
   [2 U U U U]])


(def puzzle-board-solution
  "Simple nurikabe puzzle solution"
  [[3 0 1 0 2]
   [1 0 0 0 1]
   [1 0 1 0 0]
   [0 0 0 1 2]
   [2 1 0 0 0]])

; Current puzzle
; TODO: turn this into a global swappable atom
; (def b puzzle-board)
; (def b puzzle-board-gm-walker-anderson)
(def b puzzle-board-tester)

(defn get-tile-value
  [board [x y]]
  (let [value (nth (nth board x) y)]
     (if-not (nil? value) value 0)))


(def DIRECTIONS
  [[0 1] [0 -1] [1 0] [-1 0]])


(defn board-row-count []
  (count b))


(defn board-column-count []
  (count (first b)))


 ; TODO: explore walk
; (use 'clojure.walk :only  [prewalk])
; (prewalk #(if  (number? %)  (inc %) %) matrix)
; =>  [[2 3 4]  [5 6 7]  [8 9 10]]


(defn get-numbered-tiles []
   (filter
     (fn [tile] (pos? (get-tile-value b tile)))
     (vec (for  [x  (range (board-column-count)),
                 y  (range (board-row-count))]
           [x y]))
    ))


(defn all-directions-for-tile
 [[x y]]
 (let [tiles (map (fn[[dx dy]] [(+ x dx) (+ y dy)]) DIRECTIONS)]
  tiles))


(defn possible-directions-for-tile
  [[x y]]
  (let [all-tiles (all-directions-for-tile [x y])]
    (remove (fn [[i j]]
              (or (< i 0)
                  (< j 0)
                  (> i (- (board-row-count) 1))
                  (> j (- (board-column-count) 1))
                   ; The tile is available
                   ; TODO: Create restricted board
                   ; To not allow tiles which border on area
                  ; (not (zero? (get-tile-value b [i j])))
                   ))
     all-tiles)))


(defn expansions-for-area
  "Expands given area in all possible directions by one tile"
  [area]
      (set
      ; Clean nils
       (remove nil?
         (reduce
            (fn [new-areas tile]
                (s/union
                  new-areas
                  (set (map (fn [direction]
                           ; How to have this not concat nils
                            (if-not (some #{direction} area)
                           (conj area direction)))
                   (possible-directions-for-tile tile)))))
          #{}
          area
          ))))


(defn summon-areas-for-tile
  ; Default value for areas is the tile itself
  ([board tile] (summon-areas-for-tile board tile [[tile]]))
  ([board tile areas]
  (do
    (println "Area count: " (count (first areas)))
  (if (= (count (first areas)) (get-tile-value board tile))
    areas
    (let [new-areas (vec
                       (reduce (fn [new-areas area] (doall (concat new-areas (expansions-for-area area))))
                       []
                       areas))]
     (summon-areas-for-tile board tile new-areas))))))


(defn summon-areas-for-tile-into-set
  ; Default value for areas is the tile itself
  ([board tile] (summon-areas-for-tile-into-set board tile #{#{tile}}))
  ([board tile areas]
  (do
    (println "Total Area count: " (count areas))
    (println "Area count: " (count (first areas)))
    ; (println "NEW Areas : "  areas)
    (println "NEW Areas class : "  (class areas))
  (if (= (count (first areas)) (get-tile-value board tile))
    areas
    (let [new-areas
                 (reduce (fn [new-areas area] (s/union new-areas  (expansions-for-area area)))
                 #{}
                 areas)]
     (summon-areas-for-tile-into-set board tile new-areas))))))




(defn generate-all-possible-areas-for-board []
  (reduce
    (fn [result tile] (assoc result (keyword (str tile)) (summon-areas-for-tile b tile)))
    {}
    (get-numbered-tiles)
    )
  )

; Improved
; TODO: Remove the bottom one
; (defn populate-board-with
;   [area]
;     (reduce (fn [new-board [x y]]
;                (let [value (if (> (get-tile-value b [x y]) 1)  (get-tile-value b [x y]) 1) ]
;                 (assoc new-board x (assoc  (nth new-board x) y value)))
;                 )
;      b
;      area
;     ))


(defn populate-board-with
  [area]
    (reduce (fn [new-board [x y]]
             (if (some #{[x y]} area)
               (let [value (if (> (get-tile-value b [x y]) 1)  (get-tile-value b [x y]) 1) ]
                (assoc new-board x (assoc  (nth new-board x) y value)))
                (assoc new-board x (assoc  (nth new-board x) y (get-tile-value b [x y])))
                ))
     b
     ;TODO: change order of y and x
     (for  [y (range (board-row-count)) x (range (board-column-count))]  [x,y])
    ))

; Some ASCII ESC color codes
; reset color
;(print (str "\u001B[0m"))
; background 40 - 47, 48 reserved, 49 default
; foreground 30 - 37, 38 reserved, 39 default
; white background color
;(print (str "\u001B[47m"))
; black background color
;(print (str "\u001B[49m"))
 ; bold 1 , bold off 21
; black foreground color
;(print (str "\u001B[30m"))
(defn print-board
  [board]
      (do
      (doall
        ; TODO remove 0s from range
        (for  [x  (range (board-row-count)),
               y  (range (board-column-count))]
        (let [value (get-tile-value board [x y])]
           (do
             (if (= value 0)
               (do
                 ; green bckg
                 (print "\u001b[46m")
                 (print "  "))
               (do
                 ; white bckg
                 (print "\u001B[47m")
                 ; black font
                 (print "\u001B[30m")
                 (print  "" value)))


             ; Set new line
             (if (= y (- (board-column-count) 1)) (println  "\u001b[49m"))))))
      ; Reset colors
      (print (str "\u001B[0m"))
      )
)


(defn print-all-areas
  []
  (let [all-areas (generate-all-possible-areas-for-board)]
   (doseq  [[num-tile areas] all-areas]
     (doall
       (for [area areas]
         (do
         (println "Tile: " (name num-tile)  " Area: " area)
         (print-board (populate-board-with area))
         (println)
        ))
     )
   )
  ))


; For Fancy in place bash printing
; Checkout:
; man 5 terminfo
; tput cuu 4 goes up 4 lines
; tput el clears to end of line
; function clearLastLine ()  {
;   tput cuu 1 && tput el
;     }


(defn -main
  "The beggining of a Nurikabe Solver"
  [& args]
  (do
  (println "Hello, Nurikabe!")
  (println "These are all areas for tile [0 0]")
  (println (summon-areas-for-tile puzzle-board [0 0]))))



; Some usefull learning Clojure Snippets
; TODO: Move out eventually

; Print current directory
; (System/getProperty "user.dir")

; Load file into repl
; (load-file "src/nurikabe/core.clj")
;
; (use ’nurikabe.core :reload)

; refresh namespace
(comment
 (do (use '[clojure.tools.namespace.repl :only (refresh)]) (refresh))
 )

; Repl debugger
; (use 'alex-and-georges.debug-repl)
; (debug-repl)
; Show all locals with
; *locals
; Continue with
; ()

;; refresh lein profiles
; lein clean
; lein deps
; lein run

; stack trace
; (use 'clojure.stacktrace)

; print root cause
; (print-stack-trace  (root-cause *e) 3)
; print dump of last N rows
; (print-stack-trace *e 5))
; print cause rows
; (print-cause-trace *e 3))
