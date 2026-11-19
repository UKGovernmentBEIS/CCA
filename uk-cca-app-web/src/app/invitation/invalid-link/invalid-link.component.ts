import { Component, input } from '@angular/core';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-invalid-link',
  templateUrl: './invalid-link.component.html',
  imports: [PageHeadingComponent],
})
export class InvalidLinkComponent {
  /** `code` query param: the error code of the rejected invitation link. */
  readonly code = input<string>();
}
