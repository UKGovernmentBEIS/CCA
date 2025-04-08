package uk.gov.cca.api.account.domain;


public enum AccountSearchKey {

    ACCOUNT_NAME,
    BUSINESS_ID,
    FACILITY_ID,
    POST_CODE;

    public String concat(String... keys) {
        return String.join("_", keys) + "_" + this.name();
    }
}
