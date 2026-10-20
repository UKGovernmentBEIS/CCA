import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '@netz/common/testing';

import { UnderlyingAgreementWaitActivationComponent } from './underlying-agreement-wait-activation.component';

describe('UnderlyingAgreementWaitActivationComponent', () => {
  let component: UnderlyingAgreementWaitActivationComponent;
  let fixture: ComponentFixture<UnderlyingAgreementWaitActivationComponent>;
  let page: Page;

  class Page extends BasePage<UnderlyingAgreementWaitActivationComponent> {
    get warningText() {
      return this.query<HTMLDivElement>('govuk-warning-text').textContent.trim();
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({});
    fixture = TestBed.createComponent(UnderlyingAgreementWaitActivationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.warningText).toEqual(`!Waiting for the operator's assent/activation`);
  });
});
