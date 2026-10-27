import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { facilityDetailsFixtures, mockHistoricalFacility } from '../testing/facility-details-fixtures';
import { FacilityDetailsComponent, FacilityDetailsRouteData } from './facility-details.component';

describe('CCA3 facility details presentation', () => {
  let fixture: ComponentFixture<FacilityDetailsComponent>;
  let data: BehaviorSubject<Partial<FacilityDetailsRouteData>>;

  beforeEach(async () => {
    data = new BehaviorSubject({});
    await TestBed.configureTestingModule({
      imports: [FacilityDetailsComponent],
      providers: [provideRouter([]), { provide: ActivatedRoute, useValue: { data, snapshot: { data: {} } } }],
    }).compileComponents();
    fixture = TestBed.createComponent(FacilityDetailsComponent);
  });

  it.each(facilityDetailsFixtures)(
    'renders $node with semantic headings and the historical event date',
    async ({ details, calculation }) => {
      data.next({ facilityDetails: details, facilityCalculation: calculation });
      await fixture.whenStable();
      const root: HTMLElement = fixture.nativeElement;
      expect(root.querySelector('h1').textContent.trim()).toBe('West London Site');
      expect(root.querySelectorAll('h2')).toHaveLength(2);
      expect(root.querySelector('.govuk-caption-m').textContent).toContain(
        calculation.scenario === 'primary-surplus' ? '2 May 2027, 3:35pm' : '2 Sept 2027, 3:35pm',
      );
      expect(root.querySelector('dt').textContent).toBe('Run ID');
      expect(root.querySelector('dd').textContent).toContain('BOS-TP8004');
      expect(root.textContent).not.toContain('Due date');
      expect(root.querySelector('a')).toBeNull();
      const help = root.querySelector('details');
      if (calculation.scenario.startsWith('primary')) {
        expect(help).toBeNull();
      } else {
        expect(help.open).toBe(false);
        expect(help.querySelector('summary').textContent).toContain('I need help with the');
        expect(help.querySelectorAll('li').length).toBeGreaterThan(1);
        help.querySelector('summary').click();
        expect(help.open).toBe(true);
        help.querySelector('summary').click();
        expect(help.open).toBe(false);
      }
    },
  );

  it('updates the reused page when resolved identity and calculation change', async () => {
    data.next({
      facilityDetails: facilityDetailsFixtures[0].details,
      facilityCalculation: facilityDetailsFixtures[0].calculation,
    });
    await fixture.whenStable();
    data.next({
      facilityDetails: mockHistoricalFacility({ siteName: 'New historical site', targetPeriod: 'TP8' }),
      facilityCalculation: { scenario: 'secondary-target-not-met-refund', grossBuyoutFee: '101.0000' },
    });
    await fixture.whenStable();
    const root: HTMLElement = fixture.nativeElement;
    expect(root.querySelector('h1').textContent).toBe('New historical site');
    expect(root.textContent).toContain('TP8-V3 (Target not met)');
    expect(root.textContent).toContain('101.00');
    expect(root.textContent).not.toContain('Date banked');
    expect(root.querySelectorAll('h2')[1].textContent).toBe('Refund');
  });

  it('does not render a guessed layout or type when the historical calculation mapping is absent', async () => {
    data.next({ facilityDetails: mockHistoricalFacility() });
    await fixture.whenStable();
    expect(fixture.nativeElement.querySelector('h1')).toBeNull();
    expect(fixture.nativeElement.querySelector('dl')).toBeNull();
  });
});
