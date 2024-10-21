import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockTargetUnitAccountDetails, mockUnderlyingAgreementDetails } from '../../../specs/fixtures/mock';
import { ActiveTargetUnitStore } from '../active-target-unit.store';
import { TargetUnitComponent } from './target-unit.component';

describe('TargetUnitComponent', () => {
  let store: ActiveTargetUnitStore;

  beforeEach(async () => {
    await render(TargetUnitComponent, {
      providers: [ActiveTargetUnitStore, provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ targetUnitId: 1 }) });
        store = testbed.inject(ActiveTargetUnitStore);
        store.setState({
          targetUnitAccountDetails: mockTargetUnitAccountDetails,
          underlyingAgreementDetails: mockUnderlyingAgreementDetails,
        });
      },
    });
  });

  it('should render all headings', () => {
    expect(screen.getByRole('heading', { name: 'Target unit name 01 Live' })).toBeVisible();
    expect(screen.getByRole('heading', { name: 'Target unit details' })).toBeVisible();
    expect(screen.getByRole('heading', { name: 'Financial independence' })).toBeVisible();
    expect(screen.getByRole('heading', { name: 'Operator address' })).toBeVisible();
    expect(screen.getByRole('heading', { name: 'Responsible Person' })).toBeVisible();
    expect(screen.getByRole('heading', { name: 'Administrative contact details' })).toBeVisible();
  });

  it('should display the tabs', () => {
    const tabTitles = ['Details', 'Workflow history', 'Reports', 'Users and contacts'];

    expect(screen.getAllByRole('tab')).toHaveLength(4);
    expect(screen.getAllByRole('tab')[0]).toHaveTextContent(tabTitles[0]);
    expect(screen.getAllByRole('tab')[1]).toHaveTextContent(tabTitles[1]);
    expect(screen.getAllByRole('tab')[2]).toHaveTextContent(tabTitles[2]);
    expect(screen.getAllByRole('tab')[3]).toHaveTextContent(tabTitles[3]);
  });

  it('should display the headers', () => {
    const tabTitles = ['Details', 'Workflow history', 'Reports', 'Users and contacts'];

    expect(screen.getAllByRole('tab')).toHaveLength(4);
    expect(screen.getAllByRole('tab')[0]).toHaveTextContent(tabTitles[0]);
    expect(screen.getAllByRole('tab')[1]).toHaveTextContent(tabTitles[1]);
    expect(screen.getAllByRole('tab')[2]).toHaveTextContent(tabTitles[2]);
    expect(screen.getAllByRole('tab')[3]).toHaveTextContent(tabTitles[3]);
  });

  it('should display the target unit details', () => {
    const summaryValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

    expect(summaryValues).toEqual([
      [
        ['Downloadable version', 'Activation date'],
        ['ADS_1-T00002 Underlying Agreement v1.pdf', '30 Sep 2024'],
      ],
      [
        [
          'Operator name',
          'Operator type',
          'Company registration number',
          'Standard industrial classification (SIC) code',
          'Subsector',
        ],
        ['Target unit name 01', 'Limited company', '2636942', '01110', ''],
      ],
      [['Status'], ['Financially independent']],
      [['Address'], ['Line 1  Line 2  City  County  SE23 6FH  GR']],
      [
        ['First name', 'Last name', 'Job title', 'Email address', 'Phone number', 'Address'],
        [
          'John',
          'Doe',
          'Job title 1',
          'responsible@test.gr',
          'GR (30) 6999999999',
          'Line 1  Line 2  City 2  County2  SE23 6FH2  GR',
        ],
      ],
      [
        ['First name', 'Last name', 'Job title', 'Email address', 'Phone number', 'Address'],
        [
          'John',
          'Doe',
          'Job title 2',
          'administrative@test.gr',
          'GR (30) 6999999999',
          'Line 2  Line 22  City 2  County2  SE23 6FH2  GR',
        ],
      ],
    ]);
  });
});
