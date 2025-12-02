import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { TargetUnitAccountInfoViewService } from 'cca-api';

import { mockTrackCorrectiveActionsState } from '../../../testing/mock-data';
import { TrackCorrectiveActionsSummaryComponent } from './track-corrective-actions-summary.component';

describe('TrackCorrectiveActionsSummaryComponent', () => {
  let component: TrackCorrectiveActionsSummaryComponent;
  let fixture: ComponentFixture<TrackCorrectiveActionsSummaryComponent>;
  let targetUnitAccountInfoViewService: jest.Mocked<Partial<TargetUnitAccountInfoViewService>>;
  let store: RequestTaskStore;

  const route = { snapshot: { params: { actionId: '1' } } };

  beforeEach(async () => {
    targetUnitAccountInfoViewService = {
      getTargetUnitAccountDetailsById: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [TrackCorrectiveActionsSummaryComponent],
      providers: [
        provideHttpClient(),
        { provide: ActivatedRoute, useValue: route },
        { provide: TargetUnitAccountInfoViewService, useValue: targetUnitAccountInfoViewService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockTrackCorrectiveActionsState);

    fixture = TestBed.createComponent(TrackCorrectiveActionsSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct content', () => {
    expect(fixture).toMatchSnapshot();
  });
});
