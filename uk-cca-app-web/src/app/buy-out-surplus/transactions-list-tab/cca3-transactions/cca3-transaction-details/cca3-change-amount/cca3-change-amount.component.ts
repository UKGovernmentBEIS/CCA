import { Component, inject } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { TextareaComponent } from '@netz/govuk-components';
import {
  ErrorSummaryComponent,
  MultipleFileInputComponent,
  SummaryComponent,
  TextInputComponent,
  WizardStepComponent,
} from '@shared/components';

import { Cca3ChangeAmountState } from './cca3-change-amount.state';

@Component({
  selector: 'cca-cca3-change-amount',
  templateUrl: './cca3-change-amount.component.html',
  imports: [
    ReactiveFormsModule,
    TextareaComponent,
    ErrorSummaryComponent,
    MultipleFileInputComponent,
    SummaryComponent,
    TextInputComponent,
    WizardStepComponent,
  ],
})
export class Cca3ChangeAmountComponent {
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  readonly state = inject(Cca3ChangeAmountState);

  onSubmit() {
    if (!this.state.form.valid || this.state.terminated()) return;
    this.state.errorSummary.set(null);
    this.router.navigate(['check-your-answers'], { relativeTo: this.route });
  }
}
