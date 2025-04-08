import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';

import { SentSubsistenceFeesComponent } from './sent-subsistence-fees.component';
import { mockSentSubsistenceFeesDetails } from './testing/mock-data';

describe('SentSubsistenceFeesComponent', () => {
  let component: SentSubsistenceFeesComponent;
  let fixture: ComponentFixture<SentSubsistenceFeesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SentSubsistenceFeesComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, null, {
            subFeesDetails: mockSentSubsistenceFeesDetails,
          }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SentSubsistenceFeesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const detailsValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

    expect(detailsValues).toEqual([
      [
        ['Payment request date', 'Payment status', 'Total (GBP)', 'Outstanding (GBP)'],
        ['01 Jan 2025', 'Awaiting payment', '900 (initially 1,000)', '599'],
      ],
    ]);
  });
});
