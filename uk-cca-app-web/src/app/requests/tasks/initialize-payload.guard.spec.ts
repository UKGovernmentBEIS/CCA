import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { lastValueFrom, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { UNAApplicationRequestTaskPayload } from '@requests/common';

import { initializePayloadGuard } from './initialize-payload.guard';

describe('Initialize Payload Guard', () => {
  let store: RequestTaskStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
      ],
    });

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT' as any } });
    store.setPayload({
      payloadType: 'UNDERLYING_AGREEMENT_APPLICATION_SUBMIT_PAYLOAD',
      sectionsCompleted: {},
      underlyingAgreement: { manageFacilities: { facilityItems: [] } },
    });
  });

  function runGuard(): Observable<unknown> {
    return TestBed.runInInjectionContext(() => initializePayloadGuard());
  }

  it('should pass', async () => {
    await lastValueFrom(runGuard());

    const payload: UNAApplicationRequestTaskPayload = store.select(requestTaskQuery.selectRequestTaskPayload)();
    expect(payload.sectionsCompleted['underlyingAgreementTargetUnitDetails']).toEqual('COMPLETED');
  });
});
