import { ChangeDetectorRef, DestroyRef, Directive, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { distinctUntilChanged } from 'rxjs';

import { TextInputComponent } from '@netz/govuk-components';

@Directive({
  selector: 'govuk-text-input[ccaAsyncValidationField],[govuk-text-input][ccaAsyncValidationField]',
})
export class AsyncValidationFieldDirective implements OnInit {
  private readonly textInputComponent = inject(TextInputComponent);
  private readonly cdRef = inject(ChangeDetectorRef);
  private readonly destroy$ = inject(DestroyRef);

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
