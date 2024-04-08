package uk.gov.cca.api.workflow.request.flow.common.service;

import uk.gov.cca.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.List;

/**
 * Generates request id according to the RequestType.
 */
public interface RequestIdGenerator {

    String generate(RequestParams params);

    List<RequestType> getTypes();

    String getPrefix();
}
