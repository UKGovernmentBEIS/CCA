import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { WizardStepComponent } from '@shared/components';

import { SectorAssociationSchemesDTO } from 'cca-api';

import { ActiveSectorStore } from '../../active-sector.store';
import { TargetUnitDetailsInputComponent } from '../common/components/target-unit-details-input/target-unit-details-input.component';
import { TargetUnitCreationFormModel } from '../common/types';
import { CreateTargetUnitStore } from './create-target-unit.store';
import { TARGET_UNIT_CREATION_FORM, TargetUnitCreationFormProvider } from './create-target-unit-form.provider';

@Component({
  selector: 'cca-create-target-unit',
  templateUrl: './create-target-unit.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, WizardStepComponent, TargetUnitDetailsInputComponent],
  providers: [TargetUnitCreationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateTargetUnitComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly createTargetUnitStore = inject(CreateTargetUnitStore);
  private readonly activeSector = inject(ActiveSectorStore).state;

  protected readonly form = inject<FormGroup<TargetUnitCreationFormModel>>(TARGET_UNIT_CREATION_FORM);

  protected readonly subSectors = (this.activatedRoute.snapshot.data.subSectorScheme as SectorAssociationSchemesDTO)
    .subsectorAssociations;

  onSubmitTargetUnit() {
    this.createTargetUnitStore.updateState({
      ...this.form.value,
      isCompanyRegistrationNumber: this.form.value.isCompanyRegistrationNumber,
      companyRegistrationNumber: this.form.value.companyRegistrationNumber,
      registrationNumberMissingReason: this.form.value.registrationNumberMissingReason,
      sicCodes: this.form.value.sicCodes.filter((val) => !!val),
      competentAuthority: this.activeSector.sectorAssociationDetails.competentAuthority,
      subsectorAssociationName: this.subSectors.find(
        (subsector) => subsector.id === this.form.get('subsectorAssociationId')?.value,
      )?.name,
    });

    this.router.navigate(['..', 'operator-address'], { relativeTo: this.activatedRoute });
  }
}
