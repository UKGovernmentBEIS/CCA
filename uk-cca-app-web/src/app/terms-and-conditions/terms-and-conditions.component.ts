import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import {
  AccordionComponent,
  AccordionItemComponent,
  ButtonDirective,
  CheckboxComponent,
  CheckboxesComponent,
  GovukValidators,
} from '@netz/govuk-components';
import { AuthService, LatestTermsStore } from '@shared/services';

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
    AccordionComponent,
    AccordionItemComponent,
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
