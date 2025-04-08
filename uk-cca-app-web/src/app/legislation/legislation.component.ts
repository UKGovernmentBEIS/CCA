import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-legislation',
  standalone: true,
  templateUrl: './legislation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent],
})
export class LegislationComponent {}
