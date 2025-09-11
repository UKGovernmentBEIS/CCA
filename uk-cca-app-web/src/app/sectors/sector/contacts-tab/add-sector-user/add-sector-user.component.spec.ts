import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import { SectorUsersInvitationService } from 'cca-api';

import { RoleCode } from '../../types';
import { AddSectorUserComponent } from './add-sector-user.component';

describe('AddSectorUserComponent', () => {
  const adminRole: RoleCode = 'sector_user_administrator';
  const basicUserRole: RoleCode = 'sector_user_basic_user';

  async function renderAdmin(role: RoleCode, svc: Partial<SectorUsersInvitationService> = {}) {
    await render(AddSectorUserComponent, {
      configureTestBed: (testbed) => {
        const route = new ActivatedRouteStub({ id: 123 }, { role });
        testbed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
        testbed.overrideProvider(ActivatedRoute, { useValue: route });
        testbed.overrideProvider(SectorUsersInvitationService, { useValue: svc });
      },
    });
  }

  function includesForm() {
    expect(document.querySelector("input[name='firstName']")).toBeInTheDocument();
    expect(document.querySelector("input[name='lastName']")).toBeInTheDocument();
    expect(document.querySelector("input[name='email']")).toBeInTheDocument();
    expect(document.querySelectorAll("input[name='contactType']")).toHaveLength(2);
  }

  it('should render form from administrator user', async () => {
    await renderAdmin(adminRole);

    const adminLis = [
      'add and remove other users',
      'perform all tasks related to target units and facilities',
      'submit target period reporting',
    ];

    expect(screen.getByText('This user will have permission to:')).toBeInTheDocument();

    adminLis.forEach((li) => expect(screen.getByText(li)).toBeInTheDocument());

    expect(
      screen.getByText(
        'You should only add a user who has been authorised to perform these actions by the sector association.',
      ),
    ).toBeInTheDocument();

    includesForm();
  });

  it('should render form for basic user', async () => {
    await renderAdmin(basicUserRole);

    const adminLis = [
      'view account details',
      'perform all tasks related to target units and facilities',
      'submit target period reports',
    ];

    expect(screen.getByText('This user will have permission to:')).toBeInTheDocument();

    adminLis.forEach((li) => expect(screen.getByText(li)).toBeInTheDocument());

    expect(
      screen.getByText('They will not be able to add and remove other users from your account.'),
    ).toBeInTheDocument();
  });

  it('should prepopulate contactType control for admin', async () => {
    await renderAdmin(adminRole);

    expect(screen.getByLabelText('Sector association')).toBeChecked();
    expect(screen.getByLabelText('Consultant')).not.toBeChecked();
  });

  it('should prepopulate contactType control for basic user', async () => {
    await renderAdmin(basicUserRole);

    expect(screen.getByLabelText('Sector association')).toBeChecked();
    expect(screen.getByLabelText('Consultant')).not.toBeChecked();
  });

  it('should show form errors', async () => {
    await renderAdmin(adminRole);

    const user = UserEvent.setup();
    await user.click(screen.getByText('Submit'));

    expect(document.querySelector('.govuk-error-summary')).toBeInTheDocument();
    expect(screen.getAllByText('Enter the user’s first name')).toHaveLength(2);
    expect(screen.getAllByText('Enter the user’s last name')).toHaveLength(2);
    expect(screen.getAllByText('Enter the user’s email')).toHaveLength(2);

    await user.type(screen.getByLabelText('Email address'), 'sector_user');
    await user.type(screen.getByLabelText('First name'), 'Sector');
    await user.type(screen.getByLabelText('Last name'), 'User');
    await user.click(screen.getByText('Submit'));

    expect(screen.getAllByText('Enter an email address in the correct format, like name@example.com')).toHaveLength(2);
  });
});
