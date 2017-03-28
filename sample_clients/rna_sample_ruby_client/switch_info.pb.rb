## Generated from switch_info.proto
require "beefcake"


class SwitchInfo
  include Beefcake::Message
end

class SwitchInfo
  optional :value, :string, 1
  optional :type, :uint32, 2
  optional :encoded, :bool, 3
  optional :date, :sint32, 4
  optional :unit_price, :float, 5
end
