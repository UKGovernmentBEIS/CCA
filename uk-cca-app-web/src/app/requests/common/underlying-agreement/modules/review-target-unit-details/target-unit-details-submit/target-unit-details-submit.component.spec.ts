import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import { mockRequestTaskState } from '../../../testing';
import { TargetUnitDetailsSubmitComponent } from './target-unit-details-submit.component';

describe('TargetUnitDetailsSubmitComponent', () => {
  let component: TargetUnitDetailsSubmitComponent;
  let fixture: ComponentFixture<TargetUnitDetailsSubmitComponent>;
  let store: RequestTaskStore;
  const route = new ActivatedRouteStub();

  const taskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  const saveSubTaskSpy = jest.spyOn(taskService, 'saveSubtask');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TargetUnitDetailsSubmitComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TaskService, useValue: taskService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(TargetUnitDetailsSubmitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = screen.getByRole('heading', { name: 'Target unit details' });
    expect(heading).toBeInTheDocument();
  });

  it('should render the target unit details input fields', () => {
    expect(screen.getByText('Operator name')).toBeInTheDocument();
    expect(screen.getByText('Operator type')).toBeInTheDocument();
    expect(screen.getByText('Company registration number')).toBeInTheDocument();
    expect(screen.getByText('Subsector')).toBeInTheDocument();
  });

  it('should submit form and call saveSubtask method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Continue'));
    expect(saveSubTaskSpy).toHaveBeenCalledTimes(1);
  });

  it('should call the saveSubtask method when the form is updated in a valid way and then submitted', async () => {
    const user = UserEvent.setup();
    await user.type(screen.getByText('Operator name'), 'New Operator');
    await user.type(screen.getByText('Company registration number'), '12345678');
    await user.type(screen.getByText('Subsector'), 'New Subsector');
    saveSubTaskSpy.mockClear();

    await user.click(screen.getByText('Continue'));
    expect(saveSubTaskSpy).toHaveBeenCalledTimes(1);
  });
});
