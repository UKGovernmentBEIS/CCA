import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { PendingRequestService } from '@netz/common/services';
import { LinkDirective } from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import {
  SectorAssociationSchemeDTO,
  TargetUnitAccountDetailsResponseDTO,
  UpdateTargetUnitAccountService,
} from 'cca-api';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { TargetUnitDetailsInputComponent } from '../../../common/components/target-unit-details-input/target-unit-details-input.component';
import { TargetUnitCreationFormModel } from '../../../common/components/target-unit-details-input/target-unit-details-input-controls';
import { EDIT_TARGET_UNIT_DETAILS_FORM, EditDetailsFormProvider } from './edit-details-form.provider';

@Component({
  selector: 'cca-edit-details',
  templateUrl: './edit-details.component.html',
  standalone: true,
  imports: [ReactiveFormsModule, WizardStepComponent, LinkDirective, RouterLink, TargetUnitDetailsInputComponent],
  providers: [EditDetailsFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditDetailsComponent {
  readonly form = inject<FormGroup<TargetUnitCreationFormModel>>(EDIT_TARGET_UNIT_DETAILS_FORM);
  readonly pendingRequest = inject(PendingRequestService);
  private readonly updateTargetUnitAccountService = inject(UpdateTargetUnitAccountService);
  private readonly store = inject(ActiveTargetUnitStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  accountDetails: TargetUnitAccountDetailsResponseDTO = this.store.state;
  readonly subSectors = (this.route.snapshot.data?.subSectorScheme as SectorAssociationSchemeDTO)
    ?.subsectorAssociationSchemes;

  onSubmit() {
    this.updateTargetUnitAccountService
      .updateTargetUnitAccountSicCode(+this.route.snapshot.paramMap.get('targetUnitId'), {
        sicCode: this.form.value.sicCode,
      })
      .subscribe(() => {
        this.store.updateTargetUnitAccountSicCode({ sicCode: this.form.value.sicCode });
        this.router.navigate(['../..'], { relativeTo: this.route });
      });
  }
}
