import { NgModule } from '@angular/core';

import { CompetentAuthorityPipe } from '@shared/pipes/competent-authority.pipe';

import { GovukDatePipe } from './govuk-date.pipe';
import { ItemActionHeaderPipe } from './item-action-header.pipe';
import { ItemActionTypePipe } from './item-action-type.pipe';
import { ItemLinkPipe } from './item-link.pipe';
import { ItemNamePipe } from './item-name.pipe';
import { PhoneNumberPipe } from './phone-number.pipe';
import { SecondsToMinutesPipe } from './seconds-to-minutes.pipe';
import { TextEllipsisPipe } from './text-ellipsis.pipe';
import { TimelineItemLinkPipe } from './timeline-item-link.pipe';
import { UserFullNamePipe } from './user-full-name.pipe';

@NgModule({
  imports: [
    CompetentAuthorityPipe,
    GovukDatePipe,
    ItemActionHeaderPipe,
    ItemActionTypePipe,
    ItemLinkPipe,
    ItemNamePipe,
    PhoneNumberPipe,
    SecondsToMinutesPipe,
    TextEllipsisPipe,
    TimelineItemLinkPipe,
    UserFullNamePipe,
  ],
  exports: [
    CompetentAuthorityPipe,
    GovukDatePipe,
    ItemActionHeaderPipe,
    ItemActionTypePipe,
    ItemLinkPipe,
    ItemNamePipe,
    PhoneNumberPipe,
    SecondsToMinutesPipe,
    TextEllipsisPipe,
    TimelineItemLinkPipe,
    UserFullNamePipe,
  ],
})
export class PipesModule {}
