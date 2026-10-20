import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub, BasePage } from '@netz/common/testing';

import Cca3MigrationAccountActivationNotifyOperatorConfirmationComponent from './cca3-migration-account-activation-notify-operator-confirmation.component';

describe('UnderlyingAgreementActivationNotifyOperatorConfirmationComponent', () => {
  let component: Cca3MigrationAccountActivationNotifyOperatorConfirmationComponent;
  let fixture: ComponentFixture<Cca3MigrationAccountActivationNotifyOperatorConfirmationComponent>;
  let page: Page;

  class Page extends BasePage<Cca3MigrationAccountActivationNotifyOperatorConfirmationComponent> {
    get header() {
      return this.query<HTMLDivElement>('.govuk-panel__body');
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [Cca3MigrationAccountActivationNotifyOperatorConfirmationComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Cca3MigrationAccountActivationNotifyOperatorConfirmationComponent);
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
