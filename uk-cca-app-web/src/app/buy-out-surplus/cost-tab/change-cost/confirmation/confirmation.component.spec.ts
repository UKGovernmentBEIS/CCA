import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';

import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let fixture: ComponentFixture<ConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmationComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ targetPeriodType: 'TP7' }),
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationComponent);
    fixture.detectChanges();
  });

  it('confirms the target period cost was updated', () => {
    expect(fixture.nativeElement.querySelector('.govuk-panel').textContent).toContain(
      'TP7 Buyout cost per tCO2e updated',
    );
  });

  it('links back to the Cost tab', () => {
    const returnLink = fixture.nativeElement.querySelector('a');

    expect(returnLink.textContent).toContain('Return to: Buy-out and surplus');
    expect(returnLink.getAttribute('href')).toBe('/buyout-surplus#cost');
  });
});
