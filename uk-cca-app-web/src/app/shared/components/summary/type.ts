import { DownloadableFile } from '@shared/utils';

export type SummaryData = Array<{
  header: string;
  data: SummarySection[];
  changeLink?: string;
  opts?: {
    testid?: string;
  };
}>;

export type SummarySection = {
  key: string;
  value: string | string[] | DownloadableFile[];
  link?: string; // Provide a url value if the value should redirect the user
  change?: boolean;
  isFileList?: boolean;
  appendChangeParam?: boolean;
  prewrap?: boolean;
  changeLink?: string; // if a change link is present in a summary section, it precedes the section change link
};
