import { NoticeOfIntent } from 'cca-api';

export const isWizardCompleted = (noticeOfIntent?: NoticeOfIntent): boolean => {
  return !!noticeOfIntent?.noticeOfIntentFile;
};
