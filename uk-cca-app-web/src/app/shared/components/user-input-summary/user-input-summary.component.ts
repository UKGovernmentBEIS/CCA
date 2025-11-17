import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
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
  @Input() userInfo: Partial<Omit<CcaOperatorUserRegistrationWithCredentialsDTO, 'emailToken'>>;
  @Input() changeLink: string;

  changeQueryParams: Params = { change: true };
  modifiedUserInfo: Partial<Omit<CcaOperatorUserRegistrationWithCredentialsDTO, 'emailToken'>>;

  constructor(readonly route: ActivatedRoute) {}

  ngOnInit() {
    this.modifiedUserInfo = this.userInfo;
  }
}
