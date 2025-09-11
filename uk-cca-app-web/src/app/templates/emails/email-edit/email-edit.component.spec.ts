import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { NotificationTemplatesService } from 'cca-api';

import { mockNotificationTemplateViewDTO } from '../../testing/mock-data';
import { EmailEditComponent } from './email-edit.component';

describe('EmailEditComponent', () => {
  let component: EmailEditComponent;
  let fixture: ComponentFixture<EmailEditComponent>;
  let notificationTemplatesService: jest.Mocked<Partial<NotificationTemplatesService>>;
  let router: Router;

  beforeEach(async () => {
    notificationTemplatesService = {
      getNotificationTemplateById: jest.fn().mockReturnValue(of(mockNotificationTemplateViewDTO)),
      updateNotificationTemplate: jest.fn().mockReturnValue(of(null)),
    };

    await TestBed.configureTestingModule({
      imports: [EmailEditComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { params: { templateId: 10 } } } },
        { provide: NotificationTemplatesService, useValue: notificationTemplatesService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(EmailEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call notification service with correct template ID', () => {
    expect(notificationTemplatesService.getNotificationTemplateById).toHaveBeenCalledWith(10);
  });

  it('should display edit template name in page heading', () => {
    const pageHeading = fixture.nativeElement.querySelector('netz-page-heading');
    expect(pageHeading.textContent.trim()).toBe(`Edit ${mockNotificationTemplateViewDTO.name}`);
  });

  it('should initialize form with template data', () => {
    const form = component.form();
    expect(form.get('subject')?.value).toBe(mockNotificationTemplateViewDTO.subject);
    expect(form.get('message')?.value).toBe(mockNotificationTemplateViewDTO.text);
  });

  it('should validate required fields', () => {
    const form = component.form();

    form.get('subject')?.setValue('');
    form.get('message')?.setValue('');

    expect(form.get('subject')?.hasError('required')).toBe(true);
    expect(form.get('message')?.hasError('required')).toBe(true);
  });

  it('should render form fields with correct values', () => {
    const subjectInput = fixture.nativeElement.querySelector('[formControlName="subject"]');
    const messageTextarea = fixture.nativeElement.querySelector('[formControlName="message"]');

    expect(subjectInput).toBeTruthy();
    expect(messageTextarea).toBeTruthy();
  });

  it('should render summary component with computed data', () => {
    const summaryComponent = fixture.nativeElement.querySelector('cca-summary');
    expect(summaryComponent).toBeTruthy();
    expect(component.data()).toBeDefined();
  });

  it('should navigate back with notification state after successful update', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    component.onSubmit();

    expect(navigateSpy).toHaveBeenCalledWith(['..'], {
      relativeTo: expect.any(Object),
      replaceUrl: true,
      queryParams: {},
      state: { notification: true },
    });
  });
});
