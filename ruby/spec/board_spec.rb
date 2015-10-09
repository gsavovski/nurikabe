require 'rspec'
require './spec/spec_helper'
require 'pry'
require 'matrix'
# MUST DO: Why is matrix not loading correctly
# require "/Users/goran/.rubies/ruby-2.0.0-p247/lib/ruby/2.0.0/matrix"
require './lib/models/board'
require './lib/nurikabe'


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

describe Board do

  describe '#create_general_area_span_for_numbered' do

    it 'generates area for size 3' do
      sample_test_board_18x18[9,9] = 3
      b = Board.new sample_test_board_18x18
      area_span3 = b.create_general_area_span_for_numbered 3
      expect(area_span3).to eq [[0, -2], [0, -1], [1, -1], [-1, -1], [0, 0], [1, 0], [-1, 0], [2, 0], [-2, 0], [0, 1], [1, 1], [-1, 1], [0, 2]]
    end

  end

  context 'summon area' do

    it 'summons all areas for numbered tile of size 2' do
      sample_test_board_18x18[9,9] = 2
      b = Board.new sample_test_board_18x18
      areas_for_2 = b.summon_areas_within_restricted_solution_board([9,9], 2, sample_test_board_18x18)
      expect(areas_for_2.count).to eq 4
    end

    it 'summons all areas for numbered tile of size 3' do
      sample_test_board_18x18[9,9] = 3
      b = Board.new sample_test_board_18x18
      areas_for_3 = b.summon_areas_within_restricted_solution_board([9,9], 3, sample_test_board_18x18)
      expect(areas_for_3.count).to eq 18
    end

    it 'summons all areas for numbered tile of size 4' do
      sample_test_board_18x18[9,9] = 4
      b = Board.new sample_test_board_18x18
      areas_for_4 = b.summon_areas_within_restricted_solution_board([9,9], 4, sample_test_board_18x18)
      expect(areas_for_4.count).to eq 76
    end

    it 'summons all areas for numbered tile of size 5' do
      sample_test_board_18x18[9,9] = 5
      b = Board.new sample_test_board_18x18
      areas_for_5 = b.summon_areas_within_restricted_solution_board([9,9], 5, sample_test_board_18x18)
      expect(areas_for_5.count).to eq 315
    end

    it 'summons all areas for numbered tile of size 6' do
      sample_test_board_18x18[9,9] = 6
      b = Board.new sample_test_board_18x18
      areas = b.summon_areas_within_restricted_solution_board([9,9], 6, sample_test_board_18x18)
      expect(areas.count).to eq 1296
    end

    it 'summons all areas for numbered tile of size 7' do
      sample_test_board_18x18[9,9] = 7
      b = Board.new sample_test_board_18x18
      areas = b.summon_areas_within_restricted_solution_board([9,9], 7, sample_test_board_18x18)
      expect(areas.count).to eq 5320
    end

    #TODO: Tag with slow
    # it 'summons all areas for numbered tile of size 8' do
    #   sample_test_board_18x18[9,9] = 8
    #   b = Board.new sample_test_board_18x18
    #   areas = b.summon_areas_within_restricted_solution_board([9,9], 8, sample_test_board_18x18)
    #   expect(areas.count).to eq 21800
    # end

    # it 'summons all areas for numbered tile of size 9' do
    #   sample_test_board_18x18[9,9] = 9
    #   b = Board.new sample_test_board_18x18
    #   areas = b.summon_areas_within_restricted_solution_board([9,9], 9, sample_test_board_18x18)
    #   expect(areas.count).to eq 89190
    # end

    # it 'summons all areas for numbered tile of size 10' do
    #   sample_test_board_18x18[9,9] = 10
    #   b = Board.new sample_test_board_18x18
    #   areas = b.summon_areas_within_restricted_solution_board([9,9], 10, sample_test_board_18x18)
    #   expect(areas.count).to eq 364460
    # end
  end

  context "summon areas obstacled" do
    it 'summons all areas for very restricted/obstacled 5' do
      sample_test_board_18x18_restricted[9,9] = 5
      b = Board.new sample_test_board_18x18_restricted
      areas = b.summon_areas_within_restricted_solution_board([9,9], 5, sample_test_board_18x18_restricted)
      expect(areas.count).to eq 1
    end

  end

end
