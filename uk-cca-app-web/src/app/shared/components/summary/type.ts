import { DownloadableFile } from '@shared/utils';

export type LinkList = Array<{ text: string; link: string }>;

export type SummarySectionBase = {
  key: string;
  link?: string; // Provide a url value if the value should redirect the user
  change?: boolean;
  appendChangeParam?: boolean;
  preline?: boolean; // this must be true for text-area inputs
  changeLink?: string; // if a change link is present in a summary section, it precedes the section change link
  action?: string;
  actionLink?: string;
  fieldDiff?: boolean;
};

export type SummarySection =
  | (SummarySectionBase & { value: string | string[]; isFileList?: false; isLinkList?: false })
  | (SummarySectionBase & { value: DownloadableFile[]; isFileList: true; isLinkList?: false })
  | (SummarySectionBase & { value: LinkList; isFileList?: false; isLinkList: true });

/** Options accepted by {@link SummaryFactory} row methods. */
export type SummarySectionOpts = Partial<SummarySectionBase>;

export type SummaryData = Array<{
  header: string;
  data: SummarySection[];
  changeLink?: string;
  opts?: {
    testid?: string;
    headerClasses?: string[];
  };
}>;
