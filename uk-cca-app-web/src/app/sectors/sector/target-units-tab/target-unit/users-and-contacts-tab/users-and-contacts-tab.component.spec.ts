import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub } from '@netz/common/testing';
import { getByTestId, queryByText } from '@testing';

import { OperatorAuthoritiesInfoDTO, OperatorAuthoritiesService } from 'cca-api';

import { mockOperatorAuthorities, mockOperatorAuthoritiesNotEditable } from '../../../../specs/fixtures/mock';
import { UsersAndContactsTabComponent } from './users-and-contacts-tab.component';

describe('Target unit Users component', () => {
  let fixture: ComponentFixture<UsersAndContactsTabComponent>;
  let operatorAuthoritiesService: jest.Mocked<Partial<OperatorAuthoritiesService>>;

  async function setup(mockData: OperatorAuthoritiesInfoDTO) {
    operatorAuthoritiesService = {
      getAccountOperatorAuthorities: jest.fn().mockReturnValue(of(mockData)),
    };

    await TestBed.configureTestingModule({
      imports: [UsersAndContactsTabComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
      ],
    })
      .overrideProvider(OperatorAuthoritiesService, { useValue: operatorAuthoritiesService })
      .compileComponents();

    fixture = TestBed.createComponent(UsersAndContactsTabComponent);
    fixture.detectChanges();
    await fixture.whenStable();
  }

  it('should render users', async () => {
    await setup(mockOperatorAuthorities);
    expect(getByTestId('target-unit-users-form')).toBeTruthy();
    expect(document.querySelectorAll('.govuk-table__row').length).toBe(mockOperatorAuthorities.authorities.length + 1);
  });

  it('should NOT show add operator button if NOT editable (only Regulator user allowed)', async () => {
    await setup(mockOperatorAuthoritiesNotEditable);
    expect(queryByText('Add a new operator')).toBeNull();
  });
});
