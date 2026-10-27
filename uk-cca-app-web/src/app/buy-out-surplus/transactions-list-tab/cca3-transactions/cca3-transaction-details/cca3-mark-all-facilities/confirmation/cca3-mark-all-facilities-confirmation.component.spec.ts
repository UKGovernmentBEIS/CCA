import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, withRouterConfig } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { FacilityBuyOutSurplusTransactionDTO } from 'cca-api';

import { mockFacilityTransactionFee, mockFacilityTransactionRefund } from '../../../../testing/cca3-facility-mock-data';
import { Cca3MarkAllStatus } from '../cca3-mark-all-facilities.types';
import { Cca3MarkAllFacilitiesConfirmationComponent } from './cca3-mark-all-facilities-confirmation.component';

@Component({ template: '' })
class TransactionDetailsStubComponent {}

describe('Cca3MarkAllFacilitiesConfirmationComponent', () => {
  let harness: RouterTestingHarness;

  async function setup(
    path: string,
    markAllStatus: Cca3MarkAllStatus,
    transactionDetails: FacilityBuyOutSurplusTransactionDTO,
  ) {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            {
              path: ':transactionId',
              data: { transactionDetails },
              children: [
                { path: '', component: TransactionDetailsStubComponent },
                {
                  path: 'mark-facilities',
                  children: [
                    {
                      path,
                      data: { markAllStatus },
                      children: [{ path: 'confirmation', component: Cca3MarkAllFacilitiesConfirmationComponent }],
                    },
                  ],
                },
              ],
            },
          ],
          withRouterConfig({ paramsInheritanceStrategy: 'always' }),
        ),
      ],
    });

    harness = await RouterTestingHarness.create();
    await harness.navigateByUrl(`/${transactionDetails.id}/mark-facilities/${path}/confirmation`);
  }

  const element = (): HTMLElement => harness.routeNativeElement;

  it('should confirm that all facilities have been marked as paid', async () => {
    await setup('all-paid', 'PAID', mockFacilityTransactionFee);

    expect(element().querySelector('.govuk-panel__title').textContent.trim()).toBe(
      'All facilities have been marked as paid',
    );
    expect(element().querySelector('a').textContent.trim()).toBe(
      `Return to: Buy-out MoA ${mockFacilityTransactionFee.transactionCode}`,
    );
  });

  it('should confirm that all facilities have been marked as refunded', async () => {
    await setup('all-refunded', 'REFUNDED', mockFacilityTransactionRefund);

    expect(element().querySelector('.govuk-panel__title').textContent.trim()).toBe(
      'All facilities have been marked as refunded',
    );
    expect(element().querySelector('a').textContent.trim()).toBe(
      `Return to: Buy-out MoA ${mockFacilityTransactionRefund.transactionCode}`,
    );
  });

  it('should link back to the transaction details', async () => {
    await setup('all-paid', 'PAID', mockFacilityTransactionFee);

    expect(element().querySelector('a').getAttribute('href')).toBe(`/${mockFacilityTransactionFee.id}`);

    element().querySelector('a').click();
    await harness.fixture.whenStable();

    expect(TestBed.inject(Router).url).toBe(`/${mockFacilityTransactionFee.id}`);
  });
});
