import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';

import { mockNonComplianceDetailsNoEnforcementState, mockNonComplianceDetailsState } from '../../testing/mock-data';
import { NonComplianceConfirmationComponent } from './confirmation.component';

describe('NonComplianceConfirmationComponent', () => {
  let component: NonComplianceConfirmationComponent;
  let fixture: ComponentFixture<NonComplianceConfirmationComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NonComplianceConfirmationComponent],
      providers: [RequestTaskStore, { provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(mockNonComplianceDetailsState);

    fixture = TestBed.createComponent(NonComplianceConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display panel title "Non-compliance details completed"', () => {
    expect(fixture.nativeElement.textContent).toContain('Non-compliance details completed');
  });

  it('should display what happens next content when enforcement notice is required', () => {
    expect(fixture.nativeElement.textContent).toContain('What happens next');
    expect(fixture.nativeElement.textContent).toContain(
      'A new task has been created to allow you to upload and send the Notice of Intent',
    );
  });

  it('should not display what happens next content when enforcement notice is not required', () => {
    store.setState(mockNonComplianceDetailsNoEnforcementState);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).not.toContain('What happens next');
    expect(fixture.nativeElement.textContent).not.toContain(
      'A new task has been created to allow you to upload and send the Notice of Intent',
    );
  });

  it('should have return-to-task-or-action-page link', () => {
    const returnLink = fixture.nativeElement.querySelector('a.govuk-link');

    expect(returnLink).toBeTruthy();
    expect(returnLink.textContent).toContain('Return to:');
  });
});
