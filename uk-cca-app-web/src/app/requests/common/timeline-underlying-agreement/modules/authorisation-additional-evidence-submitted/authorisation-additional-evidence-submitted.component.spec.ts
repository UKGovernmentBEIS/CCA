import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { mockUnderlyingAgreementSubmittedRequestAction } from '../../testing/mock-data';
import { AuthorisationAdditionalEvidenceSubmittedComponent } from './authorisation-additional-evidence-submitted.component';

describe('AuthorisationAdditionalEvidenceComponent', () => {
  let component: AuthorisationAdditionalEvidenceSubmittedComponent;
  let fixture: ComponentFixture<AuthorisationAdditionalEvidenceSubmittedComponent>;
  let store: RequestActionStore;
  let page: Page;

  class Page extends BasePage<AuthorisationAdditionalEvidenceSubmittedComponent> {
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
      imports: [AuthorisationAdditionalEvidenceSubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnderlyingAgreementSubmittedRequestAction);

    fixture = TestBed.createComponent(AuthorisationAdditionalEvidenceSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Authorisation and additional evidence');

    expect(page.summaryListValues).toEqual([
      ['Authorisation', 'authorisationAttachment.xlsx'],
      ['Additional evidence', 'No files provided'],
    ]);
  });
});
