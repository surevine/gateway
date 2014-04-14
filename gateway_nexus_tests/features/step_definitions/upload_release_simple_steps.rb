Given /^a file is uploaded into the correct repository and package with a correctly generated pom.xml file$/ do
  visit(WelcomePage)
  on(WelcomePage).upload_to_source "sample_matt_no_sec"
end

Then /^checksums are generated and the release can be downloaded by another user with access to the relevant local repository$/ do
  @browser.goto("http://10.66.2.196:8081/nexus/content/repositories/snapshots/com/surevine/tps/sample_matt_no_sec/2.10-SNAPSHOT/")
  fail "MD5 checksum not present" unless @browser.html.include? "xml.md5"
  fail "SHA1 checksum not present" unless @browser.html.include? "xml.sha1"
end

Given /^a file is uploaded into the correct repository and package with a correctly generated pom.xml file alongside a valid security marking metadata file$/ do
  visit(WelcomePage)
  on(WelcomePage).upload_to_source "sample_matt"
end
 
Then /^checksums are generated, the security marking metadata file is present and the release can be downloaded by another user with access to the relevant local and remote repositories$/ do
  @browser.goto("http://10.66.2.196:8081/nexus/content/repositories/snapshots/com/surevine/tps/sample_matt/2.10-SNAPSHOT/")
  fail "MD5 checksum not present - s" unless @browser.html.include? "xml.md5"
  fail "SHA1 checksum not present - s" unless @browser.html.include? "xml.sha1"
  fail "Security Label .xml not present - s" unless @browser.html.include? "securitylabel.xml"
  sleep 10
  @browser.goto("http://10.66.2.195:8081/nexus/content/repositories/snapshots/com/surevine/tps/sample_matt/2.10-SNAPSHOT/")
  fail "MD5 checksum not present - d" unless @browser.html.include? "xml.md5"
  fail "SHA1 checksum not present - d" unless @browser.html.include? "xml.sha1"
  #fail "Security Label .xml not present" unless @browser.html.include? "securitylabel.xml"
end
