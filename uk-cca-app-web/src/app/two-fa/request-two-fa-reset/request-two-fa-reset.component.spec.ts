import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@netz/common/testing';

import { RequestTwoFaResetComponent } from './request-two-fa-reset.component';

describe('RequestTwoFaResetComponent', () => {
  let component: RequestTwoFaResetComponent;
  let fixture: ComponentFixture<RequestTwoFaResetComponent>;
  let page: Page;

  class Page extends BasePage<RequestTwoFaResetComponent> {
    get heading() {
      return this.query<HTMLElement>('netz-page-heading h1.govuk-heading-l');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestTwoFaResetComponent, RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(RequestTwoFaResetComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display appropriate title for reset 2FA', () => {
    expect(page.heading.textContent.trim()).toEqual('Request two-factor authentication reset');
  });
});
