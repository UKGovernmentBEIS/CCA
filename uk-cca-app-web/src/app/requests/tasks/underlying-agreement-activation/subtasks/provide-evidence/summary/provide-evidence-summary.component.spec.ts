import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockRequestTaskItemDTO } from '../../../testing/mock-data';
import ProvideEvidenceSummaryComponent from './provide-evidence-summary.component';

describe('ProvideEvidenceSummaryComponent', () => {
  let component: ProvideEvidenceSummaryComponent;
  let fixture: ComponentFixture<ProvideEvidenceSummaryComponent>;
  let store: RequestTaskStore;

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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
