import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getAllByRole, getByRole, getByText, queryByText } from '@testing';

import { mockTargetUnitOperatorDetails } from '../../../../../specs/fixtures/mock';
import { ActiveOperatorStore } from './active-operator.store';
import { OperatorDetailsComponent } from './operator-details.component';

describe('OperatorDetailsComponent', () => {
  const operatorUserId = 'e7de58d5-0256-42a7-9501-014d25d5d310';
  let fixture: ComponentFixture<OperatorDetailsComponent>;
  let store: ActiveOperatorStore;
  let authStore: AuthStore;

  const setComponentState = (currentUserId = '5reg', editable = true) => {
    store.setState({
      details: mockTargetUnitOperatorDetails,
      editable,
    });

    authStore.setUserState({
      status: 'ENABLED',
      roleType: 'OPERATOR',
      userId: currentUserId,
    });

    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OperatorDetailsComponent],
      providers: [
        ActiveOperatorStore,
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ targetUnitId: 1, userId: operatorUserId }),
        },
      ],
    }).compileComponents();

    store = TestBed.inject(ActiveOperatorStore);
    authStore = TestBed.inject(AuthStore);
    fixture = TestBed.createComponent(OperatorDetailsComponent);
    setComponentState();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render "Name" and "Organisation details" titles', () => {
    expect(getByText('Name')).toBeTruthy();
    expect(getByText('Organisation details')).toBeTruthy();
  });

  it('should render "Name" section', () => {
    const detailsList = document.querySelectorAll("[data-testid='name-list'] div");

    const elements = [];

    detailsList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['First name', 'oper1'],
      ['Last name', 'tu'],
      ['Job title', 'job12'],
      ['Email', 'op1tu@cca.uk'],
    ]);
  });

  it('should render "Organisation details" section', () => {
    const contactList = document.querySelectorAll("[data-testid='organisation-details-list'] div");

    const elements = [];

    contactList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['Contact type', 'Consultant'],
      ['Organisation name', 'organisation'],
      ['Phone number 1', 'UK (44) 1234567890'],
      ['Phone number 2', 'UK (44) 1234567890'],
    ]);
  });

  it('should render 6 change links (non-editable contact type)', () => {
    expect(getAllByRole('link', { name: /Change/i }, fixture.nativeElement).length).toBe(6);
  });

  it('should render reset 2fa link with operator reset state for non-current editable user', () => {
    const resetLink2fa = getByRole('link', { name: 'Reset two-factor authentication' });
    expect(resetLink2fa.getAttribute('href')).toBe('/2fa/reset-2fa');

    const resetLink = fixture.debugElement
      .queryAll(By.directive(RouterLink))
      .map((debugElement) => debugElement.injector.get(RouterLink))
      .find((routerLink) => routerLink.href === '/2fa/reset-2fa');

    expect(resetLink).toBeTruthy();
    if (!resetLink) {
      return;
    }

    expect(resetLink.state).toEqual(
      expect.objectContaining({
        userId: operatorUserId,
        accountId: 1,
        userName: 'oper1 tu',
        role: 'OPERATOR',
      }),
    );
  });

  it('should render change 2fa link for current user', () => {
    setComponentState(operatorUserId);

    const changeLink = getByRole('link', { name: 'Change two factor authentication' });
    expect(changeLink.getAttribute('href')).toBe('/2fa/change');
    expect(queryByText('Reset two-factor authentication')).toBeNull();
  });
});
