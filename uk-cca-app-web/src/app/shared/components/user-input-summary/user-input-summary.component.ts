import { ChangeDetectionStrategy, Component, inject, input, OnInit } from '@angular/core';
import { ActivatedRoute, Params, RouterLink } from '@angular/router';

import {
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { PhoneNumberPipe } from '@shared/pipes';

import { CcaOperatorUserRegistrationWithCredentialsDTO } from 'cca-api';

@Component({
  selector: 'cca-user-input-summary-template',
  templateUrl: './user-input-summary.component.html',
  imports: [
    RouterLink,
    PhoneNumberPipe,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserInputSummaryTemplateComponent implements OnInit {
  protected readonly route = inject(ActivatedRoute);

  protected readonly userInfo =
    input<Partial<Omit<CcaOperatorUserRegistrationWithCredentialsDTO, 'emailToken'>>>(undefined);

  protected readonly changeLink = input<string>(undefined);

  changeQueryParams: Params = { change: true };
  modifiedUserInfo: Partial<Omit<CcaOperatorUserRegistrationWithCredentialsDTO, 'emailToken'>>;

  ngOnInit() {
    this.modifiedUserInfo = this.userInfo();
  }
}
