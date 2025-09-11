import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { UnderlyingAgreementSubmitConfirmationComponent } from './confirmation.component';

describe('UnderlyingAgreementSubmitConfirmationComponent', () => {
  let component: UnderlyingAgreementSubmitConfirmationComponent;
  let fixture: ComponentFixture<UnderlyingAgreementSubmitConfirmationComponent>;
  let page: Page;

  class Page extends BasePage<UnderlyingAgreementSubmitConfirmationComponent> {
    get confirmationMessage() {
      return this.query('.govuk-panel__title').innerHTML.trim();
    }
    get text() {
      return this.query<HTMLParagraphElement>('p').innerHTML.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UnderlyingAgreementSubmitConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(UnderlyingAgreementSubmitConfirmationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show confirmation message and text', () => {
    expect(page.confirmationMessage).toBe('Application sent to regulator');
    expect(page.text).toContain('The regulator will review your application and contact you.');
  });
});
