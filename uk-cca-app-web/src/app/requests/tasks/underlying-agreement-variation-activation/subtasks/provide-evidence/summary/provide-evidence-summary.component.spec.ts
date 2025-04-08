import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../../../testing/mock-data';
import ProvideEvidenceSummaryComponent from './provide-evidence-summary.component';

describe('ProvideEvidenceSummaryComponent', () => {
  let component: ProvideEvidenceSummaryComponent;
  let fixture: ComponentFixture<ProvideEvidenceSummaryComponent>;
  let store: RequestTaskStore;
  let page: Page;

  class Page extends BasePage<ProvideEvidenceSummaryComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProvideEvidenceSummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(ProvideEvidenceSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Provide evidence');
  });
});
