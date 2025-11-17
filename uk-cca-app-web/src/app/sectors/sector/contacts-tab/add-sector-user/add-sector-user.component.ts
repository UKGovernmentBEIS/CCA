import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY, startWith } from 'rxjs';

import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';

import { SectorUsersInvitationService } from 'cca-api';

import { ContactType, isAdmin, RoleCode } from '../../types';
import { ADD_SECTOR_FORM, AddSectorFormModel, AddSectorFormProvider } from './add-sector-user-form.provider';

@Component({
  selector: 'cca-add-sector-user',
  templateUrl: './add-sector-user.component.html',
  imports: [
    ButtonDirective,
    TextInputComponent,
    RadioComponent,
    RadioOptionComponent,
    PageHeadingComponent,
    ErrorSummaryComponent,
    ReactiveFormsModule,
    PendingButtonDirective,
  ],
  providers: [AddSectorFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AddSectorUserComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly sectorUserInvitationService = inject(SectorUsersInvitationService);
  private readonly router = inject(Router);

  protected readonly form = inject<AddSectorFormModel>(ADD_SECTOR_FORM);

  protected readonly status = toSignal(this.form.statusChanges.pipe(startWith(this.form.status)));
  protected readonly role = this.route.snapshot.queryParamMap.get('role') as RoleCode;
  protected readonly sectorId = this.route.snapshot.paramMap.get('sectorId');
  protected readonly isAdmin = isAdmin(this.role);

  protected readonly title = this.isAdmin ? 'Add an administrator user' : 'Add a basic user';

  protected readonly contactTypeOptions: { text: string; value: ContactType }[] = [
    {
      text: 'Sector association',
      value: 'SECTOR_ASSOCIATION',
    },
    { text: 'Consultant', value: 'CONSULTANT' },
  ];

  onSubmit() {
    this.form.markAsTouched();
    if (this.form.status === 'INVALID') return;

    const email = this.form.value.email;

    this.sectorUserInvitationService
      .inviteUserToSectorAssociation(+this.sectorId, this.form.getRawValue())
      .pipe(
        catchBadRequest(ErrorCodes.AUTHORITY1005, () => {
          this.form.controls.email.setErrors({
            emailExists: 'This user email already exists in CCA for this Sector',
          });
          return EMPTY;
        }),
        catchBadRequest(ErrorCodes.CCAAUTHORITY1003, () => {
          this.form.controls.email.setErrors({
            emailExists: 'Authority already exists for a different role type than sector user',
          });
          return EMPTY;
        }),
      )
      .subscribe(() => {
        this.router.navigate(['../add-confirmation'], {
          relativeTo: this.route,
          queryParams: { email },
          queryParamsHandling: 'merge',
        });
      });
  }
}
