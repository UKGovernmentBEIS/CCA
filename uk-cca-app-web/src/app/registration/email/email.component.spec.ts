import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { SharedModule } from '@shared/shared.module';

import { OperatorUsersRegistrationService } from 'cca-api';

import { VerificationSentComponent } from '../verification-sent/verification-sent.component';
import { EmailComponent } from './email.component';

describe('EmailComponent', () => {
  let component: EmailComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({ template: '<cca-email></cca-email>' })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, PageHeadingComponent, RouterTestingModule],
      declarations: [TestComponent, EmailComponent, VerificationSentComponent],
      providers: [
        {
          provide: OperatorUsersRegistrationService,
          useValue: { sendVerificationEmail: jest.fn((email) => of(email)) },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(EmailComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit only valid emails', () => {
    const element: HTMLElement = fixture.nativeElement;
    const emailInput = fixture.debugElement.query(By.css('input'));
    const button = element.querySelector<HTMLButtonElement>('button');
    const getVerificationSent = () => fixture.debugElement.query(By.directive(VerificationSentComponent));

    const setValueAndSubmit = (value) => {
      emailInput.triggerEventHandler('input', { target: { value } });
      button.click();
      fixture.detectChanges();
    };

    setValueAndSubmit('');
    setValueAndSubmit('asd');
    setValueAndSubmit('a'.repeat(300));
    expect(getVerificationSent());

    setValueAndSubmit('test@example.com');
    expect(getVerificationSent());
  });
});
