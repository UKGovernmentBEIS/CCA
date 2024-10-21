import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { PageHeadingComponent } from '@shared/components';

@Component({
  selector: 'cca-invalid-link',
  templateUrl: './invalid-link.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [PageHeadingComponent, AsyncPipe],
})
export class InvalidLinkComponent {
  errorCode$ = this.route.queryParamMap.pipe(map((queryParamMap) => queryParamMap.get('code')));

  constructor(private readonly route: ActivatedRoute) {}
}
