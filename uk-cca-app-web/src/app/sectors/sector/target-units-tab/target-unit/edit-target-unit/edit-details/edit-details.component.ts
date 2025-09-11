import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { WizardStepComponent } from '@shared/components';

import {
  SectorAssociationSchemesDTO,
  TargetUnitAccountDetailsResponseDTO,
  UpdateTargetUnitAccountService,
} from 'cca-api';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { TargetUnitDetailsInputComponent } from '../../../common/components/target-unit-details-input/target-unit-details-input.component';
import { TargetUnitCreationFormModel } from '../../../common/types';
import { EDIT_TARGET_UNIT_DETAILS_FORM, EditDetailsFormProvider } from './edit-details-form.provider';

@Component({
  selector: 'cca-edit-details',
  templateUrl: './edit-details.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, WizardStepComponent, RouterLink, TargetUnitDetailsInputComponent],
  providers: [EditDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditDetailsComponent {
  private readonly updateTargetUnitAccountService = inject(UpdateTargetUnitAccountService);
  private readonly store = inject(ActiveTargetUnitStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly form = inject<FormGroup<TargetUnitCreationFormModel>>(EDIT_TARGET_UNIT_DETAILS_FORM);

  protected readonly accountDetails: TargetUnitAccountDetailsResponseDTO = this.store.state;
  protected readonly subSectors = (this.route.snapshot.data?.subSectorScheme as SectorAssociationSchemesDTO)
    ?.subsectorAssociations;

  onSubmit() {
    // filter null values for SIC codes
    const sicCodes = this.form.value.sicCodes.filter((val) => !!val);

    this.updateTargetUnitAccountService
      .updateTargetUnitAccountSicCode(+this.route.snapshot.paramMap.get('targetUnitId'), {
        sicCodes: sicCodes,
      })
      .subscribe(() => {
        this.store.updateTargetUnitAccountSicCode({ sicCodes: sicCodes });
        this.router.navigate(['../..'], { relativeTo: this.route });
      });
  }
}
