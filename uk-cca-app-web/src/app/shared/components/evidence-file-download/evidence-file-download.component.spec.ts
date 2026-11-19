import { provideHttpClient, withXhr } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { defer, firstValueFrom, of, take } from 'rxjs';

import { ActivatedRouteStub, mockClass, testSchedulerFactory } from '@netz/common/testing';
import { Mocked } from 'vitest';

import { SubsistenceFeesMoAReceivedAmountControllerService } from 'cca-api';

import { EvidenceFileDownloadComponent } from './evidence-file-download.component';

describe('Buy-out and surplus evidence downloads', () => {
  it.each([
    {
      resource: 'facility detail from the route',
      params: { transactionId: 10, transactionDetailId: 13, uuid: 'evidence-uuid' },
      data: {},
      endpoint: 'transaction-details/13',
    },
    {
      resource: 'account detail from the resolver',
      params: { transactionId: 10, uuid: 'evidence-uuid' },
      data: { evidenceResource: 'transactionDetail', transactionDetails: { detailId: 14 } },
      endpoint: 'transaction-details/14',
    },
    {
      resource: 'transaction',
      params: { transactionId: 10, uuid: 'evidence-uuid' },
      data: {},
      endpoint: 'transactions/10',
    },
  ])('uses the $resource evidence endpoint', async ({ params, data, endpoint }) => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub(params, {}, data) },
      ],
    });
    const component = TestBed.createComponent(EvidenceFileDownloadComponent).componentInstance;
    const downloadUrl = firstValueFrom(component.url$);
    const http = TestBed.inject(HttpTestingController);
    const request = http.expectOne((req) => req.url === `/api/v1.0/buy-out-surplus/${endpoint}/evidence`);
    expect(request.request.params.get('fileEvidenceUuid')).toBe('evidence-uuid');
    request.flush({ token: 'download-token', tokenExpirationMinutes: 1 });
    await expect(downloadUrl).resolves.toBe('/api/v1.0/file-evidences/download-token');
    http.verify();
  });
});

describe('EvidenceFileDownloadComponent', () => {
  let component: EvidenceFileDownloadComponent;
  let fixture: ComponentFixture<EvidenceFileDownloadComponent>;
  let subsistenceFeesMoAReceivedAmountControllerService: Mocked<SubsistenceFeesMoAReceivedAmountControllerService>;

  beforeEach(async () => {
    Object.defineProperty(window, 'onfocus', { set: vi.fn() });

    subsistenceFeesMoAReceivedAmountControllerService = mockClass(SubsistenceFeesMoAReceivedAmountControllerService);
    subsistenceFeesMoAReceivedAmountControllerService.generateGetMoaReceivedAmountEvidenceFileToken.mockReturnValue(
      of({ token: 'abce', tokenExpirationMinutes: 1 }),
    );

    const activatedRoute = new ActivatedRouteStub({ moaId: 1, uuid: 'xyz', taskId: 11 });

    await TestBed.configureTestingModule({
      imports: [EvidenceFileDownloadComponent],
      providers: [
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRoute },
        {
          provide: SubsistenceFeesMoAReceivedAmountControllerService,
          useValue: subsistenceFeesMoAReceivedAmountControllerService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EvidenceFileDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    await expect(firstValueFrom(component.url$)).resolves.toEqual('/api/v1.0/file-evidences/abce');
  });

  it('should refresh the download link', async () => {
    subsistenceFeesMoAReceivedAmountControllerService.generateGetMoaReceivedAmountEvidenceFileToken
      .mockClear()
      .mockImplementation(() => {
        let subscribes = 0;

        return defer(() => {
          subscribes += 1;

          return subscribes === 1
            ? of({ token: 'abcf', tokenExpirationMinutes: 1 })
            : subscribes === 2
              ? of({ token: 'abcd', tokenExpirationMinutes: 2 })
              : of({ token: 'abce', tokenExpirationMinutes: 1 });
        });
      });

    testSchedulerFactory().run(({ expectObservable }) =>
      expectObservable(component.url$.pipe(take(3))).toBe('a 59s 999ms b 119s 999ms (c|)', {
        a: '/api/v1.0/file-evidences/abcf',
        b: '/api/v1.0/file-evidences/abcd',
        c: '/api/v1.0/file-evidences/abce',
      }),
    );
  });
});
