import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByRole, getByText } from '@testing';

import { TprFormSubmitExpiredComponent } from './tpr-form-submit-expired.component';

describe('TprFormSubmitExpiredComponent', () => {
  let component: TprFormSubmitExpiredComponent;
  let fixture: ComponentFixture<TprFormSubmitExpiredComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TprFormSubmitExpiredComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(TprFormSubmitExpiredComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render submitted expired heading', () => {
    expect(getByText('Target period report expired', fixture.nativeElement)).toBeTruthy();
    expect(
      getByText(
        'The task to submit the interim target period report for this facility has now expired.',
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
