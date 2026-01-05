

# Run Integration Tests in Vagrant
## Prerequisite


  1. Deploy staging war files to the vagrant
  2. Inside vagrant (atcm project), update "ansible/group_vars/all/wildfly.yml" to open 10000 port to support external mock server
  3. vagrant reload
  4. `mvn verify -pl staging-integration-test -Pstaging-integration-test -DINTEGRATION_HOST_KEY=localhost`
