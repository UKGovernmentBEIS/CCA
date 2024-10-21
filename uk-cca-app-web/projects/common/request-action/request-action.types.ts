import { Type } from '@angular/core';

import { TaskSection } from '@netz/common/model';

export type RequestActionPageContent = {
  component?: Type<unknown>;
  header: string;
  sections?: TaskSection[];
};

export type RequestActionPageContentFactory = () => RequestActionPageContent;
export type RequestActionPageContentFactoryMap = Record<string, RequestActionPageContentFactory>;
