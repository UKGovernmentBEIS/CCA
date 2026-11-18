import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { Observable } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserId } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PhoneInputComponent } from '@shared/components';

import { CcaOperatorUserDetailsDTO, OperatorUsersService } from 'cca-api';

import { OperatorContactType } from '../../../../../types';
import { operatorUserUpdateError } from '../../../../error/business-error';
import { ActiveOperatorStore } from '../active-operator.store';
import {
  OperatorUserDetailsFormModel,
  OperatorUserDetailsFormProvider,
  TARGET_UNIT_OPERATOR_USER_DETAILS_FORM,
} from './edit-operator-details-form.provider';

@Component({
  selector: 'cca-edit-operator-details',
  templateUrl: './edit-operator-details.component.html',
  imports: [
    RouterLink,
    PageHeadingComponent,
    ErrorSummaryComponent,
    ReactiveFormsModule,
    ButtonDirective,
    TextInputComponent,
    PhoneInputComponent,
    RadioComponent,
    RadioOptionComponent,
    PendingButtonDirective,
  ],
  providers: [OperatorUserDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditOperatorDetailsComponent {
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly operatorUsersService = inject(OperatorUsersService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(ActiveOperatorStore);

  protected readonly form = inject<FormGroup<OperatorUserDetailsFormModel>>(TARGET_UNIT_OPERATOR_USER_DETAILS_FORM);

  private readonly targetUnitId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');
  private readonly operatorUserId = this.activatedRoute.snapshot.paramMap.get('userId');

  private readonly currentUserId = inject(AuthStore).select(selectUserId);
  private readonly isCurrentUser = this.currentUserId() === this.operatorUserId;

  protected readonly contactTypeOptions: { text: string; value: OperatorContactType }[] = [
    {
      text: 'Operator',
      value: 'OPERATOR',
    },
    { text: 'Consultant', value: 'CONSULTANT' },
  ];

  onSubmit() {
    if (this.form.invalid) return;

    const updateObs: Observable<CcaOperatorUserDetailsDTO> = this.isCurrentUser
      ? this.operatorUsersService.updateCurrentOperatorUser(
          this.targetUnitId,
          this.form.getRawValue() as CcaOperatorUserDetailsDTO,
        )
      : this.operatorUsersService.updateOperatorUserById(
          this.targetUnitId,
          this.operatorUserId,
          this.form.getRawValue() as CcaOperatorUserDetailsDTO,
        );

    updateObs
      .pipe(catchBadRequest([ErrorCodes.FORM1001], () => this.businessErrorService.showError(operatorUserUpdateError)))
      .subscribe((user: CcaOperatorUserDetailsDTO) => {
        this.store.updateState({ details: user });
        this.router.navigate(['..'], { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
