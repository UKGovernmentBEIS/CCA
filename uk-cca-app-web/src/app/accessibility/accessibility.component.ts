import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BackToTopComponent, PageHeadingComponent } from '@shared/components';

@Component({
  selector: 'cca-accessibility',
  standalone: true,
  templateUrl: './accessibility.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, BackToTopComponent],
})
export class AccessibilityComponent {}
