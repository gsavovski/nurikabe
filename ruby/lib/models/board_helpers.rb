module BoardHelpers

  def add_road_around_area area, solution_board
    m_temp = *solution_board

    area.each do |x,y|

      up = [x-1,y]
      down = [x+1,y]
      left = [x,y-1]
      right = [x,y+1]

      directions = [up,right,down,left].reject{|a,b| ((a < 0) || (b < 0) || (a > @row_count-1) || (b > @col_count-1))}

      directions.each do |i,j|
        m_temp[i][j] = 0 if m_temp[i][j] == nil
      end

    end
    Matrix[*m_temp]
  end

  def neighboring_numbered_tiles_to tile, solution_board
    x,y = tile[0], tile[1]
    up = [x-1,y]
    down = [x+1,y]
    left = [x,y-1]
    right = [x,y+1]

    directions = [up,right,down,left].reject{|a,b| ((a < 0) || (b < 0) || (a > @row_count-1) || (b > @col_count-1))}

    directions = directions.select do |i,j|
      @b_nl[i,j].to_i > 1
    end

    directions
  end

  def add_solution_to_solution_board solution, sol_board
    solution.each do |tile|
      begin
      sol_board[*tile] = 1 unless all_numbered_tiles.include? tile
      rescue
        binding.pry
      end
    end
    sol_board
  end

  def create_restricted_board_from_solution_board(without_field,solution_board)
    m_temp = *solution_board

    all_numbered_tiles.each do |x,y|
      next if ((x == without_field[0]) && (y == without_field[1]))
      up = [x-1,y]
      down = [x+1,y]
      left = [x,y-1]
      right = [x,y+1]

      directions = [up,right,down,left].reject{|a,b| ((a < 0) || (b < 0) || (a > @row_count-1) || (b > @col_count-1))}

      directions.each do |i,j|
        m_temp[i][j] = 0 if m_temp[i][j] == nil
      end
    end

    Matrix[*m_temp]
  end


  def populate_road_around_numbered_one_tiles sorted_numbered_tiles, solution_board
    numbered_ones = sorted_numbered_tiles.select{|k,v| v == 1}
    numbered_ones.keys.each do |tile_key|
      x,y = tile_key.split('-').map(&:to_i)
      directions = MODIFIERS.map do |m|
        m_x = m[0] + x
        m_y = m[1] + y
        [m_x,m_y]
      end
      #remove outside boundry tiles
      directions.delete_if{|i,j| i < 0 || j < 0 || i > (@row_count-1) || j > (@col_count-1)  }

      directions.each do |tile|
        solution_board[*tile] = 0 if solution_board[*tile] == nil
      end
    end
    solution_board
  end

  def generate_all_area_spans
    all_area_spans = {}
    all_numbered_tiles.each do |num_tile|
      all_area_spans[num_tile.join('-')] = area_span_for_numbered_tile(num_tile)
    end
    all_area_spans
  end

  def populate_road_between_diagonally_touching_neighbors solution_board
    (1..@row_count-2).to_a.each do |i|
      (1..@col_count-2).to_a.each do |j|
        [[[i,j], [i+1,j+1], [i,j+1], [i+1,j]],
         [[i,j], [i+1,j-1], [i,j-1], [i+1,j]]].each do |tile_a,tile_b,tile_c,tile_d|
          if solution_board[*tile_a].to_i != 0 && solution_board[*tile_b].to_i != 0
            solution_board[*tile_c] = 0 if solution_board[*tile_c] == nil
            solution_board[*tile_d] = 0 if solution_board[*tile_d] == nil
          end
        end
      end
    end
    solution_board
  end

  def populate_road_between_orthogonal_neighbors solution_board
    (0..@row_count-2).to_a.each do |i|
      (0..@col_count-2).to_a.each do |j|
        [ [[i,j],[i,j+1],[i,j+2]] , [[i,j],[i+1,j],[i+2,j]]].each do |tile_a,tile_b,tile_c|
          if solution_board[*tile_a].to_i != 0 && solution_board[*tile_b] == nil && solution_board[*tile_c].to_i != 0
            solution_board[*tile_b] = 0
          end
        end
      end
    end
    solution_board
  end

  def create_initial_solution_board
    solution_board = Matrix.build(@row_count,@col_count){|x,y| nil}
    #populate solution board with the numbered tiles
    all_numbered_tiles.each do |tile|
      solution_board[*tile] = @b_nl[*tile]
    end
    solution_board
  end

  # all_possible_areas is a hash with keys
  # for each numbered tile and values of array
  # of arrays for all possible positioning for
  # the numbered tile
  def generate_possible_areas_for_all_numbered_tiles
    t = Time.now
    puts 'Start: Generate possible areas for all numbered tiles'
    all_possible_areas = {}

    # @b_nl.each_with_value do |value, i, j|
    #   if value != 0 then
    #     restricted_board = create_restricted_board [i,j]
    #     t_area = Time.now
    #     next if [i,j] == [7,1]
    #     puts "Summoning for [#{i},#{j}]"
    #     a = summon_areas([i,j], value, restricted_board)

    #     puts "Summoned area for [#{i},#{j}], time: " + (Time.now - t_area).to_i.to_s + 's size ' +  a.count.to_s
    #     all_possible_areas["#{i}-#{j}"] = a
    #   end
    (0..@row_count-1).to_a.each do |i|
      (0..@col_count-1).to_a.each do |j|
        # Summon for each numbered tile(non zero)
        if @b_nl[i,j] != 0 then
          restricted_board = create_restricted_board [i,j]
          t_area = Time.now
          next if [i,j] == [7,1]
          puts "Summoning for [#{i},#{j}]"
          a = summon_areas([i,j],@b_nl[i,j], restricted_board)

          puts "Summoned area for [#{i},#{j}], time: " + (Time.now - t_area).to_i.to_s + 's size ' +  a.count.to_s
          all_possible_areas["#{i}-#{j}"] = a
        end
      end
    end
    puts 'Finish: Generate possible areas for all numbered tiles, time: ' + (Time.now - t).to_i.to_s + 'seconds'
    all_possible_areas["7-1"] = [[[]]]
    all_possible_areas
  end

  # General compased diamond span with [0,0] in the middle
  # so that it translatable anywhere on the board
  def create_general_area_span_for_numbered size
    area_span = []
    row_zero = [[0,0]]
    n = size + (size - 1)
    m = ::Matrix.build(n,n){|x,y| [x,y]} #.to_a.flatten(1).to_set
    (1...size).each do |i|
      row_zero << [0,i]
      row_zero << [0,-i]
    end

    row_zero.sort.each do |y,x|
      # Bellow is the trick for the diagonal
      # on the diamond
      (0..(x.abs-size+1).abs).each do |i|
        area_span << [y+i,x]
        area_span << [y-i,x]
      end
    end

    area_span.uniq
  end

  # Create diamond shaped area span for numbered tile
  # By translating the general span on to a specific tile
  def area_span_for_numbered_tile tile
    area_span = create_general_area_span_for_numbered @b_nl[tile[0],tile[1]]
    area_span.map!{|x,y| [(x+tile[0]), (y+tile[1])] }
    area_span.reject!{|x,y| x < 0 || y < 0 || x > (@row_count-1) || y > (@col_count-1)}
    area_span
  end

  def summon_areas_within_restricted_solution_board_iterative(numbered_tile,size,restricted_solution_board)
    return [[numbered_tile]] if size == 1

    areas = [[numbered_tile]]

    while true do

      new_areas = []

      areas.each do |area|


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
      binding.pry if new_areas.empty?
      areas = new_areas.map{|a| a.sort}.sort.uniq
      break if areas[0].count >= size

    end #while

    areas.map{|a| a.sort}.sort.uniq
  end

  # Creates an array of all possible valid areas
  # for a numbered tile (recursive solution)
  # Initially all_areas is set to the numbered tile
  # size is the number on the numbered tile (i.e. how
  # many tiles has the area)
  def summon_areas(numbered_tile,size,all_areas=[[numbered_tile]],restricted_board)
    return all_areas.uniq if all_areas[0].count >= size
    new_areas = []
    all_areas.each do |area|
      area.each do |x,y|
        directions = MODIFIERS.map do |m|
          m_x = m[0] + x
          m_y = m[1] + y
          [m_x,m_y]
        end
        # Remove reached tile if outside of board
        directions.delete_if{|i,j| i < 0 || j < 0 || i > (@row_count-1) || j > (@col_count-1) || (restricted_board[i,j] != 0) }
        # directions.delete_if{|i,j| i < 0 || j < 0 || i > 9 || j > 9 }
        # Add each valid direction to the area
        directions.each do |a,b|
          next if area.include? [a,b]
          new_area = area + [[a,b]]
          (new_areas << new_area) if (@b_nl[a,b] == 0)
        end
      end
    end

    # recursive action
    summon_areas(numbered_tile,size,new_areas.uniq, restricted_board)
  end

  # MAYBE DO: Possible obsolete method because areas
  # consider colision during during spaning by considering
  # the restricted board during the creation.
  def eliminate_coliding_areas all_possible_areas
    puts 'All areas initial: ' + all_possible_areas.values.map{|areas| areas.count }.inspect

    all_possible_areas_reduced = {}

    # Reduce areas by eliminiting the ones which touch on another
    # numbered tile
    all_possible_areas.keys.each do |numbered_tile|
      x,y = numbered_tile.split('-').map(&:to_i)
      # Create board with compased tiles on all numbered tiles minus the
      # numbered tile
      restricted_board = create_restricted_board([x,y]).to_a
      all_compased_numbered_tiles = []
      (0..(@row_count-1)).to_a.each do |i|
        (0..(@col_count-1)).to_a.each do |j|
          all_compased_numbered_tiles << [i,j] if restricted_board[i][j] == 1
        end
      end

      all_areas_for_numbered_tile = all_possible_areas[numbered_tile].dup

      all_possible_areas_reduced[numbered_tile] = all_areas_for_numbered_tile.delete_if do |area|
        next if area.count == 1
        # Skip area, hmm do I need this bellow
        # next if (@b_nl[x,y] == 1)
        # Remove area if colinding
        area & all_compased_numbered_tiles != []
      end
    end #for each numbered_tile,area_span

    puts 'All numbered tile areas reduced(not coliding other numbered tiles)' + all_possible_areas_reduced.values.map{|areas| areas.count }.inspect
    all_possible_areas_reduced
  end

  def unioned_spans_for_numbered_tiles all_possible_areas
    total_possible_span_for_numbered_tile = {}
    all_possible_areas.each do |numbered_tile,possible_areas|
      total_possible_span_for_numbered_tile[numbered_tile] = possible_areas.flatten(1).uniq
    end
    total_possible_span_for_numbered_tile
  end

  def path_has_squares? solution
    solution_board = create_solution_board solution
    is_square = false
    (0..@row_count-2).to_a.each do |i|
      break if is_square
      (0..@col_count-2).to_a.each do |j|
        is_square = [[i,j],[i,j+1],[i+1,j],[i+1,j+1]].map{|tile| solution_board[*tile]  }.uniq == [0]
        break if is_square
      end
    end
    is_square
  end

  # Takes the initial board minus the field in question
  # and for all numbered tiles' neighbours fills in compass
  # sides with 1s(road tiles)
  # Usefull for finding if area sticks to another
  # numbered tile
  def create_restricted_board(without_field)
    m_temp = *@b_nl

    all_numbered_tiles.each do |x,y|
      next if ((x == without_field[0]) && (y == without_field[1]))
      up = [x-1,y]
      down = [x+1,y]
      left = [x,y-1]
      right = [x,y+1]

      numbered_fields = [up,right,down,left].reject{|a,b| ((a < 0) || (b < 0) || (a > @row_count-1) || (b > @col_count-1))}.uniq + [[x,y]]

      numbered_fields.each do |i,j|
        begin
          m_temp[i][j] = 1
        rescue
          binding.pry
          raise
        end
      end
    end

    Matrix[*m_temp]
  end

  def all_numbered_tiles
    all = []

    @b_nl.each_with_value do |value, i, j|
      all << [i,j] if value != 0
    end
    all
  end

  def all_numbered_ones
    all = []
    @b_nl.each_with_value do |value, i, j|
      all << [i,j] if value == 1
    end

    all
  end

  def area_tiles_for_board board
    all = []
    board.each_with_value do |value, i, j|
      all << [i,j] if value != 0 && value != nil
    end
    all
  end

  # for array of areas, create a hash that maps
  # the numbered tiles as keys to the area (array of tiles for that numbered tiles)
  def create_tiles_to_areas_hash_for_solution solution_areas
    numbered_tiles_for_solution = solution_areas.map{|s| @numbered_tiles_hash.values & s}.flatten(1)
    numbered_tiles_hash = {}

    numbered_tiles_for_solution.each do |numbered_tile|
      numbered_tiles_hash[numbered_tile.join('-')] = solution_areas.detect{|area| area.include? numbered_tile}
    end

    numbered_tiles_hash
  end

  # Verify that areas for a solution are not coliding or touching
  def areas_independent?(solution_areas, numbered_tiles_for_solution=[])
    # numbered_tiles_for_solution = solution_areas.map{|s| @numbered_tiles_hash.values & s}.flatten(1)
    solution = solution_areas.flatten(1).uniq

    solution_hash = create_tiles_to_areas_hash_for_solution solution_areas

    areas_independent = true
    numbered_tiles_for_solution.each do |numbered_tile|
      area_size = @b_nl[numbered_tile[0],numbered_tile[1]]
      num_tile_key = numbered_tile.join('-')
      visited_area_tiles = traverse_area(numbered_tile, create_solution_board(solution))
      areas_independent = false if visited_area_tiles.to_a.sort != solution_hash[num_tile_key].sort
      break if !areas_independent
    end

    return areas_independent
  end

  #traverse solution, starting at a numbered tile,
  #and return the visited tiles
  #Usefull to detect if area is contained and not
  #touching on another area
  def traverse_area(next_field,vt=Set.new,solution)
    visited_tiles = vt
    avail_pos = available_ways_to_go_in_area(next_field,solution).to_set - visited_tiles

    visited_tiles << next_field
    avail_pos.each do |area_tile|
      if ((solution[area_tile[0],area_tile[1]] != 0) && (!visited_tiles.include? area_tile))
        traverse_area(area_tile,visited_tiles,solution)
      end
    end
    visited_tiles
  end

  def available_ways_to_go_in_area(current_pos,solution)
    avaiable_ways = []
    x,y = current_pos[0], current_pos[1]
    up = [x-1,y]
    down = [x+1,y]
    left = [x,y-1]
    right = [x,y+1]

    [up,right,down,left].reject{|e| ((solution[e[0],e[1]] == 0)||(solution[e[0],e[1]] == nil)||(e[0] < 0)||(e[1]<0))}.uniq #.shuffle
  end

  def pick_a_random_starting_point(solution)
    while true do
      x, y = rand(@row_count-1), rand(@col_count-1)
      return [x,y] if solution[x,y] == 0
    end
  end

  def verify_road_tiles_are_connected(next_field=[0,1],vt=[],solution)
  end

  def available_ways_to_go_to_new(current_pos,solution)
    avaiable_ways = []
    x,y = current_pos[0], current_pos[1]
    up = [x-1,y]
    down = [x+1,y]
    left = [x,y-1]
    right = [x,y+1]

    [up,right,down,left].reject{|e| ((solution[e[0],e[1]].to_i >= 1)||(e[0] > @row_count-1 ) ||( e[1] > @col_count-1 )||(e[0] < 0)||(e[1]<0))} #.uniq #.shuffle
  end

  def available_ways_to_go_to(current_pos,solution)
    avaiable_ways = []
    x,y = current_pos[0], current_pos[1]
    up = [x-1,y]
    down = [x+1,y]
    left = [x,y-1]
    right = [x,y+1]

    [up,right,down,left].reject{|e| ((solution[e[0],e[1]] == 1)||(solution[e[0],e[1]] == nil)||(e[0] < 0)||(e[1]<0))}.uniq #.shuffle
  end

  def create_solution_board(solution)
    m = ::Matrix.build(@row_count,@col_count){|x,y| 0}
    m_temp =*m
    solution.each do |x,y|
      begin
        m_temp[x][y] = 1
        m_temp[x][y] = @b_nl[x,y] if @b_nl[x,y] != 0
      rescue
        next
      end
    end

    Matrix[*m_temp]
  end

  def print_board(b)
    (0..@row_count-1).to_a.each do |i|
      print '       '
      # next_field is initiall the starting tile
      (0..@col_count-1).to_a.each do |j|
        # next if @b_nl.include? [i,j]
        if b[i,j].to_i != 0
          print "\x1b[0m".encode('utf-8')
          if @b_nl[i,j] != 0
            if @b_nl[i,j].to_s.size == 1
              print ' ' + @b_nl[i,j].to_s
            else
              #two digit numbers
              print @b_nl[i,j].to_s
            end
          else
            # print ' ' + @b_nl[i,j].to_s
            print ' 1'
          end
          print "\x1b[1m"
        elsif b[i,j].nil?
          print ' x'
        else
          if @visited_tiles.include? [i,j]
            print '  '.colorize(background: :white)
          else
            # print '  '.colorize(background: :light_blue)
            print '  '.colorize(background: :white)
            # print '  '.colorize(background: :white)
          end
        end
      end
      puts '' unless i == (@row_count-1)
    end
    puts
    puts
  end

  # DO I: need this method
  # def total_wall_lenght
  #   @count = 0
  #   (0..(@row_count-1)).to_a.each do |i|
  #     (0..(@col_count-1)).to_a.each do |j|
  #       @count += 1 if @b[i,j] == 0
  #     end
  #   end
  #   @count
  # end

end
