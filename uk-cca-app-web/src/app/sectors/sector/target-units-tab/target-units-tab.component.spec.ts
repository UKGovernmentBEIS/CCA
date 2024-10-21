import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { render } from '@testing-library/angular';
import { screen } from '@testing-library/dom';
import UserEvent from '@testing-library/user-event';

import {
  SectorAssociationAuthoritiesService,
  TargetUnitAccountInfoViewService,
  TargetUnitAccountsSiteContactsService,
} from 'cca-api';

import { mockSectorAuthorities, mockTargetUnits, mockTargetUnitsNotEditable } from '../../specs/fixtures/mock';
import { SectorTargetUnitsTabComponent } from './target-units-tab.component';

describe('Target Units Tab Editable', () => {
  let targetUnitSiteContactsService: Partial<jest.Mocked<TargetUnitAccountsSiteContactsService>>;
  let sectorAssociationAuthoritiesService: Partial<jest.Mocked<SectorAssociationAuthoritiesService>>;
  let targetUnitService: Partial<jest.Mocked<TargetUnitAccountInfoViewService>>;

  beforeEach(async () => {
    targetUnitSiteContactsService = {
      updateTargetUnitAccountSiteContacts: jest.fn().mockReturnValue(of(null)),
    };

    sectorAssociationAuthoritiesService = {
      getSectorUserAuthoritiesBySectorAssociationId: jest.fn().mockReturnValue(of(mockSectorAuthorities)),
    };

    targetUnitService = {
      getTargetUnitAccountsWithSiteContacts: jest.fn().mockReturnValue(of(mockTargetUnits)),
    };

    const { fixture } = await render(SectorTargetUnitsTabComponent, {
      configureTestBed: (testbed) => {
        testbed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
        testbed.overrideProvider(TargetUnitAccountsSiteContactsService, { useValue: targetUnitSiteContactsService });
        testbed.overrideProvider(TargetUnitAccountInfoViewService, { useValue: targetUnitService });
        testbed.overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ id: 1 }) });
        testbed.overrideProvider(SectorAssociationAuthoritiesService, {
          useValue: sectorAssociationAuthoritiesService,
        });
      },
    });

    fixture.componentInstance.canCreateTargetUnit = signal(true);
    fixture.detectChanges();
  });

  it('should render the target unit list', () => {
    expect(screen.getByTestId('target-unit-list')).toBeVisible();
  });

  it('should show add button if editable is true', () => {
    expect(screen.getByText('Add new target unit')).toBeVisible();
  });

  it('should refetch data on discard changes', async () => {
    const spy = jest.spyOn(targetUnitService, 'getTargetUnitAccountsWithSiteContacts');
    const user = UserEvent.setup();

    await user.click(screen.getByText('Discard changes'));
    expect(spy).toHaveBeenCalledTimes(2);
  });
});

describe('Target Units Tab Not Editable', () => {
  let targetUnitSiteContactsService: Partial<jest.Mocked<TargetUnitAccountsSiteContactsService>>;
  let sectorAssociationAuthoritiesService: Partial<jest.Mocked<SectorAssociationAuthoritiesService>>;
  let targetUnitService: Partial<jest.Mocked<TargetUnitAccountInfoViewService>>;

  beforeEach(async () => {
    targetUnitSiteContactsService = {
      updateTargetUnitAccountSiteContacts: jest.fn().mockReturnValue(of(null)),
    };

    sectorAssociationAuthoritiesService = {
      getSectorUserAuthoritiesBySectorAssociationId: jest.fn().mockReturnValue(of(mockSectorAuthorities)),
    };

    targetUnitService = {
      getTargetUnitAccountsWithSiteContacts: jest.fn().mockReturnValue(of(mockTargetUnitsNotEditable)),
    };

    const { fixture } = await render(SectorTargetUnitsTabComponent, {
      configureTestBed: (testbed) => {
        testbed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
        testbed.overrideProvider(TargetUnitAccountsSiteContactsService, { useValue: targetUnitSiteContactsService });
        testbed.overrideProvider(TargetUnitAccountInfoViewService, { useValue: targetUnitService });
        testbed.overrideProvider(ActivatedRoute, { useValue: new ActivatedRouteStub({ id: 1 }) });
        testbed.overrideProvider(SectorAssociationAuthoritiesService, {
          useValue: sectorAssociationAuthoritiesService,
        });
      },
    });

    fixture.detectChanges();
  });

  it('should show assignTo as text', () => {
    mockTargetUnitsNotEditable.accountsWithSiteContact.forEach((_, idx) => {
      expect(document.getElementById(`targetUnits.${idx}.assignedTo`)).toBeFalsy();
    });
  });

  it('should not show save and discard buttons', () => {
    expect(screen.queryByText('Save')).not.toBeInTheDocument();
    expect(screen.queryByText('Discard changes')).not.toBeInTheDocument();
  });
});
