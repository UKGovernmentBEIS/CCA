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
import { ResponsiblePersonComponent } from './responsible-person.component';

describe('ResponsiblePersonComponent', () => {
  let component: ResponsiblePersonComponent;
  let fixture: ComponentFixture<ResponsiblePersonComponent>;
  let store: RequestTaskStore;
  const route = new ActivatedRouteStub();

  const taskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  const saveSubTaskSpy = jest.spyOn(taskService, 'saveSubtask');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResponsiblePersonComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TaskService, useValue: taskService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(ResponsiblePersonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', () => {
    const heading = screen.getByRole('heading', { name: 'Responsible person' });
    expect(heading).toBeInTheDocument();
  });

  it('should render the responsible person input fields', () => {
    expect(screen.getByText('Email address')).toBeInTheDocument();
    expect(screen.getByText('First name')).toBeInTheDocument();
    expect(screen.getByText('Last name')).toBeInTheDocument();
    expect(screen.getByText('Job title (optional)')).toBeInTheDocument();
    expect(screen.getAllByText('Phone number')).toHaveLength(2);
    expect(screen.getByText('The responsible person address is the same as the operator address')).toBeInTheDocument();
  });

  it('should submit form and call saveSubtask method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Continue'));
    expect(saveSubTaskSpy).toHaveBeenCalledTimes(1);
  });

  it('should call the saveSubtask method when the form is updated in a valid way and then submitted', async () => {
    const saveSubTaskSpy = jest.spyOn(taskService, 'saveSubtask');
    const user = UserEvent.setup();
    await user.type(screen.getByText('Email address'), 'newemail@test.com');
    await user.type(screen.getByText('First name'), 'NewFirstName');
    await user.type(screen.getByText('Last name'), 'NewLastName');
    await user.type(screen.getByText('Job title (optional)'), 'NewJobTitle');

    await user.click(screen.getByText('Continue'));
    expect(saveSubTaskSpy).toHaveBeenCalledTimes(1);
  });
});
