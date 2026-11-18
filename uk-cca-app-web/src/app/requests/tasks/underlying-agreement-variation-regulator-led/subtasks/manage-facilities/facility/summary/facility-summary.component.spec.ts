import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../../../../testing/mock-data';
import FacilitySummaryComponent from './facility-summary.component';

describe('FacilitySummaryComponent', () => {
  let component: FacilitySummaryComponent;
  let fixture: ComponentFixture<FacilitySummaryComponent>;
  let store: RequestTaskStore;

  const route = new ActivatedRouteStub({ facilityId: 'ADS_2-F00003' });

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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(fixture.nativeElement.innerHTML).toMatchSnapshot();
  });
});
