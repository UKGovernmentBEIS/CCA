import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ErrorPageComponent } from '@shared/error-page/error-page.component';

import { LinkDirective } from 'govuk-components';

import { BusinessErrorService } from './business-error.service';

@Component({
  selector: 'cca-business-error',
  template: `
    @if (businessErrorService.error$ | async; as error) {
      <cca-error-page [heading]="error.heading">
        <p class="govuk-body">
          <a govukLink [routerLink]="error.link" [fragment]="error.fragment">{{ error.linkText }}</a>
        </p>
      </cca-error-page>
    }
    `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [ErrorPageComponent, LinkDirective, RouterModule, AsyncPipe]
})
export class BusinessErrorComponent implements OnDestroy {
  constructor(readonly businessErrorService: BusinessErrorService) { }

  ngOnDestroy(): void {
    this.businessErrorService.clear();
  }
}
