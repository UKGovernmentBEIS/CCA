import { provideHttpClient, withXhr } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { config } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getSummaryListData } from '@testing';

import { ReceivedAmountStore } from '../received-amount.store';
import { mockReceivedAmountStoreState } from '../testing/mock-data';
import { CheckYourAnswersComponent } from './check-your-answers.component';

describe('CheckYourAnswersComponent', () => {
  let component: CheckYourAnswersComponent;
  let fixture: ComponentFixture<CheckYourAnswersComponent>;
  let sectorMoasReceivedAmountStore: ReceivedAmountStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckYourAnswersComponent],
      providers: [
        ReceivedAmountStore,
        provideHttpClient(withXhr()),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ moaId: 1 }) },
      ],
    }).compileComponents();

    sectorMoasReceivedAmountStore = TestBed.inject(ReceivedAmountStore);
    sectorMoasReceivedAmountStore.setState(mockReceivedAmountStoreState);

    fixture = TestBed.createComponent(CheckYourAnswersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to the confirmation page only after the save succeeds', () => {
    const httpTestingController = TestBed.inject(HttpTestingController);
    const navigateSpy = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

    fixture.nativeElement.querySelector('button').click();

    const request = httpTestingController.expectOne((req) => req.method === 'POST');
    expect(request.request.body).toEqual(mockReceivedAmountStoreState.details);
    expect(navigateSpy).not.toHaveBeenCalled();

    request.flush(null, { status: 204, statusText: 'No Content' });

    expect(navigateSpy).toHaveBeenCalledWith(['..', 'confirmation'], {
      relativeTo: TestBed.inject(ActivatedRoute),
      replaceUrl: true,
    });
    httpTestingController.verify();
  });

  it('should stay on the page and pass the original error on when the save fails', async () => {
    const httpTestingController = TestBed.inject(HttpTestingController);
    const navigateSpy = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    const onUnhandledError = vi.fn();
    const previousHandler = config.onUnhandledError;
    config.onUnhandledError = onUnhandledError;

    fixture.nativeElement.querySelector('button').click();
    httpTestingController
      .expectOne((req) => req.method === 'POST')
      .flush({ code: 'ERROR' }, { status: 400, statusText: 'Bad Request' });
    await new Promise((resolve) => setTimeout(resolve));
    config.onUnhandledError = previousHandler;

    expect(onUnhandledError).toHaveBeenCalledWith(expect.objectContaining({ status: 400, error: { code: 'ERROR' } }));
    expect(navigateSpy).not.toHaveBeenCalled();
    httpTestingController.verify();
  });

  it('should display the correct amount data', () => {
    const detailsValues = getSummaryListData(fixture.nativeElement);

    expect(detailsValues).toEqual([
      [
        [
          'Sector',
          'Transaction ID',
          'Added payment (GBP)',
          'New received payment (GBP)',
          'Comments',
          'Uploaded evidence',
        ],
        ['ADS_52-Aerospace_52', 'CCACM01201', '350', '5,450', 'mplah mplah', 'No files provided'],
      ],
    ]);
  });
});
