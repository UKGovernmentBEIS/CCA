import { ChangeDetectionStrategy, Component } from '@angular/core';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

@Component({
  selector: 'cca-contact-us',
  standalone: true,
  templateUrl: './contact-us.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [PageHeadingComponent],
})
export class ContactUsComponent {}
