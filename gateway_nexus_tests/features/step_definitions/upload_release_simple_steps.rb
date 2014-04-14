Given /^a file is uploaded into the correct repository and package with a correctly generated pom.xml file$/ do
   HOST = '10.66.2.196'
   USER = 'root'
   Net::SSH.start(HOST, USER, :keys => "~/.ssh/VPCDevInstanceDefault.pem") do|ssh|
     @result = ssh.exec!('cd ~/sample/sample_matt_no_sec && mvn deploy')
   end
   puts @result
end

Then /^checksums are generated and the release can be downloaded by another user with access to the relevant repository$/ do
  @browser.goto("http://10.66.2.195:8081/nexus/content/repositories/snapshots/")
  @browser.link(:href => "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/").click
  @browser.link(:href => "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/").click
  @browser.link(:href => "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/tps/").click
  @browser.link(:href => "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/tps/sample_matt_no_sec/").click
  fail "MD5 checksum not present" unless @browser.html.include? "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/tps/sample_matt_no_sec/maven-metadata.xml.md5"
  fail "SHA1 checksum not present" unless @browser.html.include? "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/tps/sample_matt_no_sec/maven-metadata.xml.sha1"
end

Given /^a file is uploaded into the correct repository and package with a correctly generated pom.xml file alongside a valid security marking metadata file$/ do
  HOST = '10.66.2.196'
  USER = 'root'
  Net::SSH.start(HOST, USER, :keys => "~/.ssh/VPCDevInstanceDefault.pem") do|ssh|
    @result = ssh.exec!('cd ~/sample/sample_matt && mvn deploy')
  end
  puts @result
end
 
Then /^checksums are generated, the security marking metadata file is present and the release can be downloaded by another user with access to the relevant repository$/ do
  @browser.goto("http://10.66.2.195:8081/nexus/content/repositories/snapshots/")
  @browser.link(:href => "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/").click
  @browser.link(:href => "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/").click
  @browser.link(:href => "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/tps/").click
  @browser.link(:href => "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/tps/sample_matt/").click
  @browser.link(:href => "http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/tps/sample_matt/1.2-SNAPSHOT/").click
  fail "MD5 checksum not present" unless @browser.html.include? "xml.md5"
  fail "SHA1 checksum not present" unless @browser.html.include? "xml.sha1"
  fail "Security Label .xml not present" unless @browser.html.include? "securitylabel.xml"
end