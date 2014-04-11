require 'sinatra'

get '/' do
  File.read("latest_run.html")
end