import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BreadcrumbService } from '@netz/common/navigation';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PageHeadingComponent, PhoneInputComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';

import { OPERATOR_USER_INVITATION_FORM, OperatorUserInviteFormModel } from '../form.provider';
import { OperatorUserInvitationStore } from '../store';

@Component({
  selector: 'cca-operator-user-invitation',
  templateUrl: './operator-user-invitation-details.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    ErrorSummaryComponent,
    PageHeadingComponent,
    RadioComponent,
    RadioOptionComponent,
    TextInputComponent,
    ButtonDirective,
    PendingButtonDirective,
    PhoneInputComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorUserInvitationComponent implements OnInit {
  private readonly breadcrumbsService = inject(BreadcrumbService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(OperatorUserInvitationStore);

  contactTypeOptions = [
    { text: 'Operator', value: 'OPERATOR' },
    { text: 'Consultant', value: 'CONSULTANT' },
  ];
  readonly form = inject<FormGroup<OperatorUserInviteFormModel>>(OPERATOR_USER_INVITATION_FORM);

  ngOnInit(): void {
    this.breadcrumbsService.show([
      {
        text: 'Home',
        link: ['dashboard'],
      },
    ]);
  }
  onSubmit() {
    if (this.form.invalid) return;

    this.store.updateState({ ...this.store.state, ...this.form.value });
    const change = this.activatedRoute.snapshot.queryParamMap.get('change') === 'true';
    this.router.navigate(change ? ['summary'] : ['create-password'], { relativeTo: this.activatedRoute });
  }
}
