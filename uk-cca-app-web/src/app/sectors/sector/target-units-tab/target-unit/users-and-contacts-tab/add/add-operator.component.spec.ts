import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { throwError } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { OperatorUsersInvitationService } from 'cca-api';

import { AddOperatorComponent } from './add-operator.component';

describe('Add operator Component', () => {
  beforeEach(async () => {
    await render(AddOperatorComponent, {
      providers: [provideHttpClient(), provideHttpClientTesting()],
      configureTestBed: (testbed) => {
        testbed.overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ targetUnitId: 1 }) });

        const mockProvider = mockClass(OperatorUsersInvitationService);
        mockProvider.inviteOperatorUserToAccount = jest.fn().mockReturnValue(
          throwError(
            () =>
              new HttpErrorResponse({
                status: 400,
                error: {
                  code: 'AUTHORITY1016',
                  message: '',
                  security: true,
                  data: [[]],
                },
              }),
          ),
        );

        testbed.overrideProvider(OperatorUsersInvitationService, { useValue: mockProvider });
      },
    });
  });

  it('should render preform content', () => {
    expect(screen.getByText('Add an operator user')).toBeInTheDocument();
    expect(screen.getByText('This user will have permission to:')).toBeInTheDocument();
    expect(screen.getByText('view other operator users')).toBeInTheDocument();
    expect(screen.getByText('view all tasks related to this target unit and its facilities')).toBeInTheDocument();
    expect(screen.getByText('view target period reports')).toBeInTheDocument();
    expect(screen.getByText('You should only add a user authorised to perform these actions.')).toBeInTheDocument();
  });

  it('should render form', () => {
    expect(screen.getByTestId('add-operator-form')).toBeInTheDocument();
    expect(document.getElementById('firstName')).toBeInTheDocument();
    expect(document.getElementById('lastName')).toBeInTheDocument();
    expect(document.getElementById('email')).toBeInTheDocument();
    expect(document.getElementById('contactType')).toBeInTheDocument();
  });

  it('should render error when operator already exists', async () => {
    const userEvent = UserEvent.setup();

    await userEvent.type(document.getElementById('firstName'), 'Operator');
    await userEvent.type(document.getElementById('lastName'), 'Admin');
    await userEvent.type(document.getElementById('email'), 'regulator_admin@cca.uk');
    await userEvent.click(screen.getByText('Submit'));

    expect(
      screen.getAllByText(
        'This email address is already in use. You must enter a different email address for this user to add them as an operator user',
      ),
    ).toHaveLength(2);
  });
});
