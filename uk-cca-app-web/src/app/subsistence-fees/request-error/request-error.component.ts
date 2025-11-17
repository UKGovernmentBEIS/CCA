import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-request-error',
  templateUrl: './request-error.component.html',
  imports: [RouterLink, PageHeadingComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RequestErrorComponent {
  private readonly activatedRoute = inject(ActivatedRoute);

  protected readonly errorCode = this.activatedRoute.snapshot.queryParamMap.get('errorCode');
}
