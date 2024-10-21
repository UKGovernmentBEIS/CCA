import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, viewChild } from '@angular/core';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { BehaviorSubject, filter, startWith, take } from 'rxjs';

import { ReturnToTaskOrActionPageComponent } from '@netz/common/components';
import { ButtonDirective, ErrorSummaryComponent, LinkDirective } from '@netz/govuk-components';
import { PageHeadingComponent } from '@shared/components';
import { PendingButtonDirective } from '@shared/directives/pending-button.directive';

@Component({
  selector: 'cca-wizard-step',
  standalone: true,
  templateUrl: './wizard-step.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    AsyncPipe,
    PageHeadingComponent,
    PendingButtonDirective,
    RouterLink,
    ButtonDirective,
    LinkDirective,
    ErrorSummaryComponent,
    ReturnToTaskOrActionPageComponent,
  ],
})
export class WizardStepComponent {
  @Input() showBackLink = false;
  @Input() showCancelLink = false;
  @Input() cancelLinkPath: string;
  @Input() formGroup: UntypedFormGroup;
  @Input() heading: string;
  @Input() caption: string;
  @Input() submitText = 'Continue';
  @Input() hideSubmit: boolean;

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  errorSummaryEl = viewChild(ErrorSummaryComponent);

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
            this.errorSummaryEl()?.container.nativeElement.focus();
            break;
        }
      });
  }
}
