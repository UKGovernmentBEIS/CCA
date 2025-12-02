import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestActionStore } from '@netz/common/store';

import { mockDetailsCorrectiveActionsCompletedActionState } from '../../testing/mock-data';
import { DetailsCorrectiveActionsActionsComponent } from './details-corrective-actions-actions.component';

describe('DetailsCorrectiveActionsActionsComponent', () => {
  let component: DetailsCorrectiveActionsActionsComponent;
  let fixture: ComponentFixture<DetailsCorrectiveActionsActionsComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsCorrectiveActionsActionsComponent],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(mockDetailsCorrectiveActionsCompletedActionState);

    fixture = TestBed.createComponent(DetailsCorrectiveActionsActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show proper view', () => {
    expect(fixture).toMatchSnapshot();
  });
});
