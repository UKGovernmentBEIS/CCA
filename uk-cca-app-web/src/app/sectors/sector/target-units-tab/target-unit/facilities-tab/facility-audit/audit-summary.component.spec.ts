import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { AuditSummaryComponent } from './audit-summary.component';

describe('AuditComponent', () => {
  let component: AuditSummaryComponent;
  let fixture: ComponentFixture<AuditSummaryComponent>;

  const route = { snapshot: { params: { facilityId: 'ADS_1-F00001' } } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuditSummaryComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    fixture = TestBed.createComponent(AuditSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
