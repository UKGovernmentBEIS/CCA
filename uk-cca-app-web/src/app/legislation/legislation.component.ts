import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-legislation',
  templateUrl: './legislation.component.html',
  imports: [PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LegislationComponent {}
