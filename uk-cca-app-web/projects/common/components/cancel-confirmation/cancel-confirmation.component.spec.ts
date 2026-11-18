import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@netz/common/testing';

import { CancelConfirmationComponent } from './cancel-confirmation.component';

describe('CancelConfirmationComponent', () => {
  let page: Page;
  let component: CancelConfirmationComponent;
  let fixture: ComponentFixture<CancelConfirmationComponent>;

  class Page extends BasePage<CancelConfirmationComponent> {
    get confirmationMessage() {
      return this.query('.govuk-panel__title').innerHTML.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, CancelConfirmationComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelConfirmationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show confirmation message', () => {
    expect(page.confirmationMessage).toBe('Task cancelled');
  });
});
