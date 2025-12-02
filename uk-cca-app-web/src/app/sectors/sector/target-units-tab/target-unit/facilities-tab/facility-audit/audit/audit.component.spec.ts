import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockFacilityDetails } from '../../testing/mock-data';
import { FacilityAuditStore } from '../facility-audit.store';
import { AuditComponent } from './audit.component';

describe('AuditComponent', () => {
  let component: AuditComponent;
  let fixture: ComponentFixture<AuditComponent>;
  let store: FacilityAuditStore;

  const mockStore = {
    state: {
      auditRequired: false,
      reasons: [],
      comments: '',
    },
    updateAudit: jest.fn().mockReturnValue(of({ auditRequired: false, reasons: [], comments: '' })),
    init: jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuditComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        FacilityAuditStore,
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            facilityDetails: { ...mockFacilityDetails, facilityBusinessId: 'TEST-123' },
          }),
        },
      ],
    }).compileComponents();

    store = TestBed.inject(FacilityAuditStore);
    store.setState(mockStore.state);

    fixture = TestBed.createComponent(AuditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display correct view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
