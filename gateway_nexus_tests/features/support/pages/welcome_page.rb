require_relative '../helpers/ssh_module'

class WelcomePage
  include PageObject
  include FigNewton
  include SshModule
  
  page_url("#{FigNewton.base_url}#welcome")
  
  def upload_to_source(release_name)
    upload_release_to_source release_name
  end
  
end