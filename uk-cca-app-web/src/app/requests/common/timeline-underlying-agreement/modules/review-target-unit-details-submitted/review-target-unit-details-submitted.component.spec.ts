import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { mockUnderlyingAgreementSubmittedRequestAction } from '../../testing/mock-data';
import { ReviewTargetUnitDetailsSubmittedComponent } from './review-target-unit-details-submitted.component';

describe('ReviewTargetUnitDetailsComponent', () => {
  let component: ReviewTargetUnitDetailsSubmittedComponent;
  let fixture: ComponentFixture<ReviewTargetUnitDetailsSubmittedComponent>;
  let store: RequestActionStore;
  let page: Page;

  class Page extends BasePage<ReviewTargetUnitDetailsSubmittedComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [
          ...(Array.from(row.querySelectorAll('dt')) ?? []),
          ...(Array.from(row.querySelectorAll('dd')) ?? []),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim()));
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReviewTargetUnitDetailsSubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnderlyingAgreementSubmittedRequestAction);

    fixture = TestBed.createComponent(ReviewTargetUnitDetailsSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Target unit details');
    expect(page.summaryListValues).toMatchSnapshot();
  });
});
