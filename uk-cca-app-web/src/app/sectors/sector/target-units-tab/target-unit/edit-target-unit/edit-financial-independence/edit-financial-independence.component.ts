import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { GovukSelectOption, GovukValidators, SelectComponent } from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';
import { financialIndependenceStatusTypeMap } from '@shared/pipes';

import { TargetUnitAccountDetailsDTO, UpdateTargetUnitAccountService } from 'cca-api';

import { ActiveTargetUnitStore } from '../../../active-target-unit.store';

@Component({
  selector: 'cca-edit-financial-independence',
  templateUrl: './edit-financial-independence.component.html',
  standalone: true,
  imports: [SelectComponent, ReactiveFormsModule, WizardStepComponent, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditFinancialIndependenceComponent {
  private readonly updateTargetUnitAccountService = inject(UpdateTargetUnitAccountService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly store = inject(ActiveTargetUnitStore);

  readonly targetUnitAccountDetails = this.store.state.targetUnitAccountDetails;

  readonly form = this.fb.group({
    financialIndependenceStatus: this.fb.control(
      this.targetUnitAccountDetails?.financialIndependenceStatus,
      GovukValidators.required('You must select an option'),
    ),
  });

  readonly options: GovukSelectOption<TargetUnitAccountDetailsDTO['financialIndependenceStatus']>[] = [
    {
      text: financialIndependenceStatusTypeMap['FINANCIALLY_INDEPENDENT'],
      value: 'FINANCIALLY_INDEPENDENT',
    },
    {
      text: financialIndependenceStatusTypeMap['NON_FINANCIALLY_INDEPENDENT'],
      value: 'NON_FINANCIALLY_INDEPENDENT',
    },
  ];

  onSubmitFinancialIndependenceStatus() {
    this.updateTargetUnitAccountService
      .updateTargetUnitAccountFinancialIndependenceStatusCode(+this.route.snapshot.paramMap.get('targetUnitId'), {
        financialIndependenceStatus: this.form.value.financialIndependenceStatus,
      })
      .subscribe(() => {
        this.store.updateFinancialIndependenceStatus(this.form.getRawValue());
        this.router.navigate(['../..'], { relativeTo: this.route, replaceUrl: true });
      });
  }
}
