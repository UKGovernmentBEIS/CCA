import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';

import { CcaOperatorUserInvitationDTO, OperatorUsersInvitationService } from 'cca-api';

import { ADD_OPERATOR_FORM, AddOperatorForm, AddOperatorFormProvider } from './add-operator-form.provider';

type ContactType = CcaOperatorUserInvitationDTO['contactType'];

@Component({
  selector: 'cca-add-operator',
  templateUrl: './add-operator.component.html',
  imports: [
    ErrorSummaryComponent,
    TextInputComponent,
    RadioComponent,
    RadioOptionComponent,
    ReactiveFormsModule,
    PageHeadingComponent,
    PendingButtonDirective,
    ButtonDirective,
  ],
  providers: [AddOperatorFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddOperatorComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly operatorInviteService = inject(OperatorUsersInvitationService);

  private readonly operatorExistsErrorText =
    'This email address is already in use. You must enter a different email address for this user to add them as an operator user';

  readonly form = inject<AddOperatorForm>(ADD_OPERATOR_FORM);

  protected readonly formInvalid = toSignal(this.form.statusChanges.pipe(map((s) => s === 'INVALID')));
  private readonly targetUnitId = this.route.snapshot.paramMap.get('targetUnitId');

  protected readonly contactTypeOptions: { text: string; value: ContactType }[] = [
    {
      text: 'Operator',
      value: 'OPERATOR',
    },
    { text: 'Consultant', value: 'CONSULTANT' },
  ];

  onSubmit() {
    this.form.markAsTouched();
    this.form.updateValueAndValidity();
    if (this.formInvalid()) return;

    this.operatorInviteService.inviteOperatorUserToAccount(+this.targetUnitId, this.form.getRawValue()).subscribe({
      complete: () => {
        this.router.navigate(['../confirmation'], { relativeTo: this.route });
      },
      error: (e) => {
        if (e.error.code === ErrorCodes.AUTHORITY1016) {
          this.form.controls.email.setErrors({
            operatorExists: this.operatorExistsErrorText,
          });
        }
      },
    });
  }
}
