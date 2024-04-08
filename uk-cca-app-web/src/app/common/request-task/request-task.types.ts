import { Type } from '@angular/core';

import { TaskSection } from '../shared/model';

type RequestTaskPageContent = {
  contentComponent?: Type<unknown>;
  preContentComponent?: Type<unknown>;
  postContentComponent?: Type<unknown>;
  header: string;
  sections?: TaskSection[];
};

export type RequestTaskPageContentFactory = () => RequestTaskPageContent;
export type RequestTaskPageContentFactoryMap = Record<string, RequestTaskPageContentFactory>;
export type RequestTaskIsEditableResolver = () => boolean;
