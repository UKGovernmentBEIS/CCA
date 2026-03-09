import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { NotificationTemplatesService } from 'cca-api';

import { TemplateSearchComponent } from '../template-search';
import { activatedRouteMock, mockNotificationTemplateSearchResults } from '../testing/mock-data';
import { EmailsComponent } from './emails.component';

describe('EmailsComponent', () => {
  let component: EmailsComponent;
  let fixture: ComponentFixture<EmailsComponent>;
  let notificationTemplatesService: jest.Mocked<Partial<NotificationTemplatesService>>;
  let routerMock: any;

  beforeEach(async () => {
    notificationTemplatesService = {
      getCurrentUserNotificationTemplates: jest.fn().mockReturnValue(of(mockNotificationTemplateSearchResults)),
    };

    await TestBed.configureTestingModule({
      imports: [EmailsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: NotificationTemplatesService, useValue: notificationTemplatesService },
      ],
    }).compileComponents();

    routerMock = TestBed.inject(Router);
    jest.spyOn(routerMock, 'navigate').mockResolvedValue(true);

    fixture = TestBed.createComponent(EmailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch templates on initialization', () => {
    expect(notificationTemplatesService.getCurrentUserNotificationTemplates).toHaveBeenCalledWith(0, 30, [], null);
  });

  it('should render template search component', () => {
    const templateSearch = fixture.debugElement.query(By.directive(TemplateSearchComponent));
    expect(templateSearch).toBeTruthy();
  });

  it('should render template list when data is available', () => {
    const templateList = fixture.debugElement.query(By.css('[data-testid="template-list-component"]'));
    expect(templateList).toBeTruthy();
  });

  it('should render pagination when templates are available', () => {
    const pagination = fixture.debugElement.query(By.css('cca-pagination'));
    expect(pagination).toBeTruthy();
  });

  it('should render no results message when no templates', () => {
    notificationTemplatesService.getCurrentUserNotificationTemplates.mockReturnValue(of({ templates: [], total: 0 }));

    const newFixture = TestBed.createComponent(EmailsComponent);
    newFixture.detectChanges();

    const noResultsMessage = newFixture.debugElement.query(By.css('p[role="status"]'));
    expect(noResultsMessage.nativeElement.textContent.trim()).toBe('There are no results to show');
  });

  it('should handle API error gracefully', () => {
    notificationTemplatesService.getCurrentUserNotificationTemplates.mockReturnValue(
      throwError(() => new Error('API Error')),
    );

    const newFixture = TestBed.createComponent(EmailsComponent);
    newFixture.detectChanges();

    const templateSearch = newFixture.debugElement.query(By.directive(TemplateSearchComponent));
    expect(templateSearch.componentInstance.templates()).toEqual([]);
    expect(templateSearch.componentInstance.count()).toBe(0);
  });
});
