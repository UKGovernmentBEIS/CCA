import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, ErrorSummaryComponent, TextInputComponent } from '@netz/govuk-components';
import { CountyAddressInputComponent } from '@shared/components';

import { SectorAssociationContactDTO, SectorAssociationDetailsUpdateService } from 'cca-api';

import { sectorAssociationContactDetailsUpdateError } from '../../../error/business-error';
import { ActiveSectorStore } from '../../active-sector.store';
import {
  SECTOR_ASSOCIATION_CONTACT_DETAILS_FORM,
  SectorAssociationContactDetailsFormModel,
  SectorAssociationContactDetailsFormProvider,
} from './edit-sector-association-contact-details-form.provider';

@Component({
  selector: 'cca-edit-sector-association-contact-details',
  templateUrl: './edit-sector-association-contact-details.component.html',
  imports: [
    RouterLink,
    PageHeadingComponent,
    ErrorSummaryComponent,
    ReactiveFormsModule,
    ButtonDirective,
    TextInputComponent,
    CountyAddressInputComponent,
    PendingButtonDirective,
  ],
  providers: [SectorAssociationContactDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditSectorAssociationContactDetailsComponent {
  private readonly sectorAssociationDetailsUpdateService = inject(SectorAssociationDetailsUpdateService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(ActiveSectorStore);

  protected readonly form = inject<FormGroup<SectorAssociationContactDetailsFormModel>>(
    SECTOR_ASSOCIATION_CONTACT_DETAILS_FORM,
  );

  protected readonly sectorAssociationContact = this.store.state.sectorAssociationContact;

  protected readonly returnText = `${this.store.state.sectorAssociationDetails.acronym} - ${this.store.state.sectorAssociationDetails.commonName}`;

  onSubmit() {
    this.form.markAsTouched();
    if (this.form.invalid) return;

    this.sectorAssociationDetailsUpdateService
      .updateSectorAssociationContact(
        +this.activatedRoute.snapshot.paramMap.get('sectorId'),
        this.form.value as SectorAssociationContactDTO,
      )
      .pipe(
        catchBadRequest([ErrorCodes.FORM1001, ErrorCodes.SECTOR1002], () =>
          this.businessErrorService.showError(sectorAssociationContactDetailsUpdateError),
        ),
      )
      .subscribe(() => {
        this.store.updateContactDetails(this.form.getRawValue());
        this.router.navigate(['..'], { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
