import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { AuthStore, selectUserId } from '@netz/common/auth';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  LinkDirective,
  RadioComponent,
  RadioOptionComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { PageHeadingComponent, PhoneInputComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';

import { SectorUserAuthorityDetailsDTO, SectorUsersService } from 'cca-api';

import { sectorUserDetailsUpdateError } from '../../../../error/business-error';
import { ContactType } from '../../../types';
import { ActiveSectorUserStore } from '../../active-sector-user.store';
import {
  SECTOR_USER_DETAILS_FORM,
  SectorUserDetailsFormModel,
  SectorUserDetailsFormProvider,
} from './edit-sector-user-details-form.provider';

@Component({
  selector: 'cca-edit-sector-user-details',
  templateUrl: './edit-sector-user-details.component.html',
  standalone: true,
  imports: [
    LinkDirective,
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
  providers: [SectorUserDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditSectorUserDetailsComponent {
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly sectorUsersService = inject(SectorUsersService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(ActiveSectorUserStore);

  protected readonly form = inject<FormGroup<SectorUserDetailsFormModel>>(SECTOR_USER_DETAILS_FORM);

  private readonly sectorId = +this.activatedRoute.snapshot.paramMap.get('sectorId');
  private readonly sectorUserId = this.activatedRoute.snapshot.paramMap.get('sectorUserId');

  private readonly currentUserId = inject(AuthStore).select(selectUserId);
  private readonly isCurrentUser = this.currentUserId() === this.sectorUserId;

  readonly sectorUserDetails = this.store.state.details;

  contactTypeOptions: { text: string; value: ContactType }[] = [
    {
      text: 'Sector association',
      value: 'SECTOR_ASSOCIATION',
    },
    { text: 'Consultant', value: 'CONSULTANT' },
  ];

  onSubmit() {
    if (this.form.invalid) return;

    const data = { ...this.form.getRawValue(), email: this.sectorUserDetails.email } as SectorUserAuthorityDetailsDTO;

    if (this.isCurrentUser) {
      this.sectorUsersService
        .updateCurrentSectorUser(this.sectorId, data)
        .pipe(
          catchBadRequest([ErrorCodes.FORM1001, ErrorCodes.SECTOR1001], () =>
            this.businessErrorService.showError(sectorUserDetailsUpdateError),
          ),
        )
        .subscribe((user: SectorUserAuthorityDetailsDTO) => {
          this.store.updateState({ details: user });
          this.router.navigate(['..'], { relativeTo: this.activatedRoute, replaceUrl: true });
        });
    } else {
      this.sectorUsersService
        .updateSectorUserBySectorAssociationIdAndUserId(this.sectorId, this.sectorUserId, data)
        .pipe(
          catchBadRequest([ErrorCodes.FORM1001, ErrorCodes.SECTOR1001], () =>
            this.businessErrorService.showError(sectorUserDetailsUpdateError),
          ),
        )
        .subscribe((user: SectorUserAuthorityDetailsDTO) => {
          this.store.updateState({ details: user });
          this.router.navigate(['../../..'], {
            relativeTo: this.activatedRoute,
            replaceUrl: true,
            fragment: 'contacts',
          });
        });
    }
  }
}
