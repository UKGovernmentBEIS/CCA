import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import UnderlyingAgreementActivationNotifyOperatorConfirmationComponent from './underlying-agreement-activation-notify-operator-confirmation.component';

describe('UnderlyingAgreementActivationNotifyOperatorConfirmationComponent', () => {
  let component: UnderlyingAgreementActivationNotifyOperatorConfirmationComponent;
  let fixture: ComponentFixture<UnderlyingAgreementActivationNotifyOperatorConfirmationComponent>;
  let page: Page;

  class Page extends BasePage<UnderlyingAgreementActivationNotifyOperatorConfirmationComponent> {
    get header() {
      return this.query<HTMLDivElement>('.govuk-panel__body');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UnderlyingAgreementActivationNotifyOperatorConfirmationComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UnderlyingAgreementActivationNotifyOperatorConfirmationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.header.textContent.trim()).toEqual('Underlying agreement activated and sent to operator');
  });
});
