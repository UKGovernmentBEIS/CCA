import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockFacilityDetails } from '../../test/mock-data';
import { FacilityAuditStore } from '../facility-audit.store';
import { ReasonsComponent } from './reasons.component';

describe('ReasonsComponent', () => {
  let component: ReasonsComponent;
  let fixture: ComponentFixture<ReasonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReasonsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        FacilityAuditStore,
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            facilityDetails: mockFacilityDetails,
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ReasonsComponent);
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
