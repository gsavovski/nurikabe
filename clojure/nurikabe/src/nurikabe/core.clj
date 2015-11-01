(ns nurikabe.core
  (:gen-class)
  ; (require [clojure.core.async :as async :refer  [>! <! >!! <!! go]])
  (require [clojure.set :as s]
           [clojure.math.combinatorics :as c]))

;Debugger tool
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
    [3 U U U U U U U U U U U U U U 3 U U U U U U U U U U U U U U U 3]
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


; 3 secs in ruby
(def gm-prasanna
  [[0 0 3 0 0 1 0 0 1 0]
   [0 3 0 0 0 0 5 0 0 0]
   [0 0 0 0 0 0 0 0 0 3]
   [0 0 0 0 2 0 0 0 0 0]
   [0 0 0 0 0 0 0 0 3 0]
   [0 4 0 0 0 0 0 0 0 0]
   [0 0 0 0 0 4 0 0 0 0]
   [1 0 0 0 0 0 0 0 0 0]
   [0 1 0 0 2 0 0 1 0 0]
   [0 0 0 1 0 0 0 0 3 0]])

; Current puzzle
; TODO: turn this into a global swappable atom
(def b puzzle-board)
; (def b puzzle-board-gm-walker-anderson)
; (def b puzzle-board-tester)
; (def b gm-prasanna)

; Final solution board
(def sb (atom b))

(defn update-solution-board
  [[x y] value]
  (swap! sb assoc-in  [x y] value))


(def all-possible-areas (atom {}))

(defn add-areas-to-all-possible-areas
  [numbered-tile areas]
  (swap! all-possible-areas assoc (keyword (str numbered-tile)) areas))

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
    (vec (for  [x  (range (board-row-count)),
                y  (range (board-column-count))]
           [x y]))
    ))

(defn get-path-tiles-for-board
  [board]
  (set (filter
         (fn [tile] (= (get-tile-value board tile) 0))
         (vec (for  [x  (range (board-row-count)),
                     y  (range (board-column-count))]
                [x y]))
         )))


(defn all-directions-for-tile
  [[x y]]
  (let [tiles (map (fn[[dx dy]] [(+ x dx) (+ y dy)]) DIRECTIONS)]
    tiles))


(defn possible-directions-for-tile
  ([[x y]]
   (possible-directions-for-tile [x y] b))
  ([[x y] board]
   (let [all-tiles (all-directions-for-tile [x y])]
     (remove (fn [[i j]]
               (or (< i 0)
                   (< j 0)
                   (> i (- (board-row-count) 1))
                   (> j (- (board-column-count) 1))
                   ; The tile is available
                   ; TODO: Create restricted board
                   ; To not allow tiles which border on area
                   ; Allow to move only to 'free' 0 tiles
                   (not (zero? (get-tile-value board [i j])))
                   ; (= (get-tile-value b [i j]) -1)
                   ))
             all-tiles))))


(defn expansions-for-area
  "Given an area, returns all possible
  expansions for it by one tile"
  [area board]
  (set
    (reduce
      (fn [new-areas tile]
        (s/union
          new-areas
          (set
            (keep (fn [direction]
                    (if-not (contains? area direction)
                      (s/union area #{direction})))
                  (possible-directions-for-tile tile board)))))
      #{}
      area)))


(defn replace-tile [board x y value]
  (assoc board x (assoc (nth board x) y value)))


(defn get-area-tiles-in-board [board]
  (filter
    (fn [tile] (pos? (get-tile-value board tile)))
    (vec (for  [x  (range (board-row-count)),
                y  (range (board-column-count))]
           [x y]))))


(defn create-restricted-board-for-tile [board tile]
  "Given a board with areas, surround all
  areas with '-1' restrictred tiles, because
  when discovering new areas we should not
  colide with existing areas"
  (reduce
    (fn [new-board tile]
      (reduce (fn [new-new-board [x y]]
                (replace-tile new-new-board x y -1))
              new-board
              (possible-directions-for-tile tile board)))
    board
    (remove #{tile} (set (get-area-tiles-in-board board)))))


(defn summon-areas-for-tile
  ; Default value for areas is the tile itself
  ([board tile] (summon-areas-for-tile board tile #{#{tile}}))
  ([board tile areas]
   (if (= (count (first areas)) (get-tile-value board tile))
     (set areas)
     (let [new-areas
           (reduce (fn [new-areas area] (s/union new-areas  (expansions-for-area area board)))
                   #{}
                   areas)]
       (summon-areas-for-tile board tile new-areas)))))


(defn generate-all-possible-areas-for-board []
  (reduce
    (fn [result tile]
      (let [areas  (summon-areas-for-tile (create-restricted-board-for-tile b tile) tile)]
      (do
        (add-areas-to-all-possible-areas tile areas)
        (assoc result (keyword (str tile)) areas))))
    {}
    (get-numbered-tiles)))


; http://stackoverflow.com/questions/18246549/cartesian-product-in-clojure
(defn cart [colls]
  (if (empty? colls)
    '(())
    (for [x (first colls)
          more (cart (rest colls))]
      (cons x more))))


(defn group-areas-by-combinations-of-n
  [n]
  (let [groups (c/combinations (get-numbered-tiles) n)]
    (reduce
      (fn [groupings group]
        (assoc groupings group
               (cart
                 #{
                   ((keyword  (str (first (vec group))))  (deref all-possible-areas))
                   ((keyword  (str (second (vec group))))  (deref all-possible-areas))})))
      {}
      groups)))


(defn populate-board-with
  [area]
  (reduce (fn [new-board [x y]]
            (let [value (max (get-tile-value b [x y]) 1)]
              (replace-tile new-board x y value)))
          b
          area))


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
                ; Prepend white space on single digit values
                (if (< (count (str value)) 2)
                  (print  "" value)
                  (print  value))))


            ; Set new line
            (if (= y (- (board-column-count) 1)) (println  "\u001b[49m"))))))
    ; Reset colors
    (print (str "\u001B[0m"))))


(defn merge-areas-into-one
  [areas]
  (reduce
    (fn[merged-area area] (s/union merged-area area))
    #{}
    areas))


(defn correct-path-size []
  (let [num-tiles (get-numbered-tiles)
        num-tiles-values (map (fn [tile] (get-tile-value b tile)) (vec num-tiles))
        num-tiles-sum (reduce + 0 num-tiles-values)
        total-tiles-count (* (board-row-count) (board-column-count))]
    (- total-tiles-count num-tiles-sum)))


(defn path-continuous?
  [board]
  (let [path-tiles (get-path-tiles-for-board board)]
    (reduce
      (fn[path tile] (let [all-dirs (set (all-directions-for-tile tile))]
                       (if (and (not= (s/intersection (s/difference path-tiles tile) all-dirs) #{})
                                (not (contains? path tile))

                                )
                         (s/union path #{tile})
                         path
                         )))
      #{}
      path-tiles)))

(defn path-without-squares?
  [board]
  (not-any?
    (fn[[x y]]
      (let [ square #{[x y]
                      [x (+ y 1)]
                      [(+ x 1) y]
                      [(+ x 1) (+ y 1)]}]
        (=
         (s/intersection
           (get-path-tiles-for-board board)
           square)
         square)))
    (vec (for  [x  (range (- (board-row-count) 1)),
                y  (range (- (board-column-count) 1))]
           [x y]))))


(defn path-valid?
  [board]
  (and
    (= (count (path-continuous? board)) (correct-path-size))
    (path-without-squares? board)))



(defn generate-possible-solutions []
  (set
    (let [all-areas (generate-all-possible-areas-for-board)
          possible-solutions (cart (vals all-areas))]
      possible-solutions)))


(defn solutions-with-correct-path
  [solutions]
  (keep
    (fn [solution] (path-valid? (populate-board-with (merge-areas-into-one (vec solution)))))
    solutions))


(defn print-correct-solutions []
  (let [all-solutions (generate-possible-solutions)]
    (vec (for [solution all-solutions]
           (let [solution-board (populate-board-with (merge-areas-into-one (vec solution)))]
             (if (path-valid? solution-board)
               (do
                 (println solution)
                 (println)
                 (print-board solution-board)
                 (println))))))))



(defn print-all-possible-solutions []
  (let [all-solutions (generate-possible-solutions)]
    (vec (for [solution all-solutions]
           (do
             (println solution)
             (println)
             (print-board (populate-board-with (merge-areas-into-one (vec solution))))
             (println))))))



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
            ))))))


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
    (print-correct-solutions)
    ))



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
