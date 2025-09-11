import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { PageHeadingComponent } from '@netz/common/components';
import { GovukDatePipe } from '@netz/common/pipes';
import {
  AccordionComponent,
  AccordionItemComponent,
  SummaryListComponent,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from '@netz/govuk-components';

import { BuyOutAndSurplusInfoService } from 'cca-api';

@Component({
  selector: 'cca-view-surplus-history',
  templateUrl: './surplus-history.component.html',
  standalone: true,
  imports: [
    AccordionComponent,
    AccordionItemComponent,
    PageHeadingComponent,
    SummaryListComponent,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    GovukDatePipe,
    DecimalPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SurplusHistoryComponent {
  private readonly buyoutSurplusInfoService = inject(BuyOutAndSurplusInfoService);
  private readonly activatedRoute = inject(ActivatedRoute);

  private readonly targetUnitId = +this.activatedRoute.snapshot.paramMap.get('targetUnitId');

  readonly targetPeriod = this.activatedRoute.snapshot.paramMap.get('targetPeriodType');
  readonly targetUnitBusinessId = this.activatedRoute.snapshot.data.targetUnit.targetUnitAccountDetails.businessId;

  readonly surplusHistory = toSignal(
    this.buyoutSurplusInfoService.getAllSurplusHistoryByTargetPeriodAndAccountId(
      this.targetUnitId,
      this.targetPeriod as 'TP5' | 'TP6',
    ),
  );
}
