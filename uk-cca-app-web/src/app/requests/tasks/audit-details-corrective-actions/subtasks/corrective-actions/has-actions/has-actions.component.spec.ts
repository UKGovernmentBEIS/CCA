import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { TasksApiService } from '@requests/common';

import { mockAuditDetailsAndCorrectiveActionsState } from '../../../testing/mock-data';
import { HasActionsComponent } from './has-actions.component';

describe('HasActionsComponent', () => {
  let component: HasActionsComponent;
  let fixture: ComponentFixture<HasActionsComponent>;
  let store: RequestTaskStore;
  let router: Router;
  let tasksApiService: TasksApiService;

  const mockTasksApiService = {
    saveRequestTaskAction: jest.fn().mockReturnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HasActionsComponent],
      providers: [
        provideHttpClient(),
        RequestTaskStore,
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: TasksApiService, useValue: mockTasksApiService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    tasksApiService = TestBed.inject(TasksApiService);

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockAuditDetailsAndCorrectiveActionsState);

    fixture = TestBed.createComponent(HasActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = fixture.debugElement.query(By.css('h1, h2, h3'));
    expect(heading.nativeElement.textContent).toContain(
      'Did the final audit report identify any corrective actions the operator must complete?',
    );
  });

  it('should submit form and navigate to actions', () => {
    const onSubmitSpy = jest.spyOn(component, 'onSubmit');
    const navigateSpy = jest.spyOn(router, 'navigate');

    const continueButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    continueButton.nativeElement.click();

    component.onSubmit();

    expect(onSubmitSpy).toHaveBeenCalled();
    expect(tasksApiService.saveRequestTaskAction).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['../actions'], { relativeTo: new ActivatedRouteStub() });
  });
});
