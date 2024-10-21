import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '@netz/common/testing';

import { UnderlyingAgreementWaitReviewComponent } from './underlying-agreement-wait-review.component';

describe('UnderlyingAgreementWaitReviewComponent', () => {
  let component: UnderlyingAgreementWaitReviewComponent;
  let fixture: ComponentFixture<UnderlyingAgreementWaitReviewComponent>;
  let page: Page;

  class Page extends BasePage<UnderlyingAgreementWaitReviewComponent> {
    get warningText() {
      return this.query<HTMLDivElement>('govuk-warning-text').textContent.trim();
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({});
    fixture = TestBed.createComponent(UnderlyingAgreementWaitReviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.warningText).toEqual('!Waiting for the regulator to complete the review');
  });
});
