module SshModule

  def upload_release_to_source(release_name)
    host = "#{FigNewton.nexus_one_ip}"
    user = "#{FigNewton.nexus_one_uname}"
    Net::SSH.start(host, user, :keys => "#{FigNewton.nexus_one_key}") do | ssh |
      @result = ssh.exec!("cd ~/sample/#{release_name} && mvn deploy")
    end
    puts @result
  end

end
