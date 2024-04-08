import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Params, RouterLink } from '@angular/router';

import { PipesModule } from '@shared/pipes/pipes.module';

import { GovukComponentsModule } from 'govuk-components';

import { OperatorUserRegistrationDTO } from 'cca-api';

@Component({
  selector: 'cca-user-input-summary-template',
  standalone: true,
  imports: [GovukComponentsModule, NgIf, PipesModule, RouterLink],
  templateUrl: './user-input-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserInputSummaryTemplateComponent implements OnInit {
  @Input() userInfo: Partial<Omit<OperatorUserRegistrationDTO, 'emailToken'>>;
  @Input() changeLink: string;

  changeQueryParams: Params = { change: true };
  modifiedUserInfo: Partial<Omit<OperatorUserRegistrationDTO, 'emailToken'>>;

  constructor(readonly route: ActivatedRoute) {}

  ngOnInit(): void {
    this.modifiedUserInfo = this.userInfo;
  }
}
