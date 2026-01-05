package uk.gov.justice.api.resource.identity;
public class RuleConstants {

    private static final String GROUP_IDAM = "IDAM";

    private static final String SYSTEM_USERS = "System Users";

    private RuleConstants() {
    }

    public static final String[] getIdamGroups() {
        return new String[]{GROUP_IDAM};
    }

    public static final String[] getSystemUsers() {
        return new String[]{SYSTEM_USERS};
    }
}
