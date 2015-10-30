(ns nurikabe.core-test
    (require [nurikabe.core :refer :all])
    (use midje.sweet))

(def test-board
   [[U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U 5 U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]])

(def populated-board
   [[1 U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U 5 U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U 1]])

(with-redefs [b test-board]

  ; Basic board values
  (fact (board-column-count) => 11)
  (fact (board-column-count) => 11)

  (fact (populate-board-with #{[0 0] [10 10] [5 5]}) =>
         populated-board )

  (fact (get-numbered-tiles) => '([5 5]))

  (fact (get-tile-value (replace-tile b 5 5 10) [5 5]) => 10)

)





