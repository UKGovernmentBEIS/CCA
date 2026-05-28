import { NonComplianceNoticeOfIntent } from 'cca-api';

export const isWizardCompleted = (noticeOfIntent?: NonComplianceNoticeOfIntent): boolean => {
  return !!noticeOfIntent?.file;
};
