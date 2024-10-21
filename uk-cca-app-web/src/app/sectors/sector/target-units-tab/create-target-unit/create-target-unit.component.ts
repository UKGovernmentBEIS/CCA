import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { LinkDirective } from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { SectorAssociationSchemeDTO } from 'cca-api';

import { ActiveSectorStore } from '../../active-sector.store';
import { TargetUnitDetailsInputComponent } from '../common/components/target-unit-details-input/target-unit-details-input.component';
import { TargetUnitCreationFormModel } from '../common/components/target-unit-details-input/target-unit-details-input-controls';
import { CreateTargetUnitStore } from './create-target-unit.store';
import { TARGET_UNIT_CREATION_FORM, TargetUnitCreationFormProvider } from './create-target-unit-form.provider';

@Component({
  selector: 'cca-create-target-unit',
  templateUrl: './create-target-unit.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, LinkDirective, RouterLink, WizardStepComponent, TargetUnitDetailsInputComponent],
  providers: [TargetUnitCreationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateTargetUnitComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(CreateTargetUnitStore);
  private readonly activeSector = inject(ActiveSectorStore).state;

  readonly form = inject<FormGroup<TargetUnitCreationFormModel>>(TARGET_UNIT_CREATION_FORM);

  readonly subSectors = (this.route.snapshot.data.subSectorScheme as SectorAssociationSchemeDTO)
    .subsectorAssociationSchemes;

  onSubmitTargetUnit() {
    this.store.updateState({
      ...this.form.value,
      competentAuthority: this.activeSector.sectorAssociationDetails.competentAuthority,
    });

    this.router.navigate(['..', 'operator-address'], { relativeTo: this.route });
  }
}
