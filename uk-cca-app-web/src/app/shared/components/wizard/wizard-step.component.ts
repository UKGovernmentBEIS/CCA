import { Component, input, output, signal, viewChild } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { filter, startWith, take } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective, ErrorSummaryComponent } from '@netz/govuk-components';

@Component({
  selector: 'cca-wizard-step',
  templateUrl: './wizard-step.component.html',
  imports: [
    ReactiveFormsModule,
    PageHeadingComponent,
    PendingButtonDirective,
    RouterLink,
    ButtonDirective,
    ErrorSummaryComponent,
  ],
})
export class WizardStepComponent {
  protected readonly showBackLink = input(false);
  protected readonly showCancelLink = input(false);
  protected readonly cancelLinkPath = input<string>(undefined);
  protected readonly formGroup = input<UntypedFormGroup>(undefined);
  protected readonly heading = input<string>(undefined);
  protected readonly caption = input<string>(undefined);
  protected readonly submitText = input('Continue');
  protected readonly hideSubmit = input<boolean>(undefined);

  protected readonly formSubmit = output<UntypedFormGroup>();

  protected readonly isErrorSummaryDisplayed = signal(false);
  private readonly errorSummary = viewChild(ErrorSummaryComponent);

  /**
   * Shows the error summary and moves focus to it. Hosts call this after setting errors from an API response, since
   * those arrive after the submit that the summary refocuses on by itself.
   */
  showErrorSummary(): void {
    this.isErrorSummaryDisplayed.set(true);
    this.errorSummary()?.focus();
  }

  onSubmit(): void {
    this.formGroup()
      .statusChanges.pipe(
        startWith(this.formGroup().status),
        filter((status) => status !== 'PENDING'),
        take(1),
      )
      .subscribe((status) => {
        switch (status) {
          case 'VALID':
            this.formSubmit.emit(this.formGroup());
            break;

          case 'INVALID':
            this.formGroup().markAllAsTouched();
            this.isErrorSummaryDisplayed.set(true);
            break;
        }
      });
  }
}
