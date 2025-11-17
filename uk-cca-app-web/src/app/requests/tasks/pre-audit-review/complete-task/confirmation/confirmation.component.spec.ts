import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockPreAuditReviewState } from '../../testing/mock-data';
import { PreAuditReviewConfirmationComponent } from './confirmation.component';

describe('PreAuditReviewConfirmationComponent', () => {
  let component: PreAuditReviewConfirmationComponent;
  let fixture: ComponentFixture<PreAuditReviewConfirmationComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PreAuditReviewConfirmationComponent],
      providers: [RequestTaskStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockPreAuditReviewState);

    fixture = TestBed.createComponent(PreAuditReviewConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct header and caption', () => {
    expect(fixture).toMatchSnapshot();
  });
});
