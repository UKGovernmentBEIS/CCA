import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';
import { BackToTopComponent } from '@shared/components';

@Component({
  selector: 'cca-accessibility',
  standalone: true,
  templateUrl: './accessibility.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, BackToTopComponent],
})
export class AccessibilityComponent {}
