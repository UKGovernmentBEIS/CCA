import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY, switchMap } from 'rxjs';

import {
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TextareaComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { ErrorSummaryComponent, ErrorSummaryInfo, WizardStepComponent } from '@shared/components';

import { BuyOutAndSurplusInfoService, FacilityInfoDTO } from 'cca-api';

import { BuyoutSurplusStore } from '../../../../../../buy-out-surplus/buy-out-surplus.store';
import { SurplusAmountPipe } from '../surplus-amount.pipe';
import { EDIT_FACILITY_SURPLUS_FORM, editFacilitySurplusFormProvider } from './edit-surplus-form.provider';

@Component({
  selector: 'cca-edit-facility-surplus',
  templateUrl: './edit-surplus.component.html',
  providers: [editFacilitySurplusFormProvider, BuyoutSurplusStore],
  imports: [
    SurplusAmountPipe,
    ReactiveFormsModule,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    TextareaComponent,
    TextInputComponent,
    WizardStepComponent,
    ErrorSummaryComponent,
  ],
})
export default class EditFacilitySurplusComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly buyOutAndSurplusInfoService = inject(BuyOutAndSurplusInfoService);
  private readonly buyoutSurplusStore = inject(BuyoutSurplusStore);
  private readonly routeData = toSignal(this.route.data, { initialValue: this.route.snapshot.data });
  private readonly facilityDetails = computed(() => this.routeData().facilityDetails as FacilityInfoDTO);

  protected readonly currentSurplus = computed(() => this.facilityDetails().surplusDetails?.surplusGained ?? '0');
  protected readonly errorSummaryInfo = signal<ErrorSummaryInfo | null>(null);
  readonly form = signal(inject(EDIT_FACILITY_SURPLUS_FORM)).asReadonly();

  onSubmit() {
    const { surplus, comments } = this.form().getRawValue();
    this.errorSummaryInfo.set(null);

    this.buyoutSurplusStore
      .checkForPendingBatchRun()
      .pipe(
        switchMap((runInProgress) => {
          if (runInProgress) {
            this.errorSummaryInfo.set({ message: 'You cannot make an adjustment while a Batch Run is in progress.' });
            return EMPTY;
          }

          return this.buyOutAndSurplusInfoService.updateFacilitySurplusGained(this.facilityDetails().facilityId, {
            newSurplusGained: surplus,
            comments,
          });
        }),
      )
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.route }));
  }
}
