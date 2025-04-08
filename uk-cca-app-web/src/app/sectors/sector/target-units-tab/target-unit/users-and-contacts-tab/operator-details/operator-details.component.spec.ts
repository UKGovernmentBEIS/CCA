import { ComponentFixture, waitForAsync } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';

import { mockTargetUnitOperatorDetails } from 'src/app/sectors/specs/fixtures/mock';

import { ActiveOperatorStore } from './active-operator.store';
import { OperatorDetailsComponent } from './operator-details.component';

describe('OperatorDetailsComponent', () => {
  let componentFixture: ComponentFixture<OperatorDetailsComponent>;
  let store: ActiveOperatorStore;

  beforeEach(async () => {
    const { fixture } = await render(OperatorDetailsComponent, {
      providers: [
        ActiveOperatorStore,
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ targetUnitId: 1, userId: 'e7de58d5-0256-42a7-9501-014d25d5d310' }),
        },
      ],
      configureTestBed: (testbed) => {
        store = testbed.inject(ActiveOperatorStore);
        store.setState({
          details: mockTargetUnitOperatorDetails,
          editable: true,
        });
      },
    });

    componentFixture = fixture;
  });

  it('should create', waitForAsync(async () => {
    expect(componentFixture.componentInstance).toBeTruthy();
  }));

  it('should render "Name" and "Organisation details" titles', () => {
    expect(screen.getByText('Name')).toBeTruthy();
    expect(screen.getByText('Organisation details')).toBeTruthy();
  });

  it('should render "Name" section', () => {
    const detailsList = document.querySelectorAll("[data-testid='name-list'] div");

    const elements = [];

    detailsList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['First name', ' oper1 '],
      ['Last name', ' tu '],
      ['Job title', ' job12 '],
      ['Email', ' op1tu@cca.uk '],
    ]);
  });

  it('should render "Organisation details" section', () => {
    const contactList = document.querySelectorAll("[data-testid='organisation-details-list'] div");

    const elements = [];

    contactList.forEach((div) => {
      elements.push([div.querySelector('dt').textContent, div.querySelector('dd').textContent]);
    });

    expect(elements).toEqual([
      ['Contact type', ' Consultant '],
      ['Organisation name', ' organisation '],
      ['Phone number 1', ' UK (44) 1234567890 '],
      ['Phone number 2', ' UK (44) 1234567890 '],
    ]);
  });

  it('should render 6 change links (non-editable contact type)', () => {
    expect(screen.getAllByText(/Change/i)).toHaveLength(6);
  });
});
