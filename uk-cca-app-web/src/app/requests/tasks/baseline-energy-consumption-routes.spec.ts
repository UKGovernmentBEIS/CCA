import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Routes, withRouterConfig } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { RequestTaskState, RequestTaskStore } from '@netz/common/store';
import { BaselineEnergyDraftService } from '@requests/common';

import { BASELINE_ENERGY_CONSUMPTION_ROUTES as APPLICATION_ROUTES } from './underlying-agreement-application/subtasks/manage-facilities/facility/baseline-energy-consumption/baseline-energy-consumption.routes';
import { BASELINE_ENERGY_CONSUMPTION_ROUTES as REVIEW_ROUTES } from './underlying-agreement-review/subtasks/manage-facilities/facility/baseline-energy-consumption/baseline-energy-consumption.routes';
import { BASELINE_ENERGY_CONSUMPTION_ROUTES as VARIATION_ROUTES } from './underlying-agreement-variation/subtasks/manage-facilities/facility/baseline-energy-consumption/baseline-energy-consumption.routes';
import { BASELINE_ENERGY_CONSUMPTION_ROUTES as VARIATION_REGULATOR_LED_ROUTES } from './underlying-agreement-variation-regulator-led/subtasks/manage-facilities/facility/baseline-energy-consumption/baseline-energy-consumption.routes';
import { BASELINE_ENERGY_CONSUMPTION_ROUTES as VARIATION_REVIEW_ROUTES } from './underlying-agreement-variation-review/subtasks/manage-facilities/facility/baseline-energy-consumption/baseline-energy-consumption.routes';

const state = {
  requestTaskItem: {
    requestTask: {
      payload: {
        underlyingAgreement: {
          facilities: [
            { facilityId: 'ADS_1-F00001', facilityDetails: { name: 'Other facility' } },
            {
              facilityId: 'ADS_1-F00002',
              facilityDetails: { name: 'Facility 2' },
              cca3BaselineAndTargets: {
                baselineData: { baselineDate: '2022-01-01' },
                targetComposition: { measurementType: 'ENERGY_KWH' },
                facilityBaselineEnergyConsumption: {
                  totalFixedEnergy: '150',
                  hasVariableEnergy: true,
                  variableEnergyType: 'BY_PRODUCT',
                  variableEnergyConsumptionDataByProduct: [{ productName: 'prod1', productStatus: 'LIVE' }],
                },
              },
            },
          ],
        },
      },
    },
  },
} as unknown as RequestTaskState;

describe.each([
  ['application', APPLICATION_ROUTES],
  ['review', REVIEW_ROUTES],
  ['variation', VARIATION_ROUTES],
  ['variation regulator led', VARIATION_REGULATOR_LED_ROUTES],
  ['variation review', VARIATION_REVIEW_ROUTES],
])('baseline energy consumption routes (%s)', (_, routes: Routes) => {
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter(
          [
            { path: 'facility/:facilityId/baseline-energy-consumption', children: routes },
            { path: 'elsewhere', children: [] },
            { path: 'blocked', canActivate: [() => false], children: [] },
          ],
          withRouterConfig({ paramsInheritanceStrategy: 'always' }),
        ),
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
      ],
    });
    TestBed.inject(RequestTaskStore).setState(state);
    harness = await RouterTestingHarness.create();
  });

  it('should seed the product draft when a product page is opened directly', async () => {
    await harness.navigateByUrl('/facility/ADS_1-F00002/baseline-energy-consumption/delete-product/prod1');

    const draftService = harness.routeDebugElement.injector.get(BaselineEnergyDraftService);
    expect(draftService.draftSignal()?.products).toEqual([{ productName: 'prod1', productStatus: 'LIVE' }]);

    draftService.removeProduct('prod1');
    expect(draftService.draftSignal().products).toEqual([]);
  });

  it('should keep the variable energy choice after visiting a product page', async () => {
    await harness.navigateByUrl('/facility/ADS_1-F00002/baseline-energy-consumption');
    harness.routeDebugElement.componentInstance['form'].controls.hasVariableEnergy.setValue(false);

    await harness.navigateByUrl('/facility/ADS_1-F00002/baseline-energy-consumption/delete-product/prod1');
    await harness.navigateByUrl('/facility/ADS_1-F00002/baseline-energy-consumption');

    expect(harness.routeDebugElement.componentInstance['form'].getRawValue()).toEqual(
      expect.objectContaining({ hasVariableEnergy: false, variableEnergyType: null }),
    );
  });

  it('should keep the draft when moving between product pages', async () => {
    await harness.navigateByUrl('/facility/ADS_1-F00002/baseline-energy-consumption/delete-product/prod1');
    const draftService = harness.routeDebugElement.injector.get(BaselineEnergyDraftService);
    draftService.removeProduct('prod1');

    await harness.navigateByUrl('/facility/ADS_1-F00002/baseline-energy-consumption/add-product');

    expect(draftService.draftSignal().products).toEqual([]);
  });

  it('should keep the draft when a navigation away is cancelled', async () => {
    await harness.navigateByUrl('/facility/ADS_1-F00002/baseline-energy-consumption/delete-product/prod1');
    const draftService = harness.routeDebugElement.injector.get(BaselineEnergyDraftService);
    draftService.removeProduct('prod1');

    await harness.navigateByUrl('/blocked');

    expect(draftService.draftSignal().products).toEqual([]);
  });

  it('should reset the draft to the store values when the pages are opened again', async () => {
    await harness.navigateByUrl('/facility/ADS_1-F00002/baseline-energy-consumption/delete-product/prod1');
    const draftService = harness.routeDebugElement.injector.get(BaselineEnergyDraftService);
    draftService.removeProduct('prod1');

    await harness.navigateByUrl('/elsewhere');
    await harness.navigateByUrl('/facility/ADS_1-F00002/baseline-energy-consumption/delete-product/prod1');

    expect(harness.routeDebugElement.injector.get(BaselineEnergyDraftService)).toBe(draftService);
    expect(draftService.draftSignal().products).toEqual([{ productName: 'prod1', productStatus: 'LIVE' }]);
  });
});
