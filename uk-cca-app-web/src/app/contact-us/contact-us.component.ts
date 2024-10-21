import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@shared/components';

@Component({
  selector: 'cca-contact-us',
  standalone: true,
  templateUrl: './contact-us.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent],
})
export class ContactUsComponent {}
