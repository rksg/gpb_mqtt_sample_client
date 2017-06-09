## Generated from icx_switch.proto
require "beefcake"


class PortInfo
  include Beefcake::Message
end

class SwitchInfo
  include Beefcake::Message
end

class PortInfo
  optional :name, :string, 1
  optional :index, :uint32, 2
  optional :admin_status, :uint32, 3
  optional :octets_rx, :uint64, 4
  optional :octets_tx, :uint64, 5
  optional :pkts_rx, :uint64, 6
  optional :pkts_tx, :uint64, 7
  optional :poe, :bool, 8
end

class SwitchInfo
  optional :name, :string, 1
  optional :ip_addr, :string, 2
  optional :uptime, :uint32, 3
  repeated :ports_info, PortInfo, 4
end
