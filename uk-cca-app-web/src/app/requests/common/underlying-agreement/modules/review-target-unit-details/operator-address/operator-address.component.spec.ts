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
import { OperatorAddressComponent } from './operator-address.component';

describe('OperatorAddressComponent', () => {
  let component: OperatorAddressComponent;
  let fixture: ComponentFixture<OperatorAddressComponent>;
  let store: RequestTaskStore;
  const taskService: Partial<jest.Mocked<TaskService>> = {
    saveSubtask: jest.fn().mockReturnValue(of({})),
  };
  const saveSubTaskSpy = jest.spyOn(taskService, 'saveSubtask');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorAddressComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: TaskService, useValue: taskService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockRequestTaskState);

    fixture = TestBed.createComponent(OperatorAddressComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the page heading', async () => {
    const heading = screen.getByRole('heading', { name: 'Operator address' });
    expect(heading).toBeInTheDocument();
  });

  it('should render the address input fields', async () => {
    const addressInput = screen.getByTestId('account-address-input');
    expect(addressInput).toBeInTheDocument();
    expect(screen.getByText('Address line 1')).toBeInTheDocument();
    expect(screen.getByText('Address line 2 (optional)')).toBeInTheDocument();
    expect(screen.getByText('Town or city')).toBeInTheDocument();
    expect(screen.getByText('County (optional)')).toBeInTheDocument();
    expect(screen.getByText('Postcode')).toBeInTheDocument();
    expect(screen.getByText('Country')).toBeInTheDocument();
  });

  it('should submit form and call saveSubtask method', async () => {
    const user = UserEvent.setup();
    await user.click(screen.getByText('Continue'));
    expect(saveSubTaskSpy).toHaveBeenCalledTimes(1);
  });
});
