import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { NotificationTemplatesService } from 'cca-api';

import { mockNotificationTemplateViewDTO } from '../../testing/mock-data';
import { EmailViewComponent } from './email-view.component';

describe('EmailViewComponent', () => {
  let component: EmailViewComponent;
  let fixture: ComponentFixture<EmailViewComponent>;
  let notificationTemplatesService: jest.Mocked<Partial<NotificationTemplatesService>>;

  beforeEach(async () => {
    notificationTemplatesService = {
      getNotificationTemplateById: jest.fn().mockReturnValue(of(mockNotificationTemplateViewDTO)),
    };

    await TestBed.configureTestingModule({
      imports: [EmailViewComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { params: { templateId: 10 } } } },
        { provide: NotificationTemplatesService, useValue: notificationTemplatesService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EmailViewComponent);
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

  it('should display template name in page heading', () => {
    const pageHeading = fixture.nativeElement.querySelector('netz-page-heading');
    expect(pageHeading.textContent.trim()).toBe(mockNotificationTemplateViewDTO.name);
  });

  it('should render summary component with computed data', () => {
    const summaryComponent = fixture.nativeElement.querySelector('cca-summary');
    expect(summaryComponent).toBeTruthy();
    expect(component.data()).toBeDefined();
  });

  it('should not render content when template is not available', () => {
    notificationTemplatesService.getNotificationTemplateById.mockReturnValue(of(null));

    const newFixture = TestBed.createComponent(EmailViewComponent);
    newFixture.detectChanges();

    const pageHeading = newFixture.nativeElement.querySelector('netz-page-heading');
    expect(pageHeading).toBeFalsy();
  });
});
