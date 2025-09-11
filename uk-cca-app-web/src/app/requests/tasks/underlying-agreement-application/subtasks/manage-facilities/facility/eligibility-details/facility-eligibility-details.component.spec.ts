import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ChangeDetectionStrategy, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, RouterModule } from '@angular/router';

import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import { underlyingAgreementQuery } from '@requests/common';

import { FacilityEligibilityDetailsComponent } from './facility-eligibility-details.component';

// Mock transform functions
jest.mock('../../../../transform', () => ({
  createRequestTaskActionProcessDTO: jest.fn().mockReturnValue({}),
  toUnderlyingAgreementSavePayload: jest.fn().mockReturnValue({}),
}));

describe('FacilityEligibilityDetailsComponent', () => {
  let fixture: ComponentFixture<FacilityEligibilityDetailsComponent>;
  let store: RequestTaskStore;

  const facilityId = 'ADS_1-F00001';
  const route: any = { snapshot: { params: { facilityId }, pathFromRoot: [] } };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilityEligibilityDetailsComponent, RouterModule.forRoot([])],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
      ],
    })
      .overrideComponent(FacilityEligibilityDetailsComponent, {
        set: {
          changeDetection: ChangeDetectionStrategy.Default,
        },
      })
      .compileComponents();

    store = TestBed.inject(RequestTaskStore);

    // Mock store select methods
    jest.spyOn(store, 'select').mockImplementation((selector) => {
      if (selector === requestTaskQuery.selectRequestTaskPayload) {
        return signal({
          underlyingAgreement: {
            facilities: [
              {
                facilityId,
                facilityDetails: { name: 'Test Facility' },
                eligibilityDetailsAndAuthorisation: {
                  isConnectedToExistingFacility: true,
                  adjacentFacilityId: 'ADS_1-F11111',
                  agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
                  erpAuthorisationExists: true,
                  authorisationNumber: 'authorisation',
                  regulatorName: 'ENVIRONMENT_AGENCY',
                  permitFile: 'test-uuid',
                },
              },
            ],
          },
        });
      }

      if (selector === underlyingAgreementQuery.selectSectionsCompleted) return signal({});
      if (selector === requestTaskQuery.selectRequestTaskId) return signal(123);

      if (selector === underlyingAgreementQuery.selectFacility(facilityId)) {
        return signal({
          facilityId,
          facilityDetails: { name: 'Test Facility' },
          eligibilityDetailsAndAuthorisation: {
            isConnectedToExistingFacility: true,
            adjacentFacilityId: 'ADS_1-F11111',
            agreementType: 'ENVIRONMENTAL_PERMITTING_REGULATIONS',
            erpAuthorisationExists: true,
            authorisationNumber: 'authorisation',
            regulatorName: 'ENVIRONMENT_AGENCY',
            permitFile: 'test-uuid',
          },
        });
      }

      if (selector === underlyingAgreementQuery.selectAttachments) return signal([]);
      if (selector === requestTaskQuery.selectRequestTaskType) return signal('UNDERLYING_AGREEMENT_APPLICATION');

      return signal({});
    });

    fixture = TestBed.createComponent(FacilityEligibilityDetailsComponent);
    fixture.detectChanges();
  });

  it('should test that the form renders properly', () => {
    expect(fixture).toMatchSnapshot();
  });
});
