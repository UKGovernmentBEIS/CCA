import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { BasePage } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../../../../testing/mock-data';
import FacilitySummaryComponent from './facility-summary.component';

describe('FacilitySummaryComponent', () => {
  let component: FacilitySummaryComponent;
  let fixture: ComponentFixture<FacilitySummaryComponent>;
  let store: RequestTaskStore;
  let page: Page;

  const route: any = {
    snapshot: {
      params: {
        facilityId: 'ADS_1-F00001',
      },
      paramMap: {
        get: jest.fn().mockReturnValue(mockRequestTaskItemDTO.requestTask.id),
      },
      pathFromRoot: [],
    },
  };

  class Page extends BasePage<FacilitySummaryComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FacilitySummaryComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem(mockRequestTaskItemDTO);

    fixture = TestBed.createComponent(FacilitySummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(page.header.textContent.trim()).toEqual('Summary');
  });
});
