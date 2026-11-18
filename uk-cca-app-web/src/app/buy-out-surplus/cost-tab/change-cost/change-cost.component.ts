import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { catchError, EMPTY } from 'rxjs';

import { TextInputComponent, WizardStepComponent } from '@shared/components';
import { logger } from '@shared/utils';

import { BuyOutAndSurplusCostInfoService, TargetPeriodBuyOutDetailsDTO } from 'cca-api';

import { CHANGE_COST_FORM, ChangeCostFormModel, ChangeCostFormProvider } from './change-cost-form.provider';

type TargetPeriod = NonNullable<TargetPeriodBuyOutDetailsDTO['businessId']>;

@Component({
  selector: 'cca-change-cost',
  templateUrl: './change-cost.component.html',
  imports: [ReactiveFormsModule, TextInputComponent, WizardStepComponent],
  providers: [ChangeCostFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChangeCostComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly buyOutAndSurplusCostInfoService = inject(BuyOutAndSurplusCostInfoService);

  private readonly routeData = signal(
    this.route.snapshot.data as { targetPeriodDetails: TargetPeriodBuyOutDetailsDTO },
  );

  protected readonly form = inject<ChangeCostFormModel>(CHANGE_COST_FORM);
  protected readonly targetPeriodDetails = computed(() => this.routeData().targetPeriodDetails);
  protected readonly targetPeriod = computed(() => this.targetPeriodDetails().businessId as TargetPeriod);

  onSubmit(): void {
    this.buyOutAndSurplusCostInfoService
      .updateBuyOutCost(this.targetPeriod(), { buyOutCost: this.form.controls.buyOutCost.value! })
      .pipe(
        catchError((error) => {
          logger.error('Error updating buy-out cost', error);
          return EMPTY;
        }),
      )
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.route, replaceUrl: true }));
  }
}
