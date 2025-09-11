import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import { VariationSubmitConfirmationComponent } from './variation-submit-confirmation.component';

describe('ConfirmationComponent', () => {
  let component: VariationSubmitConfirmationComponent;
  let fixture: ComponentFixture<VariationSubmitConfirmationComponent>;
  let page: Page;

  class Page extends BasePage<VariationSubmitConfirmationComponent> {
    get confirmationMessage() {
      return this.query('.govuk-panel__title').innerHTML.trim();
    }
    get text() {
      return this.query<HTMLParagraphElement>('p').innerHTML.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VariationSubmitConfirmationComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(VariationSubmitConfirmationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show confirmation message and text', () => {
    expect(page.confirmationMessage).toBe('Variation application sent to regulator');
    expect(page.text).toContain('The regulator will review your variation application and contact you.');
  });
});
