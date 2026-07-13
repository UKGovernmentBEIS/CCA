import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByRole, getByText } from '@testing';

import { TprFormSubmitConfirmationComponent } from './tpr-form-submit-confirmation.component';

describe('TprFormSubmitConfirmationComponent', () => {
  let component: TprFormSubmitConfirmationComponent;
  let fixture: ComponentFixture<TprFormSubmitConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TprFormSubmitConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(TprFormSubmitConfirmationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render submitted confirmation heading and what happens next section', () => {
    expect(getByText('Target period report submitted', fixture.nativeElement)).toBeTruthy();
    expect(getByRole('heading', { name: 'What happens next' }, fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'The service will calculate and store the performance for the facility using the energy and throughput data you entered.',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
    expect(
      getByText(
        'You can find the results of these calculations on the Reports tab for the facility.',
        fixture.nativeElement,
      ),
    ).toBeTruthy();
  });

  it('should render a return to dashboard link', () => {
    const link = getByRole('link', { name: 'Return to: Dashboard' }, fixture.nativeElement);

    expect(link).toBeTruthy();
    expect(link.textContent?.trim()).toBe('Return to: Dashboard');
  });
});
