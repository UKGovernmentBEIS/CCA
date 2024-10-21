import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchBadRequest, ErrorCodes } from '@error/business-errors';
import { ButtonDirective, ErrorSummaryComponent, LinkDirective, TextInputComponent } from '@netz/govuk-components';
import { CountyAddressInputComponent } from '@shared/components';
import { PageHeadingComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';

import { SectorAssociationDetailsUpdateService } from 'cca-api';

import { sectorAssociationDetailsUpdateError } from '../../../error/business-error';
import { ActiveSectorStore } from '../../active-sector.store';
import { SectorEnergyEprFactorPipe } from '../../pipes/sector-energy-epr-factor.pipe';
import {
  SECTOR_ASSOCIATION_DETAILS_FORM,
  SectorAssociationDetailsFormModel,
  SectorAssociationDetailsFormProvider,
} from './edit-sector-association-details-form.provider';

@Component({
  selector: 'cca-edit-sector-association-details',
  templateUrl: './edit-sector-association-details.component.html',
  standalone: true,
  imports: [
    LinkDirective,
    RouterLink,
    PageHeadingComponent,
    ErrorSummaryComponent,
    ReactiveFormsModule,
    ButtonDirective,
    TextInputComponent,
    CountyAddressInputComponent,
    PendingButtonDirective,
    SectorEnergyEprFactorPipe,
  ],
  providers: [SectorAssociationDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditSectorAssociationDetailsComponent {
  private readonly sectorAssociationDetailsUpdateService = inject(SectorAssociationDetailsUpdateService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(ActiveSectorStore);

  readonly form = inject<FormGroup<SectorAssociationDetailsFormModel>>(SECTOR_ASSOCIATION_DETAILS_FORM);

  sectorAssociationDetails = this.store.state.sectorAssociationDetails;

  returnText = `${this.sectorAssociationDetails.acronym} - ${this.sectorAssociationDetails.commonName}`;

  onSubmit() {
    this.form.markAsTouched();
    if (this.form.invalid) return;

    this.sectorAssociationDetailsUpdateService
      .updateSectorAssociationDetails(+this.activatedRoute.snapshot.paramMap.get('sectorId'), this.form.getRawValue())
      .pipe(
        catchBadRequest([ErrorCodes.FORM1001, ErrorCodes.SECTOR1001], () =>
          this.businessErrorService.showError(sectorAssociationDetailsUpdateError),
        ),
      )
      .subscribe(() => {
        this.store.updateDetails(this.form.getRawValue());
        this.router.navigate(['..'], { relativeTo: this.activatedRoute, replaceUrl: true });
      });
  }
}
