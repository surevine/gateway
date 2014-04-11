require 'rubygems'
require 'cucumber'
require 'cucumber/rake/task'

namespace :features do
  
  Cucumber::Rake::Task.new(:default) do |t|
    t.profile = 'default'
    t.cucumber_opts = "--format html --out reporting/latest_run.html"
  end
  
  Cucumber::Rake::Task.new(:wip) do |t|
    t.profile = 'wip'
    t.cucumber_opts = "--format html --out reporting/latest_run.html"
  end
  
  Cucumber::Rake::Task.new(:happy_path) do |t|
    t.profile = 'happy_path'
    t.cucumber_opts = "--format html --out reporting/latest_run.html"
  end
end