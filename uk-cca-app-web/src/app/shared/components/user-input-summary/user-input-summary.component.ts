import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Params, RouterLink } from '@angular/router';

import {
  LinkDirective,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';
import { PhoneNumberPipe } from '@shared/pipes';

import { CcaOperatorUserRegistrationWithCredentialsDTO } from 'cca-api';

@Component({
  selector: 'cca-user-input-summary-template',
  standalone: true,
  templateUrl: './user-input-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    RouterLink,
    PhoneNumberPipe,
    LinkDirective,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    SummaryListRowActionsDirective,
  ],
})
export class UserInputSummaryTemplateComponent implements OnInit {
  @Input() userInfo: Partial<Omit<CcaOperatorUserRegistrationWithCredentialsDTO, 'emailToken'>>;
  @Input() changeLink: string;

  changeQueryParams: Params = { change: true };
  modifiedUserInfo: Partial<Omit<CcaOperatorUserRegistrationWithCredentialsDTO, 'emailToken'>>;

  constructor(readonly route: ActivatedRoute) {}

  ngOnInit(): void {
    this.modifiedUserInfo = this.userInfo;
  }
}
