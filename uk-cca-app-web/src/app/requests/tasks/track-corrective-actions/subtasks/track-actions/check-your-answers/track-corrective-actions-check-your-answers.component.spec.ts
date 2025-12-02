import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { TargetUnitAccountInfoViewService } from 'cca-api';

import { mockTrackCorrectiveActionsState } from '../../../testing/mock-data';
import { TrackCorrectiveActionsCheckYourAnswersComponent } from './track-corrective-actions-check-your-answers.component';

describe('TrackCorrectiveActionsCheckYourAnswersComponent', () => {
  let component: TrackCorrectiveActionsCheckYourAnswersComponent;
  let fixture: ComponentFixture<TrackCorrectiveActionsCheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let targetUnitAccountInfoViewService: jest.Mocked<Partial<TargetUnitAccountInfoViewService>>;

  const route = { snapshot: { params: { actionId: '1' } } };

  beforeEach(async () => {
    targetUnitAccountInfoViewService = {
      getTargetUnitAccountDetailsById: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [TrackCorrectiveActionsCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        { provide: ActivatedRoute, useValue: route },
        { provide: TargetUnitAccountInfoViewService, useValue: targetUnitAccountInfoViewService },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockTrackCorrectiveActionsState);

    fixture = TestBed.createComponent(TrackCorrectiveActionsCheckYourAnswersComponent);
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
