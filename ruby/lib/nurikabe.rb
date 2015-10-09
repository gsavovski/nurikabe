#
# Goran Savovski
#
# Algorithm to solve Nurikabe
#
# Started, August 2014 Macedonia, Europe
#

require 'pry'
require 'matrix'
require 'colorize'
require 'thor'
require './lib/models/tile'
require './lib/models/board'
require './lib/puzzle_boards'

include PuzzleBoards

#Make public elements setter methods
class Matrix
  public :"[]=", :set_element, :set_component

  def each_with_value
    (0...self.row_count).to_a.each do |i|
      (0...self.column_count).to_a.each do |j|
        yield(self[i,j],i,j)
      end
    end
  end
end

# Directional modifiers
MODIFIERS = [[0,1],[-1,0],[1,0],[0,-1]]

class Nurikabe
  attr_accessor :board

  def initialize(b_nl)
    @board = Board.new(b_nl)
  end

  def solve
    board.solve
  end
end

class MyCLI < Thor
  puts "Avaliable puzzle to solve"
  puts PuzzleBoards.constants
  desc "solve NAME", "solve puzzle by NAME"
  def solve(name)
    puts "Solving #{name}"
    puzzle = Nurikabe.const_get name
    Nurikabe.new(puzzle).board.solve #9sec
  end
end

MyCLI.start(ARGV)




# Solved Boards

# Nurikabe.new(Gm_prasanna).board.solve #3sec
# Nurikabe.new(Nikoli_6erka).solve #9sec
# Sample Pack by Andry Stewert (Nurikabe_Sample_Pack.pdf)
# Nurikabe.new(Sample10).board.solve #38sec
# Nurikabe.new(Sample8).board.solve #10sec
# Nurikabe.new(Sample6).board.solve #19sec
# Nurikabe.new(Sample9).board.solve #3min
# Nurikabe.new(Sample5).board.solve #25sec
# Nurikabe.new(Sample4).board.solve #43sec
# Nurikabe.new(Nikoli_10ka).board.solve  #2:08mins
# Nurikabe.new(Gm_walker_anderson).board.solve  #2:58mins
# Nurikabe.new(Gm_grant_fikes).board.solve #5:28mins
# Nurikabe.new(Nikoli_casty).board.solve #1922 sec

# Unsolved Boards

# Nurikabe.new(Sample7).board.solve   #chocks
# Nurikabe.new(Gm_tom_collyer_37ka).board.solve #hard,because 37?!?
# Nurikabe.new(Nikoli_miya).board.solve
# Nurikabe.new(Gm_grant_fikes2).board.solve
# Nurikabe.new(Concept_puzzles_10x10).board.solve

