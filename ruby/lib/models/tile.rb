class Tile
  attr_accessor :x,:y

  def initialize x, y
    @x = x
    @y = y
  end

  def to_s
    '[' + x.to_s + ',' + y.to_s + ']'
  end
end

# t = Tile.new(1,2)
