package uk.gov.cca.api.subsistencefees;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SubsistenceFeesTransactionIdentifierUtil {

    public String generate(String moaTypeId, Long transactionId) {
        return String.format("CCA%sM%05d", moaTypeId, transactionId);
    }
}
