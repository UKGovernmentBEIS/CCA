import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TaskService } from '@netz/common/forms';
import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { screen } from '@testing-library/angular';
import UserEvent from '@testing-library/user-event';

import { mockRequestTaskState } from '../../../testing/mock-data';
import { BaselineExistsComponent } from './baseline-exists.component';

describe('BaselineExistsComponent', () => {
  let component: BaselineExistsComponent;
  let fixture: ComponentFixture<BaselineExistsComponent>;
  let store: RequestTaskStore;
  let router: Router;

  const route = new ActivatedRouteStub();

  const taskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };

  const saveSubTaskSpy = jest.spyOn(taskService, 'saveSubtask');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BaselineExistsComponent, RouterTestingModule.withRoutes([])],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TaskService, useValue: taskService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'url', 'get').mockReturnValue('tasks/1/underlying-agreement-application/target-period-5');

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(BaselineExistsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct heading and caption text', () => {
    const heading = screen.getByRole('heading', {
      name: 'Are you providing baseline and target information for TP5 (2021 to 2022)?',
    });
    const caption = screen.getByText('TP5 (2021-2022)');

    expect(heading).toBeInTheDocument();
    expect(caption).toBeInTheDocument();
  });

  it('should render radio buttons for selecting if baseline exists', () => {
    const yesRadioButton = screen.getByLabelText('Yes');
    const noRadioButton = screen.getByLabelText('No');

    expect(yesRadioButton).toBeInTheDocument();
    expect(noRadioButton).toBeInTheDocument();
  });

  it('should handle radio button selection', async () => {
    const user = UserEvent.setup();
    const yesRadioButton = screen.getByLabelText('Yes');
    const noRadioButton = screen.getByLabelText('No');

    await user.click(yesRadioButton);
    expect(yesRadioButton).toBeChecked();
    expect(noRadioButton).not.toBeChecked();

    await user.click(noRadioButton);
    expect(noRadioButton).toBeChecked();
    expect(yesRadioButton).not.toBeChecked();
  });

  it('should call saveSubtask once when "Yes" is selected and form is submitted', async () => {
    const user = UserEvent.setup();
    const continueButton = screen.getByRole('button', { name: 'Continue' });

    await user.click(screen.getByLabelText('Yes'));
    await user.click(continueButton);

    expect(saveSubTaskSpy).toHaveBeenCalledTimes(1);
  });

  it('should call saveSubtask once when "No" is selected and form is submitted', async () => {
    const user = UserEvent.setup();
    const continueButton = screen.getByRole('button', { name: 'Continue' });

    await user.click(screen.getByLabelText('No'));
    await user.click(continueButton);

    expect(saveSubTaskSpy).toHaveBeenCalledTimes(1);
  });

  afterEach(() => {
    saveSubTaskSpy.mockClear();
  });

  it('should show notification banner only in the UnA application flow', () => {
    expect(component.showNotificationBanner).toBe(true);
    jest.spyOn(router, 'url', 'get').mockReturnValue('');
    fixture.detectChanges();
    expect(component.showNotificationBanner).toBe(true);
  });
});
