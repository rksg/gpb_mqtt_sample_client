require 'bundler/setup'
# require 'protobuf'
require 'beefcake'
require 'mqtt'
require File.join(File.dirname(__FILE__), '.', 'icx_switch.pb.rb')

class MqttClient
  conn_params = {host: '127.0.0.1', port: 1883, keep_alive: 30}
  MQTT::Client.connect(conn_params) do |mqtt_client|
    mqtt_client.get('rna-topic/SwitchInfo') do |topic, message|
      puts "I'm here"
      p "Topic: #{topic} | Message: #{message}"
      # switch_info = SwitchInfo.decode(message)
      switch_info = SwitchInfo.decode(message)
      p "GPB switch_info payload -> #{switch_info}"
      p "DECODED GPB SwitchInfo payload -> name:#{switch_info.name} | ipAddr:#{switch_info.ip_addr} | uptime:#{switch_info.uptime}"
      switch_info.ports_info.each do |port_info|
        p "PortInfo -> name:#{port_info.name} | index:#{port_info.index} | admin_status:#{port_info.admin_status} | octets_rx:#{port_info.octets_rx} | octets_tx:#{port_info.octets_tx} | pkts_rx:#{port_info.pkts_rx} | pkts_tx:#{port_info.pkts_tx} | poe_enabled: #{port_info.poe}"
      end
    end
  end
end
