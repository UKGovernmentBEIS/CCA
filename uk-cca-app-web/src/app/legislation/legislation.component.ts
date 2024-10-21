import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BackToTopComponent } from '@shared/components';
import { PageHeadingComponent } from '@shared/components';

@Component({
  selector: 'cca-legislation',
  standalone: true,
  templateUrl: './legislation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent, BackToTopComponent],
})
export class LegislationComponent {}
