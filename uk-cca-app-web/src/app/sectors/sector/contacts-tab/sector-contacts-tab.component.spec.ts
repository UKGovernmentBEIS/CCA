import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';

import { SectorAssociationAuthoritiesService, SectorUsersAuthoritiesInfoDTO } from 'cca-api';

import { SectorContactsTabComponent } from './sector-contacts-tab.component';

const mockUserAuthorities: SectorUsersAuthoritiesInfoDTO = {
  editable: true,
  authorities: [
    {
      firstName: 'fn 1',
      lastName: 'ln 1',
      roleCode: 'sector_user_administrator',
      roleName: 'Administrator User',
      contactType: 'contact type 1',
      status: 'ACTIVE',
      userId: '1',
    },
    {
      firstName: 'fn 2',
      lastName: 'ln 2',
      roleCode: 'sector_user_basic_user',
      roleName: 'Basic User',
      contactType: 'contact type 2',
      status: 'ACTIVE',
      userId: '2',
    },
  ],
};

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
    expect(document.getElementById('authorities-form')).toBeTruthy();
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
    expect(document.getElementById('authorities-form')).toBeTruthy();
  });

  it('should not allow user to change status or user type', async () => {
    mockUserAuthorities.authorities.forEach((_, idx) => {
      expect(document.getElementById(`authorities.${idx}.userType`)).toBeNull();
      expect(document.getElementById(`authorities.${idx}.status`)).toBeNull();
    });
  });
});
