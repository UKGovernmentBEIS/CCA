import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { TabLazyDirective, TabsComponent, TagComponent } from '@netz/govuk-components';
import { SummaryComponent } from '@shared/components';
import { StatusColorPipe, StatusPipe } from '@shared/pipes';

import { SubsistenceFeesRunInfoViewService } from 'cca-api';

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
    StatusPipe,
    StatusColorPipe,
    SummaryComponent,
    SectorMoasComponent,
    TuMoasComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SentSubsistenceFeesComponent {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly subsistenceFeesRunInfoViewService = inject(SubsistenceFeesRunInfoViewService);

  private readonly runId = +this.activatedRoute.snapshot.paramMap.get('runId');

  protected readonly subFeesDetails = toSignal(
    this.subsistenceFeesRunInfoViewService.getSubsistenceFeesRunDetailsById(this.runId),
  );

  protected readonly data = computed(() => {
    const details = this.subFeesDetails();
    return toSentSubsistenceFeesDetails(details);
  });
}
