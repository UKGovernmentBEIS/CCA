import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { ITEM_TYPE_TO_RETURN_TEXT_MAPPER, RequestTaskStore, TYPE_AWARE_STORE } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { mockPreAuditReviewState } from '../../../testing/mock-data';
import { PreAuditReviewDeterminationComponent } from './pre-audit-review-determination.component';

const mockForm = new FormGroup({
  reviewCompletionDate: new FormControl(new Date()),
  furtherAuditNeeded: new FormControl(true),
  reviewComments: new FormControl('blah blah blah'),
});

describe('PreAuditReviewDeterminationComponent', () => {
  let component: PreAuditReviewDeterminationComponent;
  let fixture: ComponentFixture<PreAuditReviewDeterminationComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let tasksApiService: TasksApiService;

  const route = new ActivatedRouteStub();

  const mockTasksApiService = {
    saveRequestTaskAction: vi.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReviewDeterminationComponent],
      providers: [
        provideHttpClient(),
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ITEM_TYPE_TO_RETURN_TEXT_MAPPER, useValue: () => 'Dashboard' },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    tasksApiService = TestBed.inject(TasksApiService);

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockPreAuditReviewState);

    fixture = TestBed.createComponent(PreAuditReviewDeterminationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should render the page heading', () => {
    const heading = fixture.debugElement.query(By.css('h1, h2, h3'));
    expect(heading.nativeElement.textContent).toContain('Pre-audit review determination');
  });

  it('should submit form and navigate to check-your-answers', () => {
    const onSubmitSpy = vi.spyOn(component, 'onSubmit');
    const navigateSpy = vi.spyOn(router, 'navigate');

    const continueButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    continueButton.nativeElement.click();

    component.onSubmit();

    expect(onSubmitSpy).toHaveBeenCalled();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should update form and submit with new values', () => {
    mockForm.patchValue({
      reviewCompletionDate: new Date('2025-06-06'),
      furtherAuditNeeded: true,
      reviewComments: 'blah',
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
  });
});
