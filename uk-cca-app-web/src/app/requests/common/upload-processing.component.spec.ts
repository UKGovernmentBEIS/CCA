import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { UploadProcessingComponent } from './upload-processing.component';

describe('PerformanceDataUploadProcessingComponent', () => {
  let component: UploadProcessingComponent;
  let fixture: ComponentFixture<UploadProcessingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadProcessingComponent],
      providers: [{ provide: ActivatedRoute, useValue: ActivatedRouteStub }],
    }).compileComponents();

    fixture = TestBed.createComponent(UploadProcessingComponent);
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
