import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { PageHeadingComponent } from '@netz/common/components';

@Component({
  selector: 'cca-invalid-link',
  templateUrl: './invalid-link.component.html',
  imports: [PageHeadingComponent, AsyncPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvalidLinkComponent {
  protected readonly errorCode$ = this.route.queryParamMap.pipe(map((queryParamMap) => queryParamMap.get('code')));

  constructor(private readonly route: ActivatedRoute) {}
}
