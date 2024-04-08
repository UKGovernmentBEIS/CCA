import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { OperatorUserDTO } from 'cca-api';

import { UserInputSummaryTemplateComponent } from './user-input-summary.component';

describe('SummaryTemplateComponent', () => {
  let component: UserInputSummaryTemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;

  const mockUserOperatorDTO: OperatorUserDTO = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'test@email.com',
    phoneNumber: {
      countryCode: 'UK44',
      number: '123',
    },
  };

  @Component({
    template: '<cca-user-input-summary-template [userInfo]="userInfo"></cca-user-input-summary-template>',
    standalone: true,
    imports: [UserInputSummaryTemplateComponent],
  })
  class TestComponent {
    userInfo: OperatorUserDTO;
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, TestComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    hostComponent.userInfo = mockUserOperatorDTO;
    component = fixture.debugElement.query(By.directive(UserInputSummaryTemplateComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display user details from registration correctly', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    const compiled = fixture.nativeElement;

    expect(compiled.textContent).toContain('John');
    expect(compiled.textContent).toContain('Doe');
    expect(compiled.textContent).toContain('test@email.com');
    expect(compiled.textContent).toContain('UK44');
    expect(compiled.textContent).toContain('123');
  });

  it('should display user details from noc correctly', async () => {
    const { ...newMockUserOperatorDTO } = mockUserOperatorDTO;

    hostComponent.userInfo = { ...newMockUserOperatorDTO };

    fixture.detectChanges();
    await fixture.whenStable();

    const compiled = fixture.nativeElement;

    expect(compiled.textContent).toContain('John');
    expect(compiled.textContent).toContain('Doe');
    expect(compiled.textContent).toContain('test@email.com');
    expect(compiled.textContent).toContain('UK44');
    expect(compiled.textContent).toContain('123');
  });
});
