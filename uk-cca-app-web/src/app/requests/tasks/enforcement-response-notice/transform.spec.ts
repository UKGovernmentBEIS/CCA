import { NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload } from 'cca-api';

import { createNotifyOperatorActionDTO, createRequestTaskActionProcessDTO } from './transform';

describe('createRequestTaskActionProcessDTO', () => {
  it('should return a save application DTO with the expected request task and payload types', () => {
    const payload: NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload = {
      enforcementResponseNotice: {
        type: 'PENALTY',
        file: 'uuid-1',
        comments: 'Existing comments',
      },
    };
    const sectionsCompleted = { uploadEnforcementResponseNotice: 'IN_PROGRESS' };

    expect(createRequestTaskActionProcessDTO(123, payload, sectionsCompleted)).toEqual({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_PAYLOAD',
        enforcementResponseNotice: payload.enforcementResponseNotice,
        sectionsCompleted,
      },
    });
  });

  it('should forward enforcement response notice and sections completed unchanged', () => {
    const payload: NonComplianceEnforcementResponseNoticeSaveRequestTaskActionPayload = {
      enforcementResponseNotice: {
        type: 'PENALTY_WAIVER',
        file: 'uuid-1',
        comments: 'Existing comments',
      },
    };
    const sectionsCompleted = { uploadEnforcementResponseNotice: 'COMPLETED' };
    const dto = createRequestTaskActionProcessDTO(123, payload, sectionsCompleted);

    expect(dto.requestTaskActionPayload['enforcementResponseNotice']).toBe(payload.enforcementResponseNotice);
    expect(dto.requestTaskActionPayload['sectionsCompleted']).toBe(sectionsCompleted);
  });

  it('should handle an undefined payload', () => {
    expect(createRequestTaskActionProcessDTO(123, undefined, {})).toEqual({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_APPLICATION',
      requestTaskActionPayload: {
        payloadType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SAVE_PAYLOAD',
        enforcementResponseNotice: undefined,
        sectionsCompleted: {},
      },
    });
  });
});

describe('createNotifyOperatorActionDTO', () => {
  it('should return a notify operator DTO with the expected action and payload types', () => {
    const decisionNotification = {
      operators: ['operator@example.com'],
      externalContacts: [1],
      signatory: 'Signatory name',
    };

    expect(createNotifyOperatorActionDTO(123, decisionNotification)).toEqual({
      requestTaskId: 123,
      requestTaskActionType: 'NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_NOTIFY_OPERATOR',
      requestTaskActionPayload: {
        payloadType: 'NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
        decisionNotification,
      },
    });
  });
});
