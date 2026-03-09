import { ComponentFixture } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthStore } from '@netz/common/auth';
import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockTargetUnitOperatorDetails } from 'src/app/sectors/specs/fixtures/mock';

import { ActiveOperatorStore } from './active-operator.store';
import { OperatorDetailsComponent } from './operator-details.component';

describe('OperatorDetailsComponent', () => {
  const operatorUserId = 'e7de58d5-0256-42a7-9501-014d25d5d310';

  const renderComponent = async (
    currentUserId = '5reg',
    editable = true,
  ): Promise<ComponentFixture<OperatorDetailsComponent>> => {
    const { fixture } = await render(OperatorDetailsComponent, {
      providers: [
        ActiveOperatorStore,
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ targetUnitId: 1, userId: operatorUserId }),
        },
      ],
      configureTestBed: (testbed) => {
        const store = testbed.inject(ActiveOperatorStore);
        const authStore = testbed.inject(AuthStore);

        store.setState({
          details: mockTargetUnitOperatorDetails,
          editable,
        });

        authStore.setUserState({
          status: 'ENABLED',
          roleType: 'OPERATOR',
          userId: currentUserId,
        });
      },
    });

    return fixture;
  };

  it('should create', async () => {
    const componentFixture = await renderComponent();
    expect(componentFixture.componentInstance).toBeTruthy();
  });

  it('should render "Name" and "Organisation details" titles', async () => {
    await renderComponent();
    expect(screen.getByText('Name')).toBeTruthy();
    expect(screen.getByText('Organisation details')).toBeTruthy();
  });

  it('should render "Name" section', async () => {
    await renderComponent();
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

  it('should render "Organisation details" section', async () => {
    await renderComponent();
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

  it('should render 6 change links (non-editable contact type)', async () => {
    await renderComponent();
    expect(screen.getAllByText(/Change/i)).toHaveLength(6);
  });

  it('should render reset 2fa link with operator reset state for non-current editable user', async () => {
    const componentFixture = await renderComponent();

    expect(screen.getByText('Reset two-factor authentication')).toHaveAttribute('href', '/2fa/reset-2fa');

    const resetLink = componentFixture.debugElement
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

  it('should render change 2fa link for current user', async () => {
    await renderComponent(operatorUserId);

    expect(screen.getByText('Change two factor authentication')).toHaveAttribute('href', '/2fa/change');
    expect(screen.queryByText('Reset two-factor authentication')).toBeNull();
  });
});
