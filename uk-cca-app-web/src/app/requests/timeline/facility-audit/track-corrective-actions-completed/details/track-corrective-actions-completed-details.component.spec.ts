import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';

import { mockTrackCorrectiveActionsCompletedActionState } from '../../testing/mock-data';
import { TrackCorrectiveActionsCompletedDetailsComponent } from './track-corrective-actions-completed-details.component';

describe('TrackCorrectiveActionsCompletedDetailsComponent', () => {
  let component: TrackCorrectiveActionsCompletedDetailsComponent;
  let fixture: ComponentFixture<TrackCorrectiveActionsCompletedDetailsComponent>;
  let actionStore: RequestActionStore;

  const route = { snapshot: { params: { actionId: '1' } } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackCorrectiveActionsCompletedDetailsComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(mockTrackCorrectiveActionsCompletedActionState);

    fixture = TestBed.createComponent(TrackCorrectiveActionsCompletedDetailsComponent);
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
