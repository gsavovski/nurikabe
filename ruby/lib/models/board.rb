#  Goran's algorithm to solving nurikabe puzzle
#
#   I) Generate all possible areas for each numbered tile
#  II) Create unioned span area for each numbered tile to determine
#      all possible reachable tiles
# III) Intersect all unioned spans to determine tiles that can be
#      reached from multiple numbered tiles (e.i. possibly conficting
#      tiles)
#  IV) Form groups of numbered tiles that can reach the same intersected
#      tile
#   V) Prune groups so that the positioning of all numbered tile areas
#      don't intersect nor touch each other
#  VI)... TODO

require 'set'
require './lib/models/board_helpers'

class Board
  include BoardHelpers

  attr_reader :b, :b_nl,
    :row_count,
    :col_count

  def initialize(b_nl)
    @time_super_beginning = Time.now
    @visited_tiles = []
    @b_nl = b_nl
    @row_count = @b_nl.row_count
    @col_count = @b_nl.column_count
    # @starting_point = pick_a_random_starting_point
    @starting_point = [0,0]
    @x = @starting_point[0]
    @y = @starting_point[1]
    @numbered_tiles_hash = all_numbered_tiles.each_with_object({}) { |v,h| h[v.join('-')] = v }
    # Matrix of all coordinates as values
    @m = ::Matrix.build(@row_count,@col_count){|x,y| [x,y]}.to_a.flatten(1).to_set
    @all_numbered_tiles = all_numbered_tiles
  end

  def solve
    numbered_tiles_order = calculate_order

    solve_it_new_way numbered_tiles_order

    puts 'Finished'
  end

  def calculate_order
    all_area_spans = generate_all_area_spans

    groups = []
    shared_tiles_for_spans = {}
    new_world_sorting = {}

    sorted_numbered_tiles = Hash[all_numbered_tiles.map { |tile| [tile.join('-'), @b_nl[*tile]] }].sort_by{|k,v| v}.to_h

    sorted_numbered_tiles = sorted_numbered_tiles.keys.map{|a| a.split('-').map(&:to_i)}

    sorted_numbered_tiles.each do |num_tile|
      span_for_num_tile = all_area_spans[num_tile.join('-')]

      span_size = span_for_num_tile.count

      restricted_board = create_restricted_board num_tile

      influenced_area = ((span_for_num_tile - [num_tile]) & (area_tiles_for_board(restricted_board) - [num_tile]))
      influenced_size = influenced_area.count

      influenced_size_percent = ((influenced_size / span_size.to_f) * 100).round(2)
      influenced_size_percent = 100 if @b_nl[*num_tile] == 1
      influenced_size_percent = 70 if (@b_nl[*num_tile] == 2 && influenced_size_percent == 0)
      influenced_size_percent = 50 if (@b_nl[*num_tile] == 3 && influenced_size_percent == 0)

      all_closest_tiles = []

      influenced_area.each do |tile|
        tiles = neighboring_numbered_tiles_to(tile, restricted_board)
        all_closest_tiles <<  tiles unless tiles.empty?
      end

      all_closest_tiles = (all_closest_tiles.uniq).map{|a| a.flatten(1)} - [num_tile]

      formula1 =  (1/(@b_nl[*num_tile].to_f**2) * influenced_size_percent ).round(2)
      puts "tile: #{num_tile}, size #{@b_nl[*num_tile]}, span size: #{span_size}, influenced_size: #{influenced_size}, percent #{influenced_size_percent}, num_tiles: #{all_closest_tiles}, formula1: #{formula1}"
      new_world_sorting[num_tile.join('-')] = formula1

      num_tiles_for_restricted_board = area_tiles_for_board restricted_board

      shared_tiles_for_spans[num_tile.join('-')] = span_for_num_tile & num_tiles_for_restricted_board
    end

    sorted_numbered_tiles = Hash[all_numbered_tiles.map { |tile| [tile.join('-'), @b_nl[*tile]] }].sort_by{|k,v| v}.to_h
    new_world_sorting = Hash[new_world_sorting.sort_by{|k,v| v}.reverse]

    sorted_numbered_tiles.each do |tile_key,tile_size|
      new_world_sorting[tile_key] = tile_size
    end

    new_world_sorting
  end


  def solve_it_new_way sorted_tiles
    sorted_numbered_tiles = sorted_tiles
    # sorted_numbered_tiles = Hash[all_numbered_tiles.map { |tile| [tile.join('-'), @b_nl[*tile]] }].sort_by{|k,v| v}.to_h

    solution_board = create_initial_solution_board
    solution_board = populate_road_between_orthogonal_neighbors solution_board
    solution_board = populate_road_between_diagonally_touching_neighbors solution_board
    solution_board = populate_road_around_numbered_one_tiles sorted_numbered_tiles, solution_board

    print_board solution_board

    all_diamond_shape_spans = generate_all_area_spans

    all_numbered_tile_areas = {}
    @final_grouped_solutions = [[]]

    sorted_numbered_tiles.each do |tile_key,tile_size|
      tile = tile_key.split('-').map(&:to_i)
      restricted_board = create_restricted_board_from_solution_board tile, solution_board

      current_numbered_tile_areas = summon_areas_within_restricted_solution_board(tile,tile_size, restricted_board)
      # current_numbered_tile_areas = summon_areas_within_restricted_solution_board_iterative(tile,tile_size, restricted_board)

      puts "Tile: " + tile_key + ' num:' + tile_size.to_s + ', areas count ' + current_numbered_tile_areas.count.to_s
      all_numbered_tile_areas[tile_key] = current_numbered_tile_areas

      @final_solution_areas = @final_grouped_solutions.inject(:&)

      # Eliminate solutions with tiles that are different
      # from the determined ones
      numbered_tiles_for_final_solution = @final_solution_areas.map{|s| @numbered_tiles_hash.values & s}.flatten(1)

      solutions = current_numbered_tile_areas.uniq
      #Carthesian Product of @final_grouped_solutions with the current solutions
      @expanded_solutions = @final_grouped_solutions.product(solutions.map{|s| [s]}).map{|m| m.flatten(1).uniq}

      numbered_tiles_for_solution = @expanded_solutions.first.map{|s| @numbered_tiles_hash.values & s}.flatten(1)

      shared_numbered_tiles = numbered_tiles_for_solution & numbered_tiles_for_final_solution

      t_product_selection = Time.now


      reduction_time_hash = {independence: [], traversal: []}
      puts "Product solutions count " + @expanded_solutions.count.to_s
      skiped_count = 0
      @feasible_solutions = @expanded_solutions.select do |sol|

        t_initial = Time.now

        unless areas_independent?(sol, numbered_tiles_for_solution)
          skiped_count += 1
          reduction_time_hash[:independence] << (Time.now - t_initial)
          next
        else
          reduction_time_hash[:independence] << (Time.now - t_initial)
        end

        sol1 = sol.flatten(1)

        current_solution_board = add_solution_to_solution_board sol1, solution_board.clone

        starting_tile = pick_a_random_starting_point current_solution_board
        t_traverse_start = Time.now

        visited_tiles = traverse_road_new(starting_tile, current_solution_board)
        reduction_time_hash[:traversal] << (Time.now - t_traverse_start)

        path_connected = ( @m - visited_tiles.to_set).to_a.uniq.sort == (@all_numbered_tiles.sort | sol1).uniq.sort

        # puts 'Area independece time: ' + (Time.now - t_partial).to_s

        # If all numbered tiles included in solution, check road for squares
        if numbered_tiles_for_solution.count == @all_numbered_tiles.count
          !(path_has_squares? sol1) && path_connected
        else
          path_connected
        end
      end #@expanded_solutions each combinded groups solution

      puts "Time stats independence: #{(reduction_time_hash[:independence].inject(0.0) { |sum, el| sum + el } / reduction_time_hash[:independence].size).round(8)}  traversal:  #{(reduction_time_hash[:traversal].inject(0.0) { |sum, el| sum + el } / reduction_time_hash[:traversal].size).round(4)} "
      @final_grouped_solutions = @feasible_solutions.map{|s| s.map{|r| r.sort}.sort  }.uniq

      final_solution_tiles = @final_grouped_solutions.inject(:&).flatten(1)

      solution_board = add_solution_to_solution_board final_solution_tiles, solution_board.clone
      solution_board = add_road_around_area final_solution_tiles, solution_board.clone
      print_board solution_board
    end

    puts "Final solution count: " + @final_grouped_solutions.count.to_s
    @final_grouped_solutions.each do |sol|
      print_board (create_solution_board(sol.flatten(1)))
    end
  end


  def summon_areas_within_restricted_solution_board(numbered_tile,size,all_areas=[[numbered_tile]],restricted_solution_board)
    # return all_areas.map{|a| a.sort}.sort.uniq if all_areas[0].count >= size
    return all_areas if all_areas[0].count >= size

    new_areas = []
    all_areas.each do |area|
      area.each do |x,y|
        directions = MODIFIERS.map do |m|
          m_x = m[0] + x
          m_y = m[1] + y
          [m_x,m_y]
        end
        # Remove reached tile if outside of board
        directions.delete_if{|i,j| i < 0 || j < 0 || i > (@row_count-1) || j > (@col_count-1) || (restricted_solution_board[i,j] != nil) }
        # directions.delete_if{|i,j| i < 0 || j < 0 || i > 9 || j > 9 }
        # Add each valid direction to the area
        directions.each do |a,b|
          next if area.include? [a,b]
          new_area = area + [[a,b]]
          (new_areas << new_area) if (restricted_solution_board[a,b] == nil)
        end
      end
    end

    # recursive action
    summon_areas_within_restricted_solution_board(numbered_tile,size,new_areas.map{|a| a.sort}.sort.uniq, restricted_solution_board)
  end

  # Recursive approach to traverse the road tiles
  # next_field is initialy the starting tile
  # vt visited tiles so far
  # solution is the solution in test
  def traverse_road(next_field,vt=Set.new,solution)
    @visited_tiles = vt
    avail_pos = available_ways_to_go_to(next_field,solution).to_set - @visited_tiles

    @visited_tiles << next_field
    avail_pos.each do |nf|
      if ((solution[nf[0],nf[1]] == 0) && (!@visited_tiles.include? nf))
        # print_board create_solution_board @visited_tiles
        traverse_road(nf,@visited_tiles,solution)
      end
    end
    @visited_tiles
  end

  def road_connected?(next_field,vt=Set.new,solution_board)
    visited_tiles = vt
    avail_pos = available_ways_to_go_to_new(next_field,solution_board).to_set - visited_tiles
    visited_tiles << next_field
    avail_pos.each do |nf|
      if ((solution_board[nf[0],nf[1]].to_i == 0) && (!visited_tiles.include? nf))
        road_connected?(nf,visited_tiles,solution_board)
      end
    end
    visited_tiles
  end

  def traverse_road_new(next_field,vt=Set.new,solution_board)
    visited_tiles = vt
    avail_pos = available_ways_to_go_to_new(next_field,solution_board).to_set - visited_tiles
    visited_tiles << next_field
    avail_pos.each do |nf|
      # if ((solution_board[nf[0],nf[1]] == nil) && (solution_board[nf[0],nf[1]] == 0) && (!visited_tiles.include? nf))
      if ((solution_board[nf[0],nf[1]].to_i == 0) && (!visited_tiles.include? nf))
        traverse_road_new(nf,visited_tiles,solution_board)
      end
    end
    visited_tiles
  end

  # Debuging usefull
  # @gp.each do |gk,solutions|
  #   # puts 'group: ' + gk + '  => ' + solutions.count.to_s + ' count'
  #   solutions.each do |sol|
  #     print_board(create_solution_board(sol.to_a.map{|s| s.to_a}.flatten(1)))
  #   end
  # end
end
