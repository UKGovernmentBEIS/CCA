import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { click, getAllByText, getByLabelText, getByText, setInputValue } from '@testing';

import { SectorUsersInvitationService } from 'cca-api';

import { RoleCode } from '../../types';
import { AddSectorUserComponent } from './add-sector-user.component';

describe('AddSectorUserComponent', () => {
  let fixture: ComponentFixture<AddSectorUserComponent>;
  const adminRole: RoleCode = 'sector_user_administrator';
  const basicUserRole: RoleCode = 'sector_user_basic_user';

  async function renderAdmin(role: RoleCode, svc: Partial<SectorUsersInvitationService> = {}) {
    await TestBed.configureTestingModule({
      imports: [AddSectorUserComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    })
      .overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ id: 123 }, { role }) })
      .overrideProvider(SectorUsersInvitationService, { useValue: svc })
      .compileComponents();

    fixture = TestBed.createComponent(AddSectorUserComponent);
    fixture.detectChanges();
  }

  function includesForm() {
    expect(document.querySelector("input[name='firstName']")).toBeTruthy();
    expect(document.querySelector("input[name='lastName']")).toBeTruthy();
    expect(document.querySelector("input[name='email']")).toBeTruthy();
    expect(document.querySelectorAll("input[name='contactType']").length).toBe(2);
  }

  it('should render form from administrator user', async () => {
    await renderAdmin(adminRole);

    const adminLis = [
      'add and remove other users',
      'perform all tasks related to target units and facilities',
      'submit target period reporting',
    ];

    expect(getByText('This user will have permission to:')).toBeTruthy();

    adminLis.forEach((li) => expect(getByText(li)).toBeTruthy());

    expect(
      getByText(
        'You should only add a user who has been authorised to perform these actions by the sector association.',
      ),
    ).toBeTruthy();

    includesForm();
  });

  it('should render form for basic user', async () => {
    await renderAdmin(basicUserRole);

    const adminLis = [
      'view account details',
      'perform all tasks related to target units and facilities',
      'submit target period reports',
    ];

    expect(getByText('This user will have permission to:')).toBeTruthy();

    adminLis.forEach((li) => expect(getByText(li)).toBeTruthy());

    expect(getByText('They will not be able to add and remove other users from your account.')).toBeTruthy();
  });

  it('should prepopulate contactType control for admin', async () => {
    await renderAdmin(adminRole);

    expect((getByLabelText('Sector association') as HTMLInputElement).checked).toBe(true);
    expect((getByLabelText('Consultant') as HTMLInputElement).checked).toBe(false);
  });

  it('should prepopulate contactType control for basic user', async () => {
    await renderAdmin(basicUserRole);

    expect((getByLabelText('Sector association') as HTMLInputElement).checked).toBe(true);
    expect((getByLabelText('Consultant') as HTMLInputElement).checked).toBe(false);
  });

  it('should show form errors', async () => {
    await renderAdmin(adminRole);

    click(getByText('Submit'));
    fixture.detectChanges();

    expect(document.querySelector('.govuk-error-summary')).toBeTruthy();
    expect(getAllByText('Enter the user’s first name').length).toBe(2);
    expect(getAllByText('Enter the user’s last name').length).toBe(2);
    expect(getAllByText('Enter the user’s email').length).toBe(2);

    setInputValue(getByLabelText('Email address') as HTMLInputElement, 'sector_user');
    setInputValue(getByLabelText('First name') as HTMLInputElement, 'Sector');
    setInputValue(getByLabelText('Last name') as HTMLInputElement, 'User');
    fixture.detectChanges();
    click(getByText('Submit'));
    fixture.detectChanges();

    const msg = 'Enter an email address in the correct format, like name@example.com';
    const inline = Array.from(document.querySelectorAll('.govuk-error-message')).filter((el) =>
      el.textContent?.includes(msg),
    ).length;
    const summary = Array.from(document.querySelectorAll('.govuk-error-summary__list a')).filter((el) =>
      el.textContent?.includes(msg),
    ).length;

    expect(inline + summary).toBe(2);
  });
});
