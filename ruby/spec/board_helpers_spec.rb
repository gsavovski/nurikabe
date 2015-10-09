require 'rspec'
require './spec/spec_helper'
require 'pry'
require 'matrix'
# MUST DO: Why is matrix not loading correctly
# require "/Users/goran/.rubies/ruby-2.0.0-p247/lib/ruby/2.0.0/matrix"
require './lib/models/board'
require './lib/nurikabe'
require './lib/models/board_helpers.rb'


sample_test_board_18x18_restricted = Matrix[
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,  1,  1,  1,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,  1,  5,  1,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,  1,nil,  1,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,  1,nil,  1,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,  1,nil,  1,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,  1,nil,  1,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
]

sample_test_board_18x18 = Matrix[
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,9,  nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
  [nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil,nil],
]


describe Board do

  context 'summon area' do

   it 'summons all areas for numbered tile of size 2' do
      sample_test_board_18x18[9,9] = 2
      b = Board.new sample_test_board_18x18
      areas_for_2 = b.summon_areas_within_restricted_solution_board_iterative([9,9], 2, sample_test_board_18x18)
      expect(areas_for_2.count).to eq 4
    end

    it 'summons all areas for numbered tile of size 6' do
      sample_test_board_18x18[9,9] = 6
      b = Board.new sample_test_board_18x18
      areas = b.summon_areas_within_restricted_solution_board_iterative([9,9], 6, sample_test_board_18x18)
      expect(areas.count).to eq 1296
    end

  end
end
