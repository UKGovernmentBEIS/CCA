import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { LatestTermsStore } from '@core/store/latest-terms.store';
import { ButtonDirective, CheckboxComponent, CheckboxesComponent, GovukValidators } from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives';

import { TermsAndConditionsService } from 'cca-api';

@Component({
  selector: 'cca-terms-and-conditions',
  standalone: true,
  templateUrl: './terms-and-conditions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    PageHeadingComponent,
    ReactiveFormsModule,
    CheckboxComponent,
    CheckboxesComponent,
    PendingButtonDirective,
    ButtonDirective,
  ],
})
export class TermsAndConditionsComponent {
  private readonly router = inject(Router);
  private readonly termsAndConditionsService = inject(TermsAndConditionsService);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly latestTermsStore = inject(LatestTermsStore);

  terms = this.latestTermsStore.stateAsSignal;

  form: FormGroup = this.fb.group({
    terms: this.fb.control(null, GovukValidators.required('You should accept terms and conditions to proceed')),
  });

  submitTerms(): void {
    if (this.form.valid) {
      this.termsAndConditionsService
        .editUserTerms({ version: this.terms().version })
        .pipe(switchMap(() => this.authService.loadUserTerms()))
        .subscribe(() => this.router.navigate(['']));
    }
  }
}
