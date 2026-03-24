import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { transformUsername } from '@netz/common/pipes';
import { ActivatedRouteStub } from '@netz/common/testing';
import { getByText } from '@testing';

import { mockTargetUnitOperatorDetails } from '../../../../../../specs/fixtures/mock';
import { ActiveOperatorStore } from '../active-operator.store';
import { DeleteOperatorComponent } from './delete-operator.component';

describe('Delete Operator Component', () => {
  let fixture: ComponentFixture<DeleteOperatorComponent>;
  let store: ActiveOperatorStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteOperatorComponent],
      providers: [
        ActiveOperatorStore,
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: new ActivatedRouteStub({ targetUnitId: 1, userId: '1123asd12b4' }),
        },
      ],
    }).compileComponents();

    store = TestBed.inject(ActiveOperatorStore);
    store.setState({
      details: mockTargetUnitOperatorDetails,
      editable: true,
    });

    fixture = TestBed.createComponent(DeleteOperatorComponent);
    fixture.detectChanges();
  });

  it('should render contexnt appropriately', () => {
    expect(
      getByText(`Confirm that the user account of ${transformUsername(mockTargetUnitOperatorDetails)} will be removed`),
    ).toBeTruthy();

    expect(
      getByText(
        "All tasks currently assigned to this user will be automatically unassigned after you select 'Confirm removal'.",
      ),
    ).toBeTruthy();

    expect(
      getByText("If you need to reassign any of these tasks before removing this user, select 'Cancel'."),
    ).toBeTruthy();

    expect(getByText('Confirm removal')).toBeTruthy();
    expect(getByText('Cancel')).toBeTruthy();
  });
});
