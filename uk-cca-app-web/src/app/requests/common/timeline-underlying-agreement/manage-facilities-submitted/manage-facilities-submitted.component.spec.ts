import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { mockUnderlyingAgreementSubmittedRequestAction } from '../testing/mock-data';
import { ManageFacilitiesSubmittedComponent } from './manage-facilities-submitted.component';

describe('ManageFacilitiesComponent', () => {
  let component: ManageFacilitiesSubmittedComponent;
  let fixture: ComponentFixture<ManageFacilitiesSubmittedComponent>;
  let store: RequestActionStore;
  let page: Page;

  class Page extends BasePage<ManageFacilitiesSubmittedComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get facilitiesTable() {
      return this.queryAll<HTMLTableRowElement>('tr')
        .map((row) => [
          ...(Array.from(row.querySelectorAll('td')) ?? []),
          ...(Array.from(row.querySelectorAll('th')) ?? []),
        ])
        .map((pair) => pair.map((element) => element?.textContent?.trim()));
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ManageFacilitiesSubmittedComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestActionStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setAction(mockUnderlyingAgreementSubmittedRequestAction);

    fixture = TestBed.createComponent(ManageFacilitiesSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Manage facilities list');

    expect(fixture).toMatchSnapshot('manage-facilities');
  });
});
