import { ChangeDetectorRef, DestroyRef, Directive, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { distinctUntilChanged } from 'rxjs';

import { TextInputComponent } from '@netz/govuk-components';

@Directive({
  selector: 'govuk-text-input[ccaAsyncValidationField],[govuk-text-input][ccaAsyncValidationField]',
  standalone: true,
})
export class AsyncValidationFieldDirective implements OnInit {
  constructor(
    private readonly textInputComponent: TextInputComponent,
    private readonly cdRef: ChangeDetectorRef,
    private readonly destroy$: DestroyRef,
  ) {}

  ngOnInit() {
    this.textInputComponent.control.statusChanges
      .pipe(
        takeUntilDestroyed(this.destroy$),
        distinctUntilChanged(
          (previousState, currentState) => previousState === currentState && previousState !== 'PENDING',
        ),
      )
      .subscribe(() => this.cdRef.markForCheck());
  }
}
