import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { PerformanceDataDownloadGeneratingComponent } from './performance-data-download-generating.component';

describe('PerformanceDataDownloadGeneratingComponent', () => {
  let component: PerformanceDataDownloadGeneratingComponent;
  let fixture: ComponentFixture<PerformanceDataDownloadGeneratingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceDataDownloadGeneratingComponent],
      providers: [{ provide: ActivatedRoute, useValue: ActivatedRouteStub }],
    }).compileComponents();

    fixture = TestBed.createComponent(PerformanceDataDownloadGeneratingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary values', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should show return to link', () => {
    expect(screen.getByText('Return to: Dashboard')).toBeInTheDocument();
  });
});
