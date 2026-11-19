import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  GovukValidators,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
  TextareaComponent,
  TextInputComponent,
} from '@netz/govuk-components';
import { WizardStepComponent } from '@shared/components';

import { BuyOutAndSurplusInfoService } from 'cca-api';

import { BuyoutAndSurplusTabStore } from '../buyout-and-surplus-tab.store';
import { notEqualToCurrentValidator } from './edit-surplus.helper';

@Component({
  selector: 'cca-edit-surplus-history',
  templateUrl: './edit-surplus.component.html',
  imports: [
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    WizardStepComponent,
    ReactiveFormsModule,
    TextInputComponent,
    TextareaComponent,
  ],
})
export class EditSurplusComponent {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly buyOutAndSurplusInfoService = inject(BuyOutAndSurplusInfoService);
  private readonly buyoutAndSurplusTabStore = inject(BuyoutAndSurplusTabStore);

  private readonly targetUnitId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');

  readonly currentSurplus = +this.buyoutAndSurplusTabStore.state?.surplusInfo?.surplusDetails.surplusGained;

  readonly form = new FormGroup({
    surplus: new FormControl(null, [
      GovukValidators.wholeNumber('Enter a positive number or zero'),
      GovukValidators.required('Enter a surplus amount'),
      notEqualToCurrentValidator(this.currentSurplus),
    ]),
    comments: new FormControl(null, GovukValidators.required('Enter a comment')),
  });

  onSubmit() {
    this.buyOutAndSurplusInfoService
      .updateAccountSurplusGained(this.targetUnitId, {
        newSurplusGained: this.form.value.surplus,
        comments: this.form.value.comments,
      })
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.activatedRoute }));
  }
}
