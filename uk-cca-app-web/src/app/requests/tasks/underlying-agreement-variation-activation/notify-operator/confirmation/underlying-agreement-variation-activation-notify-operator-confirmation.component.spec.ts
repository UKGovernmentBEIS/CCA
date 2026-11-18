import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import UnderlyingAgreementVariationActivationNotifyOperatorConfirmationComponent from './underlying-agreement-variation-activation-notify-operator-confirmation.component';

describe('UnderlyingAgreementVariationActivationNotifyOperatorConfirmationComponent', () => {
  let component: UnderlyingAgreementVariationActivationNotifyOperatorConfirmationComponent;
  let fixture: ComponentFixture<UnderlyingAgreementVariationActivationNotifyOperatorConfirmationComponent>;
  let page: Page;

  class Page extends BasePage<UnderlyingAgreementVariationActivationNotifyOperatorConfirmationComponent> {
    get header() {
      return this.query<HTMLDivElement>('.govuk-panel__body');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UnderlyingAgreementVariationActivationNotifyOperatorConfirmationComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UnderlyingAgreementVariationActivationNotifyOperatorConfirmationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.header.textContent.trim()).toEqual('Underlying agreement variation activated and sent to operator');
  });
});
