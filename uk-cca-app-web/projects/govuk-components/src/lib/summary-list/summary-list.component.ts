import { NgTemplateOutlet } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  HostBinding,
  TemplateRef,
  input,
  contentChildren,
  contentChild,
} from '@angular/core';

import {
  SummaryListColumnDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from './directives';
import { SummaryItem } from './summary-list.interface';

/*
  eslint-disable
  @angular-eslint/component-selector
 */
@Component({
  selector: 'dl[govuk-summary-list]',
  imports: [SummaryListRowKeyDirective, SummaryListRowDirective, NgTemplateOutlet, SummaryListRowValueDirective],
  templateUrl: './summary-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryListComponent {
  readonly details = input<SummaryItem[]>([]);
  readonly hasBorders = input(true);

  readonly rows = contentChildren(SummaryListRowDirective);
  readonly columns = contentChildren(SummaryListColumnDirective);
  readonly keyTemplate = contentChild<TemplateRef<any>>('keyTemplate');
  readonly valueTemplate = contentChild<TemplateRef<any>>('valueTemplate');

  @HostBinding('class.govuk-summary-list') readonly govukSummaryList = true;
  @HostBinding('class.govuk-!-margin-bottom-9') readonly bottomMargin = true;

  @HostBinding('class.govuk-summary-list--no-border') get govukSummaryListNoBorderClass(): boolean {
    return !this.hasBorders();
  }
}
