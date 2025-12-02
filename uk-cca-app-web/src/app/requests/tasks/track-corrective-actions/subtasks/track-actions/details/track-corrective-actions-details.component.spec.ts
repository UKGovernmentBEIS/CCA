import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { TasksApiService } from '@requests/common';

import { TargetUnitAccountInfoViewService } from 'cca-api';

import { mockTrackCorrectiveActionsState } from '../../../testing/mock-data';
import { TrackCorrectiveActionsDetailsComponent } from './track-corrective-actions-details.component';

describe('TrackCorrectiveActionsDetailsComponent', () => {
  let component: TrackCorrectiveActionsDetailsComponent;
  let fixture: ComponentFixture<TrackCorrectiveActionsDetailsComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let tasksApiService: TasksApiService;
  let targetUnitAccountInfoViewService: jest.Mocked<Partial<TargetUnitAccountInfoViewService>>;

  const route: any = {
    snapshot: {
      params: { actionId: '1' },
      paramMap: {},
      pathFromRoot: [],
    },
  };

  const mockTasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  const mockForm = new FormGroup({
    actionCarriedOutDate: new FormControl(new Date()),
    comments: new FormControl('blah blah blah'),
    evidenceFiles: new FormControl([]),
  });

  beforeEach(async () => {
    targetUnitAccountInfoViewService = {
      getTargetUnitAccountDetailsById: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [TrackCorrectiveActionsDetailsComponent],
      providers: [
        provideHttpClient(),
        RequestTaskStore,
        { provide: TasksApiService, useValue: mockTasksApiService },
        { provide: ActivatedRoute, useValue: route },
        { provide: TargetUnitAccountInfoViewService, useValue: targetUnitAccountInfoViewService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    tasksApiService = TestBed.inject(TasksApiService);

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockTrackCorrectiveActionsState);

    fixture = TestBed.createComponent(TrackCorrectiveActionsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = fixture.debugElement.query(By.css('h1, h2, h3'));
    expect(heading.nativeElement.textContent).toContain('Corrective actions carried out details');
  });

  it('should submit form and navigate to check-your-answers', () => {
    const onSubmitSpy = jest.spyOn(component, 'onSubmit');
    const navigateSpy = jest.spyOn(router, 'navigate');

    const continueButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    continueButton.nativeElement.click();

    component.onSubmit();

    expect(onSubmitSpy).toHaveBeenCalled();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['../check-your-answers'], { relativeTo: route });
  });

  it('should update form and submit with new values', () => {
    mockForm.patchValue({
      ...mockForm.value,
      comments: 'updated comments',
      actionCarriedOutDate: new Date('2025-05-15'),
    });

    component.onSubmit();

    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
  });
});
