import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { PerformanceDataDownloadConfirmationComponent } from './performance-data-download-confirmation.component';

describe('PerformanceDataDownloadConfirmationComponent', () => {
  let component: PerformanceDataDownloadConfirmationComponent;
  let fixture: ComponentFixture<PerformanceDataDownloadConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceDataDownloadConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(PerformanceDataDownloadConfirmationComponent);
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
