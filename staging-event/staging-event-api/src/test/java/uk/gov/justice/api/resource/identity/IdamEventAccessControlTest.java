package uk.gov.justice.api.resource.identity;

import static org.mockito.BDDMockito.given;

import uk.gov.moj.cpp.accesscontrol.common.providers.UserAndGroupProvider;
import uk.gov.moj.cpp.accesscontrol.drools.Action;
import uk.gov.moj.cpp.accesscontrol.test.utils.BaseDroolsAccessControlTest;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.api.runtime.ExecutionResults;
import org.mockito.Mock;

public class IdamEventAccessControlTest extends BaseDroolsAccessControlTest {

    private static final String[] UNATHORIZED_GROUP_NAME = RuleConstants.getSystemUsers();

    @Mock
    private UserAndGroupProvider userAndGroupProvider;

    public IdamEventAccessControlTest() {
        super("EVENT_API_SESSION");
    }
    @Test
    public void shouldAllowOnlyIdamUsersGroupForAccountCreated() {
        Action action = createActionFor("identity.events.account-created");

        given(userAndGroupProvider.isMemberOfAnyOfTheSuppliedGroups(action, RuleConstants.getIdamGroups()))
                .willReturn(true);
        final ExecutionResults results = executeRulesWith(action);
        assertSuccessfulOutcome(results);
    }

    @Test
    public void shouldNotAllowUnauthorisedUserAccessForAccountCreated() {
        Action action = createActionFor("identity.events.account-created");


        final ExecutionResults results = executeRulesWith(action);
        assertFailureOutcome(results);
    }

    @Test
    public void shouldAllowOnlyIdamUsersGroupForAccountUpdated() {
        Action action = createActionFor("identity.events.account-updated");

        given(userAndGroupProvider.isMemberOfAnyOfTheSuppliedGroups(action, RuleConstants.getIdamGroups()))
                .willReturn(true);
        final ExecutionResults results = executeRulesWith(action);
        assertSuccessfulOutcome(results);
    }

    @Test
    public void shouldNotAllowUnauthorisedUserAccessForAccountUpdated() {
        Action action = createActionFor("identity.events.account-updated");


        final ExecutionResults results = executeRulesWith(action);
        assertFailureOutcome(results);
    }

    @Test
    public void shouldAllowOnlyIdamUsersGroupForAccountDeregistered() {
        Action action = createActionFor("identity.events.account-deregistered");

        given(userAndGroupProvider.isMemberOfAnyOfTheSuppliedGroups(action, RuleConstants.getIdamGroups()))
                .willReturn(true);
        final ExecutionResults results = executeRulesWith(action);
        assertSuccessfulOutcome(results);
    }

    @Test
    public void shouldNotAllowUnauthorisedUserAccessForAccountDegregistered() {
        Action action = createActionFor("identity.events.account-deregistered");

        final ExecutionResults results = executeRulesWith(action);
        assertFailureOutcome(results);
    }

    @Test
    public void shouldAllowOnlyIdamUsersGroupForAccountReRegistered() {
        Action action = createActionFor("identity.events.account-reregistered");

        given(userAndGroupProvider.isMemberOfAnyOfTheSuppliedGroups(action, RuleConstants.getIdamGroups()))
                .willReturn(true);
        final ExecutionResults results = executeRulesWith(action);
        assertSuccessfulOutcome(results);
    }

    @Test
    public void shouldNotAllowUnauthorisedUserAccessForAccountReRegistered() {
        Action action = createActionFor("identity.events.account-reregistered");


        final ExecutionResults results = executeRulesWith(action);
        assertFailureOutcome(results);
    }

    @Test
    public void shouldAllowOnlyIdamUsersGroupForOrganisationCreated() {
        Action action = createActionFor("identity.events.organisation-created");

        given(userAndGroupProvider.isMemberOfAnyOfTheSuppliedGroups(action, RuleConstants.getIdamGroups()))
                .willReturn(true);
        final ExecutionResults results = executeRulesWith(action);
        assertSuccessfulOutcome(results);
    }

    @Test
    public void shouldNotAllowUnauthorisedUserAccessForOrganisationCreated() {
        Action action = createActionFor("identity.events.organisation-created");


        final ExecutionResults results = executeRulesWith(action);
        assertFailureOutcome(results);
    }

    @Test
    public void shouldAllowOnlyIdamUsersGroupForOrganisationUpdated() {
        Action action = createActionFor("identity.events.organisation-updated");

        given(userAndGroupProvider.isMemberOfAnyOfTheSuppliedGroups(action, RuleConstants.getIdamGroups()))
                .willReturn(true);
        final ExecutionResults results = executeRulesWith(action);
        assertSuccessfulOutcome(results);
    }

    @Test
    public void shouldNotAllowUnauthorisedUserAccessForOrganisationUpdated() {
        Action action = createActionFor("identity.events.organisation-updated");

        final ExecutionResults results = executeRulesWith(action);
        assertFailureOutcome(results);
    }

    @Override
    protected Map<Class<?>, Object> getProviderMocks() {
        return Collections.singletonMap(UserAndGroupProvider.class, userAndGroupProvider);
    }
}
