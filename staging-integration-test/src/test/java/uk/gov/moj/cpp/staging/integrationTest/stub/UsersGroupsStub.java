package uk.gov.moj.cpp.staging.integrationTest.stub;


import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.UUID.randomUUID;
import static uk.gov.moj.cpp.staging.integrationTest.utils.TestUtils.readFile;
import static uk.gov.moj.cpp.staging.integrationTest.utils.WiremockTestHelper.waitForStubToBeReady;

import uk.gov.justice.service.wiremock.testutil.InternalEndpointMockUtils;

public class UsersGroupsStub {

    public static void stubGetUsersDetails() {
        InternalEndpointMockUtils.stubPingFor("usersgroups-service");

        final String userDetails = readFile("mockFiles/usersgroups.query.user.json");

        stubFor(get(urlPathMatching("/usersgroups-service/query/api/rest/usersgroups/users/.*"))
                .willReturn(aResponse().withStatus(200)
                        .withBody(userDetails)));

        waitForStubToBeReady("/usersgroups-service/query/api/rest/usersgroups/users/" + randomUUID().toString(), "application/vnd.usersgroups.query.user+json");
    }
}
