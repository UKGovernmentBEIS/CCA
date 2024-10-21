import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SectorAssociationAuthoritiesService } from 'cca-api';

import { mockUserAuthorities } from 'src/app/regulators/testing/mock-data';

import { SectorContactsTabComponent } from './sector-contacts-tab.component';

describe('SectorContactsTabComponent', () => {
  let component: SectorContactsTabComponent;
  let fixture: ComponentFixture<SectorContactsTabComponent>;

  const route = new ActivatedRouteStub();

  const sectorAssociationAuthoritiesService = {
    getSectorUserAuthoritiesBySectorAssociationId: jest.fn().mockReturnValue(of(mockUserAuthorities)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorContactsTabComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: SectorAssociationAuthoritiesService,
          useValue: sectorAssociationAuthoritiesService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorContactsTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render authorities form', () => {
    expect(document.getElementById('authorities-form')).toBeInTheDocument();
  });

  it("should have as many rows as users' authorities", async () => {
    const rows = document.getElementById('authorities-form').getElementsByTagName('tr');
    expect(rows).toHaveLength(mockUserAuthorities.authorities.length + 1);
  });
});

describe('SectorContactsTabComponent non editable', () => {
  let fixture: ComponentFixture<SectorContactsTabComponent>;

  const route = new ActivatedRouteStub();

  const sectorAssociationAuthoritiesService = {
    getSectorUserAuthoritiesBySectorAssociationId: jest
      .fn()
      .mockReturnValue(of({ ...mockUserAuthorities, editable: false })),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorContactsTabComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        {
          provide: SectorAssociationAuthoritiesService,
          useValue: sectorAssociationAuthoritiesService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorContactsTabComponent);
    fixture.detectChanges();
  });

  it('should render authorities form', () => {
    expect(document.getElementById('authorities-form')).toBeInTheDocument();
  });

  it('should not allow user to change status or user type', async () => {
    mockUserAuthorities.authorities.forEach((_, idx) => {
      expect(document.getElementById(`authorities.${idx}.userType`)).toBeNull();
      expect(document.getElementById(`authorities.${idx}.status`)).toBeNull();
    });
  });
});
