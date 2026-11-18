import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@netz/common/testing';

import { ConfirmationSharedComponent } from './confirmation.component';

describe('ConfirmationSharedComponent', () => {
  let component: ConfirmationSharedComponent;
  let fixture: ComponentFixture<ConfirmationSharedComponent>;
  let componentRef: ComponentRef<ConfirmationSharedComponent>;
  let page: Page;

  class Page extends BasePage<ConfirmationSharedComponent> {
    get confirmationMessage() {
      return this.query('.govuk-panel__title').innerHTML.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [ControlContainer],
    }).compileComponents();

    fixture = TestBed.createComponent(ConfirmationSharedComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;

    componentRef.setInput('title', 'The notification has been recalled');
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show confirmation message', () => {
    expect(page.confirmationMessage).toBe('The notification has been recalled');
  });
});
