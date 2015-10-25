(ns nurikabe.core
  (:gen-class)
  ; (:require clojure.pprint)
  ; (:use clojure.pprint)
  )
(use 'alex-and-georges.debug-repl)


; Tile definitions
(def U nil) ; undefined
(def B 0)   ; path or road
(def W 1)   ; area or garden space


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
(def b puzzle-board)

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
     (vec (for  [x  (range 0 (board-row-count)),
                 y  (range 0 (board-column-count))]
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
                   (not (zero? (get-tile-value b [i j])))))
     all-tiles)))


(defn expansions-for-area
  "Expands given area in all possible directions by one tile"
  [area]
      ; Clean nils
       (remove nil?
        (reduce
          (fn [new-areas tile]
            (vec (concat
              new-areas
              (vec (map (fn [direction]
                          ; How to have this not concat nils
                          (if-not (some #{direction} area)
                           (conj area direction)))
                (possible-directions-for-tile tile)))
              )))
          []
          area)))


(defn summon-areas-for-tile
  ; Default value for areas is the tile itself
  ([board tile] (summon-areas-for-tile board tile [[tile]]))
  ([board tile areas]
  (if (= (count (first areas)) (get-tile-value board tile))
    areas
    (let [new-areas (vec
                       (reduce (fn [new-areas area] (concat new-areas (expansions-for-area area)))
                       []
                       areas))]
     (summon-areas-for-tile board tile new-areas)))))


(defn generate-all-possible-areas-for-board []
  (reduce
    (fn [result tile] (assoc result (keyword (str tile)) (summon-areas-for-tile b tile)))
    {}
    (get-numbered-tiles)
    )
  )



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
