require 'rspec-expectations'
require 'page-object'
require 'data_magic'
require 'fig_newton'
require 'require_all'
require 'faker'
require 'net/ssh'

if ENV['HEADLESS']
  require 'headless'
  headless = Headless.new
  headless.start
  at_exit do
    headless.destroy
  end
end

require_all File.dirname(__FILE__) + '/pages'
require_all File.dirname(__FILE__) + '/helpers'

World(PageObject::PageFactory)