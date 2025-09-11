import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { BackToTopComponent } from '@shared/components';

@Component({
  selector: 'cca-accessibility',
  templateUrl: './accessibility.component.html',
  standalone: true,
  imports: [PageHeadingComponent, BackToTopComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccessibilityComponent {}
