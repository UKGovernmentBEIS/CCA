import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'cca-loading-spinner',
  template: `
    <div class="loading-spinner" role="status" aria-live="polite">
      <div class="loading-spinner__spinner" aria-hidden="true"></div>

      <div class="loading-spinner__content">
        <ng-content />
      </div>
    </div>
  `,
  styleUrl: './loading-spinner.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoadingSpinnerComponent {}
