import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { map } from 'rxjs';

import { GovukTableColumn, TableComponent } from '@netz/govuk-components';
import { FormatGbpPipe } from '@shared/pipes';
import { SchemeVersionEnum } from '@shared/types';

import { BuyOutAndSurplusCostInfoService } from 'cca-api';

@Component({
  selector: 'cca-cost-tab',
  templateUrl: './cost-tab.component.html',
  imports: [TableComponent, FormatGbpPipe, RouterLink],
})
export class CostTabComponent {
  private readonly buyOutAndSurplusCostInfoService = inject(BuyOutAndSurplusCostInfoService);

  protected readonly columns: GovukTableColumn[] = [
    { field: 'businessId', header: 'Target period', isHeader: true },
    { field: 'buyOutCost', header: 'Buy-out cost per tCO2e', isNumeric: true },
    { field: 'actions', header: 'Actions', widthClass: 'govuk-!-text-align-right' },
  ];

  protected readonly buyOutCosts = toSignal(
    this.buyOutAndSurplusCostInfoService.getBuyOutCosts(SchemeVersionEnum.CCA_3).pipe(
      map((costs) =>
        costs.slice().sort((a, b) =>
          (a.businessId ?? '').localeCompare(b.businessId ?? '', 'en-GB', {
            numeric: true,
            sensitivity: 'base',
          }),
        ),
      ),
    ),
    { initialValue: [] },
  );
}
