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

(with-redefs [b test-board
              sb (atom test-board)]

  ; Basic board values
  (fact (board-column-count) => 11)
  (fact (board-row-count) => 11)

  (fact (populate-board-with #{[0 0] [10 10] [5 5]}) =>
         populated-board )

  (fact (get-numbered-tiles) => '([5 5]))

  (fact (get-tile-value (replace-tile b 5 5 10) [5 5]) => 10))



(def test-board
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U 4]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]])

(def test-board-4size-diamond
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U 1]
   [U U U U U U U U U 1 1]
   [U U U U U U U U 1 1 1]
   [U U U U U U U 1 1 1 4]
   [U U U U U U U U 1 1 1]
   [U U U U U U U U U 1 1]
   [U U U U U U U U U U 1]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]])

(with-redefs [b test-board
              sb (atom test-board)]

  (fact (populate-board-with (span-area-within-board (span-area-size-n-for-tile 4 [5 10]))) =>
        test-board-4size-diamond))


(def test-board
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U 3 U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]])

(def test-board-3size-diamond
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U 1 U U U U U]
   [U U U U 1 1 1 U U U U]
   [U U U 1 1 3 1 1 U U U]
   [U U U U 1 1 1 U U U U]
   [U U U U U 1 U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]])


(with-redefs [b test-board
              sb (atom test-board)]

  (fact (populate-board-with (span-area-size-n-for-tile 3 [5 5])) =>
        test-board-3size-diamond))


(def test-group-diamond-board
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U 3 U U U U U U U]
   [U U U U U U U U U U U]
   [U 3 U U U 5 U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]])


(def test-group-diamond-board-created
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U 1 U 1 U U U U U]
   [U U 1 1 1 1 1 U U U U]
   [U 1 1 3 1 1 1 1 U U U]
   [1 1 1 1 1 1 1 1 1 U U]
   [1 3 1 1 1 5 1 1 1 1 U]
   [1 1 1 1 1 1 1 1 1 U U]
   [U 1 U 1 1 1 1 1 U U U]])


(with-redefs [b test-group-diamond-board
              sb (atom test-group-diamond-board)]

  (fact (get-numbered-tiles) => '([6 3]  [8 1]  [8 5]))

  (fact (populate-board-with (span-area-within-board (diamond-area-for-group #{[6 3] [8 1]  [8 5]})))
        => test-group-diamond-board-created))


(def test-group-diamond-board
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U 2 U U U U U U]
   [U U U U U U U U U U U]
   [U U U 3 U U U U 3 U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U 2 U U]
   [U U U U U U U U U U U]])


(def test-group-diamond-board-created
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U 1 U U U U U U]
   [U U U 1 2 1 U U 1 U U]
   [U U 1 1 1 U U 1 1 1 U]
   [U 1 1 3 1 1 1 1 3 1 1]
   [U U 1 1 1 U U 1 1 1 U]
   [U U U 1 U U U U 1 U U]
   [U U U U U U U 1 2 1 U]
   [U U U U U U U U 1 U U]])

(with-redefs [b test-group-diamond-board
              sb (atom test-group-diamond-board)]

  (fact (populate-board-with (span-area-within-board (diamond-area-for-group (set (get-numbered-tiles)))))
        => test-group-diamond-board-created))


(def test-groups-independent
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U 2 U U U U U U]
   [U U U U U U U U U U U]
   [U U U 3 U U U U 3 U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U 2 U U]
   [U U U U U U U U U U U]])


(def test-groups-independent-created
  [[U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U U U U U U U U]
   [U U U U 1 U U U U U U]
   [U U U 1 2 1 U U 1 U U]
   [U U 1 1 1 U U 1 1 1 U]
   [U 1 1 3 1 1 1 1 3 1 1]
   [U U 1 1 1 U U 1 1 1 U]
   [U U U 1 U U U U 1 U U]
   [U U U U U U U 1 2 1 U]
   [U U U U U U U U 1 U U]])


(with-redefs [b test-groups-independent
              sb (atom test-groups-independent)]

  (fact (get-numbered-tiles) => '([4 4] [6 3] [6 8] [9 8]))

  (fact (populate-board-with (span-area-within-board (diamond-area-for-group (set (get-numbered-tiles)))))
        => test-groups-independent-created)

  (fact (independent-groups? #{[4 4] [6 3]} #{[6 8] [9 8]})
        => false ))


(def test-groups-independent
   [[U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U 3 U U U U U U U]
    [U U U U U U U U U U U]
    [U U U 3 U U U U 2 U U]
    [U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U U U U U U 3 U U]
    [U U U U U U U U U U U]])


(def test-groups-independent-created
   [[U U U U U U U U U U U]
    [U U U U U U U U U U U]
    [U U U 1 U U U U U U U]
    [U U 1 1 1 U U U U U U]
    [U 1 1 3 1 1 U U U U U]
    [U U 1 1 1 U U U 1 U U]
    [U 1 1 3 1 1 U 1 2 1 U]
    [U U 1 1 1 U U U 1 U U]
    [U U U 1 U U U 1 1 1 U]
    [U U U U U U 1 1 3 1 1]
    [U U U U U U U 1 1 1 U]])


(with-redefs [b test-groups-independent
              sb (atom test-groups-independent)]

  (fact (get-numbered-tiles) => '([4 3] [6 3] [6 8] [9 8]))

  (fact (independent-groups? #{[4 3] [6 3]} #{[6 8] [9 8]})
        => true )

  (fact (populate-board-with (span-area-within-board (diamond-area-for-group (set (get-numbered-tiles)))))
        => test-groups-independent-created))
