import { RequestTaskActionPayload, RequestTaskPayload } from 'cca-api';

export type AppealOutcomeTribunalDecision = 'APPEAL_ALLOWED' | 'APPEAL_DISMISSED';

export interface AppealOutcome {
  tribunalDecision: AppealOutcomeTribunalDecision;
  appealOutcomeDate: string;
  file: string;
  comments: string;
}

export interface AppealOutcomeRequestTaskPayload extends RequestTaskPayload {
  appealOutcome?: AppealOutcome;
  nonComplianceAttachments?: Record<string, string>;
  sectionsCompleted?: Record<string, string>;
}

export interface AppealOutcomeSaveRequestTaskActionPayload extends RequestTaskActionPayload {
  appealOutcome: AppealOutcome;
}

export const APPEAL_OUTCOME_SUBTASK = 'appealOutcomeDetails';
