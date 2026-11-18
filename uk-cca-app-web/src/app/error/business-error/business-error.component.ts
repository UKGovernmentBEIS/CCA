import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ErrorPageComponent } from '@shared/components';

import { BusinessErrorService } from './business-error.service';

@Component({
  selector: 'cca-business-error',
  template: `
    @if (businessErrorService.error$ | async; as error) {
      <cca-error-page [heading]="error.heading">
        <p>
          <a class="govuk-link" [routerLink]="error.link" [fragment]="error.fragment">{{ error.linkText }}</a>
        </p>
      </cca-error-page>
    }
  `,
  imports: [ErrorPageComponent, RouterLink, AsyncPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BusinessErrorComponent implements OnDestroy {
  readonly businessErrorService = inject(BusinessErrorService);

  ngOnDestroy() {
    this.businessErrorService.clear();
  }
}
