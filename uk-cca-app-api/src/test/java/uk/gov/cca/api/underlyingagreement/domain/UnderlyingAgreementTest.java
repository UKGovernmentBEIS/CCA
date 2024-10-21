package uk.gov.cca.api.underlyingagreement.domain;

import org.junit.jupiter.api.Test;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UnderlyingAgreementTest {

    @Test
    void testUnderlyingAgreement_AuthorisationEvidence() {

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        AuthorisationAndAdditionalEvidence authorisationAndAdditionalEvidence = new AuthorisationAndAdditionalEvidence();
        authorisationAndAdditionalEvidence.setAuthorisationAttachmentIds(
                Set.of(uuid1, uuid2)
        );
        authorisationAndAdditionalEvidence.setAdditionalEvidenceAttachmentIds(new HashSet<>());

        UnderlyingAgreement underlyingAgreement = UnderlyingAgreement
                .builder()
                .authorisationAndAdditionalEvidence(authorisationAndAdditionalEvidence)
                .build();

        assertThat(underlyingAgreement.getUnderlyingAgreementSectionAttachmentIds()).contains(uuid1, uuid2);
    }

    @Test
    void testUnderlyingAgreement_AuthorisationAndAdditionalEvidence() {

        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();

        AuthorisationAndAdditionalEvidence authorisationAndAdditionalEvidence = new AuthorisationAndAdditionalEvidence();
        authorisationAndAdditionalEvidence.setAuthorisationAttachmentIds(Set.of(uuid1, uuid2));
        authorisationAndAdditionalEvidence.setAdditionalEvidenceAttachmentIds(Set.of(uuid3));

        UnderlyingAgreement underlyingAgreement = UnderlyingAgreement
                .builder()
                .authorisationAndAdditionalEvidence(authorisationAndAdditionalEvidence)
                .build();

        assertThat(underlyingAgreement.getUnderlyingAgreementSectionAttachmentIds()).contains(uuid1, uuid2, uuid3);
    }

}