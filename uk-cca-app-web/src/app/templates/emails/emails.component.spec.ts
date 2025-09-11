import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { of, throwError } from 'rxjs';

import { NotificationTemplatesService } from 'cca-api';

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

  it('should initialize state with default values', () => {
    expect(component.currentPage()).toBe(1);
    expect(component.pageSize()).toBe(30);
    expect(component.count()).toBe(mockNotificationTemplateSearchResults.total);
    expect(component.templates()).toEqual(mockNotificationTemplateSearchResults.templates);
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

  it('should perform search when form is submitted with valid data', () => {
    component.searchForm.patchValue({ term: 'test search' });
    component.onSearch();

    expect(routerMock.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: { term: 'test search', page: 1 },
        queryParamsHandling: 'merge',
      }),
    );
  });

  it('should not perform search when form is invalid', () => {
    routerMock.navigate.mockClear();

    component.searchForm.patchValue({ term: 'ab' });
    component.onSearch();

    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  it('should handle page change', () => {
    component.onPageChange(2);

    expect(routerMock.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: { page: 2 },
        queryParamsHandling: 'merge',
      }),
    );
  });

  it('should handle page size change and reset to page 1', () => {
    component.onPageSizeChange(25);

    expect(routerMock.navigate).toHaveBeenCalledWith(
      [],
      expect.objectContaining({
        queryParams: { page: 1, pageSize: 25 },
        queryParamsHandling: 'merge',
      }),
    );
  });

  it('should handle API error gracefully', () => {
    notificationTemplatesService.getCurrentUserNotificationTemplates.mockReturnValue(
      throwError(() => new Error('API Error')),
    );

    const newFixture = TestBed.createComponent(EmailsComponent);
    newFixture.detectChanges();

    expect(newFixture.componentInstance.templates()).toEqual([]);
    expect(newFixture.componentInstance.count()).toBe(0);
  });
});
