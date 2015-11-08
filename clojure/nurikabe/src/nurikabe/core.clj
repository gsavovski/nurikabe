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
  [[U U 3 U U 1 U U 1 U]
   [U 3 U U U U 5 U U U]
   [U U U U U U U U U 3]
   [U U U U 2 U U U U U]
   [U U U U U U U U 3 U]
   [U 4 U U U U U U U U]
   [U U U U U 4 U U U U]
   [1 U U U U U U U U U]
   [U 1 U U 2 U U 1 U U]
   [U U U 1 U U U U 3 U]])

; 10 secs in ruby
(def sample8
   [[U U U U 2 U U U U 3 U]
    [U U U U U 2 U U U U U]
    [3 U U 4 U U U U U U 4]
    [U U U U U U U U 1 U U]
    [U U U U 2 U U 4 U U U]
    [2 U U 2 U U U U U U U]
    [U U U U 2 U U U 2 U U]
    [2 U U U U U U 2 U 1 U]
    [U U U 3 U U U U U U 1]
    [U U U U U U U U U 1 U]
    [4 U U U U U U 4 U U U]
  ])

; Current puzzle
; TODO: turn this into a global swappable atom
; (def b puzzle-board)
; (def b puzzle-board-gm-walker-anderson)
; (def b puzzle-board-tester)
; (def b gm-prasanna)
(def b sample8)

; Final solution board
(def sb (atom b))

(defn update-solution-board
  [[x y] value]
  (swap! sb assoc-in  [x y] value))


(def all-possible-areas (atom {}))

(defn add-areas-to-all-possible-areas
  [numbered-tile areas]
  (swap! all-possible-areas assoc (keyword (str numbered-tile)) areas))


(def all-groupings (atom {}))


(defn add-areas-to-all-groupings [tiles areas]
  (swap! all-groupings assoc (keyword (str tiles)) areas))


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

(defn get-numbered-tiles-greater-then-1 []
  (filter
    (fn [tile] (> (get-tile-value b tile) 1))
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
                   ; (nil? (get-tile-value board [i j]))
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


(defn cart-sets [colls]
  (if (empty? colls)
    #{#{}}
    (for [x (first colls)
          more (cart-sets (rest colls))]
      (set (cons x more)))))


; TODO: Try to make this return a set as top level type instead of
; ((APersistentMap$ValSeq))
(defn group-areas-by-combinations-of-n
  "DEPRECATED"
  [n]
  (let [groups (c/combinations (get-numbered-tiles) n)]
    (reduce
      (fn [groupings group]
        (assoc groupings group
               (cart-sets
                 (reduce
                   (fn [result num-tile]
                     (conj result ((keyword (str (vec num-tile))) (deref all-possible-areas))))
                   #{}
                   group))))
      {}
      groups)))


(defn populate-board-with
  [area]
  (reduce (fn [new-board [x y]]
            (let [value (max (get-tile-value b [x y]) 1)]
              (replace-tile new-board x y value)))
          @sb
          area))


(defn get-tile-print-value
  [board [x y]]
   (nth (nth board x) y))

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
    (doall
      ; TODO remove 0s from range
      (doseq  [x  (range (board-row-count)),
               y  (range (board-column-count))]
        (let [value (get-tile-print-value board [x y])]
          (do
            ; (println x y)
            (cond
              (= value 0) (do
                            ; green bckg
                            (print "\u001b[46m")
                            (print "  "))
              (= value nil) (do
                             ; ? bckg
                             (print "\u001b[41m")
                             (print "  ")
                             (print (str "\u001B[0m")))
              :else (do
                       ; white bckg
                       (print "\u001B[47m")
                       ; black font
                       (print "\u001B[30m")
                       ; Prepend white space on single digit values
                       (if (< (count (str value)) 2)
                         (print  "" value)
                         (print  value))))

                    (if (= y (- (board-column-count) 1)) (println  "\u001b[49m"))
            )))
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


(defn solution-board-total-areas-size []
  (let [num-tiles (get-numbered-tiles)]
    (reduce (fn [sum tile] (+ sum (get-tile-value b tile))) 0 num-tiles)))


; (defn traverse-path
;   ([board]
;    (let [path-tiles (get-path-tiles-for-board board)
;          starting-tile (first path-tiles)]
;      (traverse-path starting-tile, #{}, board)))

;   ([current-tile, path, board]
;    (let [path-tiles (get-path-tiles-for-board board)
;          all-dirs (set (all-directions-for-tile current-tile))
;          new-path (s/union path #{current-tile})
;          possible-dirs (s/difference (s/intersection all-dirs path-tiles) new-path)]
;      (do
;        (doseq [next-tile possible-dirs]
;          (try
;            (do
;              (println "calling traverse with next-tile " next-tile " path count "  (count new-path)  " possible dirs " possible-dirs)
;          (traverse-path next-tile new-path board))
;          (catch Exception e (debug-repl))))
;        new-path
;        ; (debug-repl)
;        )
;      )))


(defn traverse-path
  ([board]
   (let [path-tiles (get-path-tiles-for-board board)
         starting-tile (first path-tiles)]
     (traverse-path #{starting-tile} , #{}, board)))

  ([next-tiles, path, board]
   (let [path-tiles (get-path-tiles-for-board board)
         current-tile (first next-tiles)
         next-tiles (disj next-tiles current-tile)
         all-dirs (set (all-directions-for-tile current-tile))
         new-path (s/union path #{current-tile})
         possible-dirs (s/difference (s/intersection all-dirs path-tiles) new-path next-tiles)
         next-tiles (s/union next-tiles possible-dirs)]
     (if (empty? next-tiles)
       new-path
       (traverse-path next-tiles new-path board)))))


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

(defn path-continuous? [board]
    (= (count (traverse-path board)) (count (get-path-tiles-for-board board))))


(defn path-valid?
  [board]
  (and
    ; Check count only when all areas are populated, not for partial groupings
    ; (= (count (traverse-path board)) (correct-path-size))
    (path-continuous? board)
    (path-without-squares? board)))


(defn generate-possible-solutions []
  (set
    (let [all-areas (generate-all-possible-areas-for-board)
          possible-solutions (cart (vals all-areas))]
      possible-solutions)))


(defn possible-directions-within-area
  ([[x y]]
   (possible-directions-for-tile [x y] b))
  ([[x y] board]
   (let [all-tiles (all-directions-for-tile [x y])]
     (remove (fn [[i j]]
               (or (< i 0)
                   (< j 0)
                   (> i (- (board-row-count) 1))
                   (> j (- (board-column-count) 1))
                   (zero? (get-tile-value board [i j]))))
             all-tiles))))


(defn traverse-area
  [starting-tile board]
  (if (= 1 (get-tile-value b starting-tile))
    #{starting-tile}
    (loop [next-dirs #{starting-tile}
           current-area #{}]
      (if (= next-dirs nil)
        current-area
        (do
          (let [current-tile (first next-dirs)
                next-dirs (disj next-dirs current-tile)
                all-dirs (set (possible-directions-within-area current-tile board))
                area-dirs (set (filter (fn[tile] (> (get-tile-value board tile) 0)) all-dirs))
                new-area-dirs (s/union (s/difference area-dirs current-area) next-dirs)
                new-area (s/union current-area #{current-tile})]
            (if (empty? new-area-dirs)
              (recur nil new-area)
              (recur new-area-dirs new-area))))))))


(defn get-area-size-for-starting-tile
  [starting-tile board]
  (let [tile-value (get-tile-value board starting-tile)
        area (traverse-area starting-tile board)
        num-tiles (set (get-numbered-tiles))]
    (if (>  (count (s/intersection area num-tiles)) 1) 0 (count area))))


(defn no-coliding-areas?
  [board]
  (let [numbered-tiles (get-numbered-tiles)]
    (every?
      (fn[tile] (let [size-for-area (get-area-size-for-starting-tile tile board)]
                  (not= size-for-area 0)))
    numbered-tiles)))


(defn wrap-area-with-path
  [numbered-tile]
  (let [solution-board (deref sb)
        area-for-tile (traverse-area numbered-tile solution-board)]
    (doseq [tile area-for-tile]
      (let [dirs (possible-directions-for-tile tile solution-board) ]
        (doseq [d dirs]
        (update-solution-board d 0))))))


(defn get-numbered-tiles-for-completed-areas
  []
  (let [num-tiles (get-numbered-tiles)
        solution-board (deref sb)]
     (filter (fn [num-tile]
               (let [area-size (get-area-size-for-starting-tile num-tile solution-board)
                     area-for-tile (traverse-area num-tile solution-board)]
               (= area-size (get-tile-value solution-board num-tile))))
             num-tiles)))


(defn get-completed-areas
  []
  (let [completed-num-tiles (get-numbered-tiles-for-completed-areas)
        solution-board (deref sb)]
    (reduce
      (fn [completed-areas num-tile]
        (let [area-for-tile (traverse-area num-tile solution-board)]
          (s/union completed-areas area-for-tile)))
      #{}
      completed-num-tiles)))


(defn wrap-finished-areas-with-path
  []
  (let [num-tiles-completed (get-numbered-tiles-for-completed-areas)]
    (doseq [num-tile num-tiles-completed]
      (wrap-area-with-path num-tile))))


(defn clean-up-all-posible-areas
  []
  (let [num-tiles-completed (get-numbered-tiles-for-completed-areas)
        solution-board (deref sb)]
    (doseq [num-tile num-tiles-completed]
      (let [area-for-tile (traverse-area num-tile solution-board)]
        (add-areas-to-all-possible-areas num-tile #{area-for-tile})))))


(defn stack-areas-to-discover-steady-tiles
  [areas]
  (let [stacked (apply s/intersection
                       (map (fn[a] (merge-areas-into-one a)) areas))
        stacked-without-numbered-tiles (s/difference stacked (set (get-numbered-tiles)))
        numbered-tiles (s/intersection stacked (set (get-numbered-tiles)))]
    (doall
      (for [tile stacked-without-numbered-tiles]
        (do
          (update-solution-board tile 1)
          (clean-up-all-posible-areas))))))


(defn abs [n] (max n (- n)))

(defn span-area-size-n-for-tile
  "     x
      x x x
    x x 3 x x
      x x x
        x
  "
  [n [xx yy]]
  (let [zero-row-range (range (- 1 n) n)
        zero-row (reduce (fn [row y] (s/union row #{[0 y]})) #{} zero-row-range)]
    (set (map (fn [[x y]] [(+ x xx)(+ y yy)])
         (reduce
           (fn [diamond [x y]]
             (s/union (reduce
                        (fn [new-diamond i]
                          (s/union new-diamond
                                   #{[(+ x i) y] [(- x i) y]}))
                        #{}
                        (range  1 (abs (- (abs y) n))))
                      diamond))
           zero-row
           zero-row)))))

(defn span-area-within-board
  [area]
  (set
    (filter (fn [[x y]] (and (>= x 0) (>= y 0)))  area)))


(defn board-with-only-2-numbered-tiles
  [num-tile1 num-tile2]
  (let [empty-board
        (vec (repeat (board-row-count) (vec (replicate (board-column-count) 0))))
        val1 (get-tile-value b num-tile1)
        val2 (get-tile-value b num-tile2)
        b-tile1 (assoc-in empty-board num-tile1 val1)
        b-tile1-tile2 (assoc-in empty-board num-tile2 val2)]
    b-tile1-tile2))


(defn are-areas-inter-reachable?
  [num-tile1 num-tile2]
  (let [val1 (get-tile-value b num-tile1)
        val2 (get-tile-value b num-tile2)]
    (not (empty?
           (s/intersection                                    ;+1 to include touching areas
           (span-area-within-board (span-area-size-n-for-tile (+ 1 val1) num-tile1))
           (span-area-within-board (span-area-size-n-for-tile val2 num-tile2)))))))


(defn inter-reachable-groups-of-2
  []
  (let [all-groups-of-2 (c/combinations (get-numbered-tiles) 2)]
    (set
      (map #(set %1)
           (filter (fn [[num-tile1 num-tile2]]
                     (are-areas-inter-reachable? num-tile1 num-tile2))
                   all-groups-of-2)))))


(defn inter-reachable-group?
  "For each pair in pairs of interreachable areas
   calculate pair intersect group-tiles == pair,
   then union all pairs that satisfied above,
  finaly veirify if the union == group-tiles"
  [group-tiles]
  (let [groups-of-2 (inter-reachable-groups-of-2)
        groups-unioned (reduce
                         (fn [result pair-tiles] (if (= (s/intersection pair-tiles group-tiles) pair-tiles)
                                                   (s/union result pair-tiles)
                                                   (s/union result #{})))
                         #{}
                         groups-of-2)]

    (= (s/intersection group-tiles groups-unioned) group-tiles)))


"Stolen from a gist, but starred it to give credit"
(defn flatten-sets
  "Like flatten, but pulls elements out of sets instead of sequences."
  [v]
  (filter  (complement set?)
          (rest (tree-seq set? seq (set v)))))


(defn flatten-sets-one-level  [coll]
  "Flattens set of sets to one level of sets (e.g)
  from: #{#{[3 4]  [2 4]} #{#{[6 6] [6 5] [5 7] [5 6]} #{[8 4]  [7 4]}}}
  to:  #{#{[3 4] [2 4]} #{[6 6] [6 5] [5 7] [5 6]} #{[8 4] [7 4]}}"
  (reduce  (fn [r c] (if  (set? (first c)) (s/union r c) (s/union r #{ c})))
          #{}
          coll))


(defn cartesian-for-group-with-pre-existing-partial-cartesian
  [group]
  (let [n (count group)
        all-paired-group-keys (set (keys @all-groupings))
        all-partial-groups (set (c/combinations group (- (count group) 1)))
        all-partial-groups (set (map (fn [group] (keyword (str (set group)))) all-partial-groups))
        ; bla (if (= group  #{[7 7]  [8 3]  [0 9]  [10 0]  [7 0]}) (debug-repl))
        existing-partial-groups (s/intersection all-paired-group-keys all-partial-groups)
        partial-group (first existing-partial-groups)
        remaining-area (s/difference  group (read-string (name partial-group)) existing-partial-groups)]
    (do
      (map  #( flatten-sets-one-level %1)
           (cart-sets (s/union  #{ (partial-group @all-groupings)} #{ ((keyword (str (first (vec remaining-area)))) (deref all-possible-areas))}))))))


(defn cartesian-for-group-with-OUT-pre-existing-partial-cartesian
  [group]
  (cart-sets
    (reduce
      (fn [result num-tile]
        (conj result ((keyword (str (vec num-tile))) (deref all-possible-areas))))
      #{}
      group)))


(defn cartesian-for-group
  [group]
  (let [n (count group)]
    (if (= n 2)
      (cartesian-for-group-with-OUT-pre-existing-partial-cartesian group)
      (let  [all-paired-group-keys (set (keys @all-groupings))
             all-partial-groups (set (c/combinations group (- (count group) 1)))
             all-partial-groups (set (map (fn [group] (keyword (str (set group)))) all-partial-groups))
             bla (if (= group  #{[7 7]  [8 3]  [0 9]  [10 0]  [7 0]}) (debug-repl))
             existing-partial-groups (s/intersection all-paired-group-keys all-partial-groups)
             partial-group (first existing-partial-groups)]
        ;No pre existing cartesian for a partial group
        (if (nil? partial-group)
          (cartesian-for-group-with-OUT-pre-existing-partial-cartesian group)
          ;Existing cartesian for a part of the group already exists, so do not
          ;calculate all fresh and reuse the existing"
          (let [remaining-area (s/difference  group (read-string (name partial-group)) existing-partial-groups)]
          (cartesian-for-group-with-pre-existing-partial-cartesian group)))))))


(defn verify-grouped-solutions []
  (generate-all-possible-areas-for-board)
  (wrap-finished-areas-with-path)
  (do
    (doseq [n (range 2 (+ (count (get-numbered-tiles-greater-then-1)) 1))]
    ; (doseq [n (range 2 (+ (count (get-numbered-tiles)) 1))]

      (let [numbered-tiles (set (get-numbered-tiles))
            numbered-tiles-completed (set (get-numbered-tiles-for-completed-areas))
            numbered-tiles-not-completed (s/difference numbered-tiles numbered-tiles-completed)
            groups-of-n (map #(set %1) (c/combinations numbered-tiles-not-completed n))]

        (do
          (println " N: " n)
          (println "numbered-tiles-not-completed count: " (count numbered-tiles-not-completed))
          (println "numbered-tiles-not-completed tiles: " numbered-tiles-not-completed)
          ; (println "groups-of-n" groups-of-n)
          (wrap-finished-areas-with-path)
          (doseq [group groups-of-n]
            (if (or (inter-reachable-group? group))
              (let [group-tiles group
                    ; bla (println "Group  " group)
                    group-areas-without-completed (cartesian-for-group group)
                    bla (add-areas-to-all-groupings group group-areas-without-completed)

                    valid-areas-for-group (filter (fn [group-area]
                                                    (let [board-for-area (populate-board-with (merge-areas-into-one (vec group-area)))
                                                          path-valid (path-continuous? board-for-area)
                                                          no-coliding-areas (no-coliding-areas? board-for-area)]
                                                      (and path-valid no-coliding-areas)))
                                                  ((keyword (str group-tiles)) @all-groupings))

                    bla (add-areas-to-all-groupings group valid-areas-for-group)
                    stacked (try (stack-areas-to-discover-steady-tiles valid-areas-for-group) (catch Exception e (debug-repl)))]

                (doseq [group-area valid-areas-for-group]
                  (let [board-for-area (populate-board-with (merge-areas-into-one (vec group-area))) ]

                      (if (= n 5)
                    (do
                      (println " N: " n)
                      (println "Group: " group)
                      (println "Group areas before filtering: " (count group-areas-without-completed))
                      (println "Group areas after filtering: " (count valid-areas-for-group))
                      ; (println "Total combos for group" (count ((keyword (str group  )) @all-groupings)))
                      ; (println "Group area: " group-area)
                      (println)
                      (print-board board-for-area)
                      (println))

                    (if (and (= (solution-board-total-areas-size) (count (get-area-tiles-in-board board-for-area)))
                             (path-without-squares? board-for-area))
                      (println "SOLUTION")
                      )
                    )
                    )))) ))))

    (println "SOLUTION BOARD")
    (print-board (deref sb))))


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
; (print-stack-trace  (root-cause *e) 13)
; print dump of last N rows
; (print-stack-trace *e 5))
; print cause rows
; (print-cause-trace *e 3))
