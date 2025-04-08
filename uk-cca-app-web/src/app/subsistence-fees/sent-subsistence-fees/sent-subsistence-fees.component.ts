import { AfterViewChecked, ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { BreadcrumbService } from '@netz/common/navigation';
import { TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { SubsistenceFeesRunPaymentStatusPipe, SubsistenceFeesRunPaymentStatusTagColorPipe } from '@shared/pipes';

import { SubsistenceFeesRunDetailsDTO } from 'cca-api';

import { SectorMoasComponent } from './sector-moas/sector-moas.component';
import { toSentSubsistenceFeesDetails } from './sent-subsistence-fees-details-data';
import { TuMoasComponent } from './tu-moas/tu-moas.component';

@Component({
  selector: 'cca-sent-subsistence-fees',
  templateUrl: './sent-subsistence-fees.component.html',
  standalone: true,
  imports: [
    TagComponent,
    PageHeadingComponent,
    TabsComponent,
    TabLazyDirective,
    SubsistenceFeesRunPaymentStatusPipe,
    SubsistenceFeesRunPaymentStatusTagColorPipe,
    SummaryComponent,
    SectorMoasComponent,
    TuMoasComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SentSubsistenceFeesComponent implements AfterViewChecked {
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly breadcrumbService = inject(BreadcrumbService);

  readonly subFeesDetails = this.activatedRoute.snapshot.data.subFeesDetails as SubsistenceFeesRunDetailsDTO;
  readonly navigationState = { returnUrl: this.router.url };

  readonly data = toSentSubsistenceFeesDetails(this.subFeesDetails);

  ngAfterViewChecked(): void {
    this.breadcrumbService.show([
      {
        text: 'Dashboard',
        link: ['/', 'dashboard'],
      },
      {
        text: 'Subsistence fees',
        link: ['/', 'subsistence-fees'],
        fragment: 'sent-subsistence-fees',
      },
    ]);
  }
}
