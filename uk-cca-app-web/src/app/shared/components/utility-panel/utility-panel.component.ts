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
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UtilityPanelComponent {
  readonly heading = input<string>();
  readonly isFullBackground = input(false);
}
