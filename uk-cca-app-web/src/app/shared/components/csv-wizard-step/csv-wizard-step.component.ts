import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
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
  @Input() showBackLink = false;
  @Input() formGroup: UntypedFormGroup;
  @Input() heading: string;
  @Input() caption: string;
  @Input() submitText = 'Continue';
  @Input() hideSubmit: boolean;
  @Input() showReturnLink = false;
  @Input() showCancelLink = false;
  @Input() cancelLinkPath: string;
  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  isSummaryDisplayedSubject = new BehaviorSubject(false);

  onSubmit(): void {
    this.formGroup.statusChanges
      .pipe(
        startWith(this.formGroup.status),
        filter((status) => status !== 'PENDING'),
        take(1),
      )
      .subscribe((status) => {
        switch (status) {
          case 'VALID':
            this.formSubmit.emit(this.formGroup);
            break;
          case 'INVALID':
            this.formGroup.markAllAsTouched();
            this.isSummaryDisplayedSubject.next(true);
            break;
        }
      });
  }
}
