import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { PerformanceDataUploadSubmittedComponent } from './performance-data-upload-submitted.component';
import { mockRequestActionStatePerformanceDataUpload } from './testing/mock-data';

describe('PerformanceDataUploadSubmittedComponent', () => {
  let component: PerformanceDataUploadSubmittedComponent;
  let fixture: ComponentFixture<PerformanceDataUploadSubmittedComponent>;
  let store: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceDataUploadSubmittedComponent],
      providers: [RequestActionStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState(mockRequestActionStatePerformanceDataUpload);
    fixture = TestBed.createComponent(PerformanceDataUploadSubmittedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });
});
