import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorMessageComponent } from './error-message.component';

describe('ErrorMessageComponent', () => {
  let component: ErrorMessageComponent;
  let fixture: ComponentFixture<ErrorMessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorMessageComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it.each([
    [{ required: 'Enter the payment date', isIncomplete: 'Enter a full date' }, ['Enter a full date']],
    [{ required: 'Enter the payment date', isUnrealDate: 'Enter a real date' }, ['Enter a real date']],
    [{ required: 'Enter the payment date', isEmpty: 'Enter a date' }, ['Enter the payment date']],
    [
      { required: 'Enter the payment date', isUnrealDate: 'Enter a real date', external: 'Another problem' },
      ['Another problem', 'Enter a real date'],
    ],
    [{ required: 'Enter a value', external: 'Another problem' }, ['Another problem', 'Enter a value']],
  ])('shows specific date errors while preserving unrelated messages: %j', (errors, expectedMessages) => {
    const originalErrors = { ...errors };
    fixture.componentRef.setInput('errors', errors);
    fixture.detectChanges();

    const messages = fixture.nativeElement.querySelectorAll('.govuk-error-message > span');
    expect(Array.from(messages, (message: HTMLElement) => message.textContent.replace(/\s+/g, ' ').trim())).toEqual(
      expectedMessages.map((message) => `Error: ${message}`),
    );
    expect(component.errors()).toEqual(originalErrors);
  });
});
