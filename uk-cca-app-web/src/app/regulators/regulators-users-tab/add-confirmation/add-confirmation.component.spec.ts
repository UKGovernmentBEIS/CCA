import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByTestId, getByText } from '@testing';

import { AddConfirmationComponent } from './add-confirmation.component';

describe('AddRegulatorConfirmationComponent', () => {
  let fixture: ComponentFixture<AddConfirmationComponent>;
  const email = 'regulator_1245@cca.uk';

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddConfirmationComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub(null, { email }),
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AddConfirmationComponent);
    fixture.detectChanges();
  });

  it('should render', () => {
    expect(getByTestId('confirmation-screen')).toBeTruthy();
    const text = `An account confirmation email has been sent to ${email}`;
    expect(getByText(text)).toBeTruthy();
  });
});
