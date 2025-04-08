import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { PerformanceDataUploadProcessingComponent } from './performance-data-upload-processing.component';

describe('PerformanceDataUploadProcessingComponent', () => {
  let component: PerformanceDataUploadProcessingComponent;
  let fixture: ComponentFixture<PerformanceDataUploadProcessingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceDataUploadProcessingComponent],
      providers: [{ provide: ActivatedRoute, useValue: ActivatedRouteStub }],
    }).compileComponents();

    fixture = TestBed.createComponent(PerformanceDataUploadProcessingComponent);
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
