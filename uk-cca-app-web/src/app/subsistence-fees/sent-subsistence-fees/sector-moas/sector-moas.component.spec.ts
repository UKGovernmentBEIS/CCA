import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { mockSectorMoas, mockSentSubsistenceFeesDetails } from '../testing/mock-data';
import { SectorMoasComponent } from './sector-moas.component';

describe('SectorMoasComponent', () => {
  let component: SectorMoasComponent;
  let fixture: ComponentFixture<SectorMoasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SectorMoasComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            subFeesDetails: mockSentSubsistenceFeesDetails,
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SectorMoasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate with correct number of table rows', () => {
    component.state.set({
      currentPage: 0,
      pageSize: 30,
      subsistenceFeesMoas: mockSectorMoas,
      totalItems: mockSectorMoas.length,
    });

    fixture.detectChanges();

    expect(document.querySelectorAll('.govuk-table__row')).toHaveLength(component.state().totalItems + 1);
  });
});
