import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import '@testing-library/jest-dom';
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
import { render, screen } from '@testing-library/angular';

import { BuyOutAndSurplusInfoService, SurplusHistoryDTO } from 'cca-api';

import { SurplusHistoryComponent } from './surplus-history.component';

describe('ViewHistoryComponent', () => {
  const mockSurplusHistoryDTO: SurplusHistoryDTO[] = [
    { surplusGained: '10', comments: 'First', submitter: 'Alice', submissionDate: '2025-04-20T10:00:00Z' },
    { surplusGained: '20', comments: 'Second', submitter: 'Bob', submissionDate: '2025-04-21T11:30:00Z' },
    { surplusGained: '30', comments: 'Third', submitter: 'Carol', submissionDate: '2025-04-22T14:45:00Z' },
  ];

  const mockService = {
    getAllSurplusHistoryByTargetPeriodAndAccountId: jest.fn().mockReturnValue(of(mockSurplusHistoryDTO)),
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: (key: string) => {
          if (key === 'targetUnitId') return '1';
          if (key === 'targetPeriodType') return 'TP6';
          return null;
        },
      },
      data: {
        targetUnit: { targetUnitAccountDetails: { businessId: 'ADS_1-T00001' } },
      },
    },
  };

  beforeEach(async () => {
    await render(SurplusHistoryComponent, {
      imports: [
        GovukDatePipe,
        AccordionComponent,
        AccordionItemComponent,
        SummaryListComponent,
        SummaryListRowDirective,
        SummaryListRowKeyDirective,
        SummaryListRowValueDirective,
        PageHeadingComponent,
      ],
      providers: [
        { provide: BuyOutAndSurplusInfoService, useValue: mockService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    });
  });

  it('should create the component', () => {
    expect(screen.getByText(/TP6 surplus gained change history/)).toBeInTheDocument();
  });

  it('should display heading with business ID and period', () => {
    expect(screen.getByText('ADS_1-T00001')).toBeInTheDocument();
    expect(screen.getByText('TP6 surplus gained change history')).toBeInTheDocument();
  });

  it('should render three accordion items', () => {
    const headers = screen.getAllByRole('button', { name: /Amount changed by/ });
    expect(headers.length).toBe(3);
  });

  it('should display all surplusGained values', () => {
    mockSurplusHistoryDTO.forEach((h) => {
      expect(screen.getByText(h.surplusGained!)).toBeInTheDocument();
    });
  });

  it('should display comments for each history entry', () => {
    mockSurplusHistoryDTO.forEach((h) => {
      expect(screen.getByText(h.comments!)).toBeInTheDocument();
    });
  });
});
