import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { RequestTaskStore } from '@netz/common/store';

import { TasksService } from 'cca-api';

import { mockTrackCorrectiveActionsState } from '../../../testing/mock-data';
import { TrackCorrectiveActionsCheckYourAnswersComponent } from './track-corrective-actions-check-your-answers.component';

describe('TrackCorrectiveActionsCheckYourAnswersComponent', () => {
  let component: TrackCorrectiveActionsCheckYourAnswersComponent;
  let fixture: ComponentFixture<TrackCorrectiveActionsCheckYourAnswersComponent>;
  let store: RequestTaskStore;
  let tasksService: jest.Mocked<Partial<TasksService>>;

  const route = { snapshot: { params: { actionId: '1' } } };

  beforeEach(async () => {
    tasksService = {
      getRequestTaskHeaderInfo: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [TrackCorrectiveActionsCheckYourAnswersComponent],
      providers: [
        provideHttpClient(),
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
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
