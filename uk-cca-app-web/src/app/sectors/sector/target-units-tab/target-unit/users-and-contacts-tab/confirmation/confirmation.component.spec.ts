import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByRole, getByText } from '@testing';

import { mockTargetUnitAccountDetails } from '../../../../../../sectors/specs/fixtures/mock';
import { ActiveTargetUnitStore } from '../../../active-target-unit.store';
import { AddOperatorConfirmationComponent } from './confirmation.component';

describe('Invite operator confirmation ', () => {
  let fixture: ComponentFixture<AddOperatorConfirmationComponent>;
  let targetUnitStore: ActiveTargetUnitStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddOperatorConfirmationComponent],
      providers: [
        ActiveTargetUnitStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    }).compileComponents();

    targetUnitStore = TestBed.inject(ActiveTargetUnitStore);
    targetUnitStore.setState({ targetUnitAccountDetails: mockTargetUnitAccountDetails });

    fixture = TestBed.createComponent(AddOperatorConfirmationComponent);
    fixture.detectChanges();
  });

  it('should render content correctly', () => {
    const text = `You have successfully added an operator user for ${mockTargetUnitAccountDetails.businessId} - ${mockTargetUnitAccountDetails.name}`;
    expect(getByText(text)).toBeTruthy();
  });

  it('should render return link correctly', () => {
    const link = getByRole('link');
    expect(link.textContent?.trim()).toBe('Go to my dashboard');
    expect(link.getAttribute('href')).toBe('/dashboard');
  });
});
