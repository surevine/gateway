Feature: Upload a Release Using Maven

  @rm_1 @happy_path @wip
  Scenario: Upload a simple binary file to Nexus (with no special security metadata) using the Maven “deploy:file” target.
    Given a file is uploaded into the correct repository and package with a correctly generated pom.xml file 
	Then checksums are generated and the release can be downloaded by another user with access to the relevant repository
	
  @rm_3 @happy_path
  Scenario: Upload a simple binary file to Nexus alongside a valid security marking metadata file using the Maven “deploy:file” target.
    Given a file is uploaded into the correct repository and package with a correctly generated pom.xml file alongside a valid security marking metadata file 
	Then checksums are generated, the security marking metadata file is present and the release can be downloaded by another user with access to the relevant repository