import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionState, RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { Cca2TerminationAccountProcessingSubmittedRequestActionPayload, RequestActionDTO } from 'cca-api';

import { CcaTerminationProcessingSubmittedComponent } from './cca-termination-processing-submitted.component';

const payload: Cca2TerminationAccountProcessingSubmittedRequestActionPayload = {
  payloadType: 'CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD',
  businessId: 'ADS_1-T00001',
  excludedFacilities: [
    { id: 1, facilityBusinessId: 'ADS_1-F00001', siteName: 'Facility One' },
    { id: 2, facilityBusinessId: 'ADS_1-F00002', siteName: 'Facility Two' },
  ],
};

const requestActionDTO: RequestActionDTO = {
  id: 1,
  type: 'CCA2_TERMINATION_ACCOUNT_PROCESSING_SUBMITTED',
  payload,
  requestId: 'ADS_1-T00001-CCA2-TERM-1',
  requestType: 'CCA2_TERMINATION_ACCOUNT_PROCESSING',
  requestAccountId: 1,
  competentAuthority: 'ENGLAND',
  creationDate: '2025-10-29T14:52:24.845314Z',
};

const stateMock: RequestActionState = {
  action: requestActionDTO,
};

describe('CcaTerminationProcessingSubmittedComponent', () => {
  let component: CcaTerminationProcessingSubmittedComponent;
  let fixture: ComponentFixture<CcaTerminationProcessingSubmittedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CcaTerminationProcessingSubmittedComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    const actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(stateMock);

    fixture = TestBed.createComponent(CcaTerminationProcessingSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
