import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { mockUnderlyingAgreementVariationSubmittedRequestAction } from '../../testing/mock-data';
import { VariationDetailsSubmittedComponent } from './variation-details-submitted.component';

describe('VariationDetailsComponent', () => {
  let component: VariationDetailsSubmittedComponent;
  let fixture: ComponentFixture<VariationDetailsSubmittedComponent>;
  let store: RequestActionStore;
  let page: Page;

  class Page extends BasePage<VariationDetailsSubmittedComponent> {
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VariationDetailsSubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnderlyingAgreementVariationSubmittedRequestAction);

    fixture = TestBed.createComponent(VariationDetailsSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Variation details');

    expect(page.summaryListValues).toEqual([
      ['Target Unit/Facility changes', 'Amend the name of the operator/organisation'],
      ['Amend the baseline and target due to', 'a structural change'],
      ['Explain what you are changing and the reason for the changes', 'No reason'],
    ]);
  });
});
