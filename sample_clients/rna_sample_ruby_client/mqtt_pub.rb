require 'bundler/setup'
require 'beefcake'
require 'mqtt'
require File.join(File.dirname(__FILE__), '.', 'switch_info.pb.rb')

class MqttClient
  conn_params = {host: 'localhost', port: 1883, keep_alive: 100}
  switch_info = SwitchInfo.new(value: "TEST", date: 23032017)
  # switch_info.value = "TEST"
  # switch_info.type = 999
  # switch_info.date = 23032017
  e = switch_info.encode
  MQTT::Client.connect(conn_params) do |mqtt_client|
    mqtt_client.publish('sci-topic', e)
  end
end
