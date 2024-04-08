import { ChangeDetectorRef, Directive, OnInit } from '@angular/core';

import { distinctUntilChanged, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { TextInputComponent } from 'govuk-components';

@Directive({
  selector: 'govuk-text-input[ccaAsyncValidationField],[govuk-text-input][ccaAsyncValidationField]',
  providers: [DestroySubject],
})
export class AsyncValidationFieldDirective implements OnInit {
  constructor(
    private readonly textInputComponent: TextInputComponent,
    private readonly cdRef: ChangeDetectorRef,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.textInputComponent.control.statusChanges
      .pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged(
          (previousState, currentState) => previousState === currentState && previousState !== 'PENDING',
        ),
      )
      .subscribe(() => this.cdRef.markForCheck());
  }
}
