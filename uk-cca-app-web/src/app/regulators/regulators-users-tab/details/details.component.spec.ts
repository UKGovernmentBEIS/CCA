import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { fireEvent, screen, waitFor, within } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { AuthoritiesService, RegulatorUsersService } from 'cca-api';

import {
  mockDetailsRouteDataAdd,
  mockDetailsRouteDataView,
  mockRegulatorBasePermissions,
  mockRegulatorUserState,
} from '../../testing/mock-data';
import { addUserState, editorUserState, viewerUserState } from '../../testing/mock-details-store';
import { DetailsComponent } from './details.component';
import { DetailsStore } from './details.store';

describe('RegulatorDetailsComponent', () => {
  const routeEdit = new ActivatedRouteStub({ userId: mockRegulatorUserState.userId }, null);
  const routeEdit2 = new ActivatedRouteStub({ userId: '123' }, null);
  const routeView = new ActivatedRouteStub({ userId: mockRegulatorUserState.userId }, null);
  const routeAdd = new ActivatedRouteStub(null, null, mockDetailsRouteDataAdd);

  let authoritiesService: Partial<jest.Mocked<AuthoritiesService>>;
  let regulatorUsersService: Partial<jest.Mocked<RegulatorUsersService>>;
  let authStore: AuthStore;
  let detailsStore: DetailsStore;
  let fixture: ComponentFixture<DetailsComponent>;

  async function bootstrap(
    route: ActivatedRouteStub,
    opts: { add: boolean; edit: boolean } = { add: false, edit: true },
  ) {
    authoritiesService = {
      getRegulatorRoles: jest.fn().mockReturnValue(of(mockRegulatorBasePermissions)),
    };

    regulatorUsersService = {
      inviteRegulatorUserToCA: jest.fn().mockReturnValue(of(null)),
      updateCurrentRegulatorUser: jest.fn().mockReturnValue(of(null)),
      updateRegulatorUserByCaAndId: jest.fn().mockReturnValue(of(null)),
    };

    const component = await render(DetailsComponent, {
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        DetailsStore,
        {
          provide: ActivatedRoute,
          useValue: route,
        },
        {
          provide: AuthoritiesService,
          useValue: authoritiesService,
        },
        {
          provide: RegulatorUsersService,
          useValue: regulatorUsersService,
        },
      ],
      configureTestBed: (testbed) => {
        authStore = testbed.inject(AuthStore);
        authStore.setUserState(mockRegulatorUserState);
        detailsStore = testbed.inject(DetailsStore);

        if (opts.add) detailsStore.setState(addUserState);
        else opts.edit ? detailsStore.setState(editorUserState) : detailsStore.setState(viewerUserState);
      },
    });

    fixture = component.fixture;
    fixture.detectChanges();
  }

  it('should render header', async () => {
    await bootstrap(routeEdit);
    expect(screen.getByText('User details')).toBeInTheDocument();
  });

  it('should render details properly', async () => {
    await bootstrap(routeEdit);
    expect(document.getElementById('user.firstName')).toHaveValue(editorUserState.user.firstName);
    expect(document.getElementById('user.lastName')).toHaveValue(editorUserState.user.lastName);
    expect(document.getElementById('user.jobTitle')).toHaveValue(editorUserState.user.jobTitle);
    expect(document.getElementById('user.email')).toHaveValue(editorUserState.user.email);
    expect(document.getElementById('user.email')).toBeDisabled();
    expect(document.getElementById('user.phoneNumber')).toHaveValue(editorUserState.user.phoneNumber);
    expect(document.getElementById('user.mobileNumber')).toHaveValue(editorUserState.user.mobileNumber);
    within(document.querySelector('cca-file-input')).getAllByText(editorUserState.user.signature.name);
    expect(document.getElementById('regulator_administrator')).toBeInTheDocument();
    expect(document.getElementById('regulator_basic_user')).toBeInTheDocument();
  });

  it('should show radios if editable is true', async () => {
    await bootstrap(routeEdit);
    expect(document.querySelectorAll("input[type='radio']")).toHaveLength(12);
  });

  it('change permissions on administrator click', async () => {
    const user = UserEvent.setup();
    await bootstrap(routeEdit);
    await user.click(document.getElementById('regulator_administrator'));
    expect(document.getElementById('permissions.MANAGE_SECTOR_ASSOCIATIONS-optionEXECUTE')).toBeChecked();
    expect(document.getElementById('permissions.ASSIGN_REASSIGN_TASKS-optionEXECUTE')).toBeChecked();
    expect(document.getElementById('permissions.MANAGE_USERS_AND_CONTACTS-optionEXECUTE')).toBeChecked();
    expect(document.getElementById('permissions.MANAGE_SECTOR_USERS-optionEXECUTE')).toBeChecked();
    expect(document.getElementById('permissions.ADMIN_TERMINATION_SUBMISSION-optionEXECUTE')).toBeChecked();
    expect(document.getElementById('permissions.ADMIN_TERMINATION_PEER_REVIEW-optionEXECUTE')).toBeChecked();
    expect(document.getElementById('permissions.MANAGE_SECTOR_ASSOCIATIONS-optionNONE')).not.toBeChecked();
    expect(document.getElementById('permissions.ASSIGN_REASSIGN_TASKS-optionNONE')).not.toBeChecked();
    expect(document.getElementById('permissions.MANAGE_USERS_AND_CONTACTS-optionNONE')).not.toBeChecked();
    expect(document.getElementById('permissions.MANAGE_SECTOR_USERS-optionNONE')).not.toBeChecked();
    expect(document.getElementById('permissions.ADMIN_TERMINATION_SUBMISSION-optionNONE')).not.toBeChecked();
    expect(document.getElementById('permissions.ADMIN_TERMINATION_PEER_REVIEW-optionNONE')).not.toBeChecked();
  });

  it('change permissions on base user click', async () => {
    const user = UserEvent.setup();
    await bootstrap(routeEdit);
    await user.click(document.getElementById('regulator_basic_user'));
    expect(document.getElementById('permissions.MANAGE_SECTOR_ASSOCIATIONS-optionEXECUTE')).not.toBeChecked();
    expect(document.getElementById('permissions.ASSIGN_REASSIGN_TASKS-optionEXECUTE')).toBeChecked();
    expect(document.getElementById('permissions.MANAGE_USERS_AND_CONTACTS-optionEXECUTE')).not.toBeChecked();
    expect(document.getElementById('permissions.MANAGE_SECTOR_USERS-optionEXECUTE')).not.toBeChecked();
    expect(document.getElementById('permissions.ADMIN_TERMINATION_SUBMISSION-optionEXECUTE')).not.toBeChecked();
    expect(document.getElementById('permissions.ADMIN_TERMINATION_PEER_REVIEW-optionEXECUTE')).not.toBeChecked();
    expect(document.getElementById('permissions.MANAGE_SECTOR_ASSOCIATIONS-optionNONE')).toBeChecked();
    expect(document.getElementById('permissions.ASSIGN_REASSIGN_TASKS-optionNONE')).not.toBeChecked();
    expect(document.getElementById('permissions.MANAGE_USERS_AND_CONTACTS-optionNONE')).toBeChecked();
    expect(document.getElementById('permissions.MANAGE_SECTOR_USERS-optionNONE')).toBeChecked();
    expect(document.getElementById('permissions.ADMIN_TERMINATION_SUBMISSION-optionNONE')).toBeChecked();
    expect(document.getElementById('permissions.ADMIN_TERMINATION_PEER_REVIEW-optionNONE')).toBeChecked();
  });

  it('should render viewer properly', async () => {
    await bootstrap(routeView, { add: false, edit: false });
    expect(document.getElementById('user.firstName')).toHaveValue(viewerUserState.user.firstName);
    expect(document.getElementById('user.lastName')).toHaveValue(viewerUserState.user.lastName);
    expect(document.getElementById('user.jobTitle')).toHaveValue(viewerUserState.user.jobTitle);
    expect(document.getElementById('user.email')).toHaveValue(viewerUserState.user.email);
    expect(document.getElementById('user.email')).toBeDisabled();
    expect(document.getElementById('user.phoneNumber')).toHaveValue(viewerUserState.user.phoneNumber);
    expect(document.getElementById('user.mobileNumber')).toHaveValue(viewerUserState.user.mobileNumber);
    within(document.querySelector('cca-file-input')).getAllByText(viewerUserState.user.signature.name);
    expect(document.getElementById('regulator_administrator')).not.toBeInTheDocument();
    expect(document.getElementById('regulator_basic_user')).not.toBeInTheDocument();
    expect(screen.getAllByText('✔')).toHaveLength(
      Object.keys(mockDetailsRouteDataView.permissions.userPermissions.permissions).length,
    );
  });

  it('should render add regulator property', async () => {
    await bootstrap(routeAdd, { add: true, edit: true });
    expect(document.getElementById('user.firstName')).toHaveValue('');
    expect(document.getElementById('user.lastName')).toHaveValue('');
    expect(document.getElementById('user.jobTitle')).toHaveValue('');
    expect(document.getElementById('user.email')).toHaveValue('');
    expect(document.getElementById('user.email')).toBeEnabled();
    expect(document.getElementById('user.phoneNumber')).toHaveValue('');
    expect(document.getElementById('user.mobileNumber')).toHaveValue('');
    expect(document.getElementById('regulator_administrator')).toBeInTheDocument();
    expect(document.getElementById('regulator_basic_user')).toBeInTheDocument();
  });

  it('should submit new regulator', async () => {
    await bootstrap(routeAdd, { add: true, edit: true });
    const spy = jest.spyOn(regulatorUsersService, 'inviteRegulatorUserToCA');
    const user = UserEvent.setup();
    await user.type(document.getElementById('user.firstName'), 'George');
    await user.type(document.getElementById('user.lastName'), 'Mitau');
    await user.type(document.getElementById('user.jobTitle'), 'Job Title for George');
    await user.type(document.getElementById('user.email'), 'georgemitau@cca.uk');
    await user.type(document.getElementById('user.phoneNumber'), '123123123');
    await user.click(document.getElementById('regulator_basic_user'));
    const signature = new File(['image bytes'], 'sample.bmp');
    const uploader = screen.getByLabelText(/Signature/);
    await waitFor(() =>
      fireEvent.change(uploader, {
        target: { files: [signature] },
      }),
    );
    await user.click(screen.getByText('Submit'));
    expect(spy).toHaveBeenCalled();
  });

  it('should show first name error if empty was submitted', async () => {
    const user = UserEvent.setup();
    await bootstrap(routeEdit);
    await user.clear(document.getElementById('user.firstName'));
    await user.click(screen.getByText('Save'));
    expect(screen.getByText('There is a problem')).toBeInTheDocument();
    expect(screen.getAllByText("Enter user's first name")).toHaveLength(2);
  });

  it('should show last name error if empty was submitted', async () => {
    const user = UserEvent.setup();
    await bootstrap(routeEdit);
    await user.clear(document.getElementById('user.lastName'));
    await user.click(screen.getByText('Save'));
    expect(screen.getByText('There is a problem')).toBeInTheDocument();
    expect(screen.getAllByText("Enter user's last name")).toHaveLength(2);
  });

  it('should show multiple errors if present', async () => {
    const user = UserEvent.setup();
    await bootstrap(routeEdit);
    await user.clear(document.getElementById('user.firstName'));
    await user.clear(document.getElementById('user.lastName'));
    await user.click(screen.getByText('Save'));
    expect(screen.getByText('There is a problem')).toBeInTheDocument();
    expect(screen.getAllByText("Enter user's first name")).toHaveLength(2);
    expect(screen.getAllByText("Enter user's last name")).toHaveLength(2);
  });

  it('should show signature file error', async () => {
    const user = UserEvent.setup();
    await bootstrap(routeEdit);
    await user.click(screen.getByText('Delete'));
    await user.click(screen.getByText('Save'));
    expect(screen.getByText('There is a problem')).toBeInTheDocument();
    expect(screen.getAllByText('Select a file')).toHaveLength(2);
  });

  it('should submit a valid form for current user', async () => {
    const user = UserEvent.setup();
    await bootstrap(routeEdit, { add: false, edit: false });
    const spy = jest.spyOn(regulatorUsersService, 'updateCurrentRegulatorUser');
    await user.type(document.getElementById('user.firstName'), 'Johnathan');
    await user.click(screen.getByText('Save'));
    expect(spy).toHaveBeenCalled();
  });

  it('should submit a valid form not for current user', fakeAsync(async () => {
    const user = UserEvent.setup();
    await bootstrap(routeEdit2);
    const spy = jest.spyOn(regulatorUsersService, 'updateRegulatorUserByCaAndId');
    await user.type(document.getElementById('user.firstName'), 'Johnathan');
    await user.click(screen.getByText('Save'));
    expect(spy).toHaveBeenCalled();
  }));
});
