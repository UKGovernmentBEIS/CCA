import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { TextInputComponent, WizardStepComponent } from '@shared/components';

import {
  SectorAssociationSchemeDetailsUpdateService,
  SubsectorAssociationSchemeDetailsUpdateService,
  TargetCommitmentsUpdateDTO,
} from 'cca-api';

import { getCca3SchemeFromRoute, sortTargetCommitments } from '../scheme-tab.utils';
import {
  SECTOR_COMMITMENT_FORM,
  SectorCommitmentFormModel,
  SectorCommitmentFormProvider,
} from './sector-commitment-form.provider';

@Component({
  selector: 'cca-sector-commitment',
  templateUrl: './sector-commitment.component.html',
  imports: [WizardStepComponent, ReactiveFormsModule, TextInputComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [SectorCommitmentFormProvider],
})
export class SectorCommitmentComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorAssociationSchemeDetailsUpdateService = inject(SectorAssociationSchemeDetailsUpdateService);
  private readonly subsectorAssociationSchemeDetailsUpdateService = inject(
    SubsectorAssociationSchemeDetailsUpdateService,
  );

  protected readonly form = inject<SectorCommitmentFormModel>(SECTOR_COMMITMENT_FORM);
  protected readonly cca3Scheme = getCca3SchemeFromRoute(this.route);
  protected readonly commitments = sortTargetCommitments(this.cca3Scheme?.targetSet?.targetCommitments);
  private readonly isSubsectorScheme = !!this.route.snapshot.data.subSector;

  onSubmit(): void {
    const payload: TargetCommitmentsUpdateDTO = {
      targetCommitments: this.commitments.map((commitment, index) => ({
        id: commitment.id,
        targetImprovement: String(this.form.controls.commitments.at(index).value),
      })),
    };

    const update$ = this.isSubsectorScheme
      ? this.subsectorAssociationSchemeDetailsUpdateService.updateSubsectorAssociationSchemeTargetCommitments(
          this.cca3Scheme.id,
          payload,
        )
      : this.sectorAssociationSchemeDetailsUpdateService.updateSectorAssociationSchemeTargetCommitments(
          this.cca3Scheme.id,
          payload,
        );

    const navigationExtras = this.isSubsectorScheme
      ? { relativeTo: this.route }
      : { relativeTo: this.route, fragment: 'scheme' };

    update$.subscribe(() => this.router.navigate(['..'], navigationExtras));
  }
}
