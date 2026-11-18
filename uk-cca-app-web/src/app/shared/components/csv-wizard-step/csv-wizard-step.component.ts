import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { BehaviorSubject, filter, startWith, take } from 'rxjs';

import { PageHeadingComponent, ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { ButtonDirective } from '@netz/govuk-components';

import { CsvErrorSummaryComponent } from '../csv-error-summary/csv-error-summary.component';

@Component({
  selector: 'cca-csv-wizard-step',
  templateUrl: './csv-wizard-step.component.html',
  imports: [
    RouterLink,
    AsyncPipe,
    PageHeadingComponent,
    ButtonDirective,
    PendingButtonDirective,
    ReactiveFormsModule,
    CsvErrorSummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CsvWizardStepComponent {
  protected readonly showBackLink = input(false);
  protected readonly formGroup = input<UntypedFormGroup>(undefined);
  protected readonly heading = input<string>(undefined);
  protected readonly caption = input<string>(undefined);
  protected readonly submitText = input('Continue');
  protected readonly hideSubmit = input<boolean>(undefined);
  protected readonly showReturnLink = input(false);
  protected readonly showCancelLink = input(false);
  protected readonly cancelLinkPath = input<string>(undefined);

  protected readonly formSubmit = output<UntypedFormGroup>();

  isSummaryDisplayedSubject = new BehaviorSubject(false);

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
            this.isSummaryDisplayedSubject.next(true);
            break;
        }
      });
  }
}
