import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

@Component({
  selector: 'cca-email-link-invalid',
  templateUrl: './email-link-invalid.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [RouterModule, PageHeadingComponent],
})
export class EmailLinkInvalidComponent {}
