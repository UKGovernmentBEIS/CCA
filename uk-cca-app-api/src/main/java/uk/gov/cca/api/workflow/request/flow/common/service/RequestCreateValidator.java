package uk.gov.cca.api.workflow.request.flow.common.service;

import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

public interface RequestCreateValidator {

    RequestCreateActionType getType();
}
