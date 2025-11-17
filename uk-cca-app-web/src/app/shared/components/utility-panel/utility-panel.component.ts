import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'cca-utility-panel',
  template: `
    <div class="utility-panel-container" [class.background]="isFullBackground()">
      <div class="govuk-grid-row govuk-!-margin-bottom-4 govuk-!-margin-top-0 utility-panel-header background">
        {{ heading() }}
      </div>
      <ng-content />
    </div>
  `,
  styles: `
    .utility-panel-container {
      border: 1px solid var(--govuk-border-colour);
      padding: var(--govuk-spacing-0) var(--govuk-spacing-3) var(--govuk-spacing-1) var(--govuk-spacing-3);
    }

    .utility-panel-header {
      padding: var(--govuk-spacing-4);
      font-weight: bold;
    }

    .background {
      background: var(--govuk-light-grey);
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UtilityPanelComponent {
  readonly heading = input<string>();
  readonly isFullBackground = input(false);
}
