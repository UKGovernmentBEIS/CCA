import { DownloadableFile } from '@shared/utils';

export type LinkList = Array<{ text: string; link: string }>;

export type SummaryData = Array<{
  header: string;
  data: SummarySection[];
  changeLink?: string;
  opts?: {
    testid?: string;
    headerClasses?: string[];
  };
}>;

export type SummarySection = {
  key: string;
  value: string | string[] | DownloadableFile[] | LinkList;
  link?: string; // Provide a url value if the value should redirect the user
  change?: boolean;
  isFileList?: boolean;
  isLinkList?: boolean;
  appendChangeParam?: boolean;
  preline?: boolean; // this must be true for text-area inputs
  changeLink?: string; // if a change link is present in a summary section, it precedes the section change link
  action?: string;
  actionLink?: string;
};
