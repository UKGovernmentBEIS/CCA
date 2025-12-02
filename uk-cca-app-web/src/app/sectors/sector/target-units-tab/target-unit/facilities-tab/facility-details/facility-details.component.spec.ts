import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/dom';

import { mockFacilityDetails } from '../testing/mock-data';
import { FacilityDetailsComponent } from './facility-details.component';

describe('FacilityDetailsComponent', () => {
  let component: FacilityDetailsComponent;
  let fixture: ComponentFixture<FacilityDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacilityDetailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            facilityDetails: mockFacilityDetails,
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FacilityDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render all section titles', () => {
    expect(screen.getAllByText('Facility details')).toHaveLength(2);
    expect(screen.getByText('Subsistence fees')).toBeInTheDocument();
  });

  it('should render "Facility details" section', () => {
    const list = document.querySelectorAll("[data-testid='facility-details'] div");

    const elements = [];

    list.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['Site name', 'Fac 1'],
      ['Address', 'address line 1city505050country'],
    ]);
  });

  it('should render "Subsistence fees" section', () => {
    const list = document.querySelectorAll("[data-testid='subsistence-fees'] div");

    const elements = [];

    list.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['Subsistence fees start date', '01/01/2024'],
      ['Scheme exit date', '02/02/2024'],
    ]);
  });
});
