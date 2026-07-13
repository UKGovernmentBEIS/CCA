import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';

import { ErrorSummaryComponent } from './error-summary.component';

describe('ErrorSummaryComponent', () => {
  let component: ErrorSummaryComponent;
  let fixture: ComponentFixture<ErrorSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorSummaryComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorSummaryComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('errorSummaryInfo', {
      message: 'Test error message',
      link: '/test-link',
      linkText: 'Go to test link',
    });

    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render error message', () => {
    const message = (fixture.nativeElement as HTMLElement).querySelector('.govuk-error-summary__list p');
    expect(message?.textContent).toContain('Test error message');
  });

  it('should render link when link and/or linkText is provided', () => {
    const link = (fixture.nativeElement as HTMLElement).querySelector('a');
    expect(link).toBeTruthy();
    expect(link?.textContent).toContain('Go to test link');
  });
});
