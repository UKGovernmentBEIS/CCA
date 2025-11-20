import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';

import { mockAuditDetailsAndCorrectiveActionsState } from '../../../testing/mock-data';
import { AuditDetailsCheckYourAnswersComponent } from './audit-details-check-your-answers.component';

describe('AuditDetailsCheckYourAnswersComponent', () => {
  let component: AuditDetailsCheckYourAnswersComponent;
  let fixture: ComponentFixture<AuditDetailsCheckYourAnswersComponent>;
  let store: RequestTaskStore;

  const route: any = {
    snapshot: {
      params: {},
      paramMap: { get: jest.fn().mockReturnValue(123) },
      pathFromRoot: [],
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuditDetailsCheckYourAnswersComponent],
      providers: [provideHttpClient(), RequestTaskStore, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockAuditDetailsAndCorrectiveActionsState);

    fixture = TestBed.createComponent(AuditDetailsCheckYourAnswersComponent);
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
