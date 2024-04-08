package uk.gov.cca.api.authorization.verifier.service;

import uk.gov.cca.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;

import java.util.List;

public interface VerifierAuthorityUpdateValidator {

    void validateUpdate(List<VerifierAuthorityUpdateDTO> verifiersUpdate, Long verificationBodyId);
}
