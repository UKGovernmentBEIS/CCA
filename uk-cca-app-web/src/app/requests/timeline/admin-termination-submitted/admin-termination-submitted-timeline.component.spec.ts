import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionStore } from '@netz/common/store';
import { ActivatedRouteStub } from '@netz/common/testing';
import { mockAdminTerminationSubmittedRequestActionState } from '@requests/common';
import { screen } from '@testing-library/angular';

import { AdminTerminationSubmittedTimelineComponent } from './admin-termination-submitted-timeline.component';

describe('AdminTerminationSubmittedTimelineComponent', () => {
  let component: AdminTerminationSubmittedTimelineComponent;
  let fixture: ComponentFixture<AdminTerminationSubmittedTimelineComponent>;
  let actionStore: RequestActionStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTerminationSubmittedTimelineComponent],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();

    actionStore = TestBed.inject(RequestActionStore);
    actionStore.setState(mockAdminTerminationSubmittedRequestActionState);

    fixture = TestBed.createComponent(AdminTerminationSubmittedTimelineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the correct data', () => {
    const summaryValues = screen
      .getAllByText((_, el) => el.tagName.toLowerCase() === 'dl')
      .map((el) => [
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
        Array.from(el.querySelectorAll('dd'))
          .filter((dt) => dt.textContent.trim() !== 'Change')
          .map((dt) => dt.textContent.trim()),
      ]);

    expect(summaryValues).toEqual([
      [
        ['Termination reason', 'Explain why the account is being terminated', 'Uploaded files'],
        ['Failure to agree a variation in a target', 'asdsadsad', 'sample_profile1.png'],
      ],
      [
        ['Users', 'Name and signature on the official notice', 'Official notice'],
        [
          'asdasdsa lname, Responsible person, test-test@cca.uk  asd England, Administrative contact, test-admin@test.com  Fred_2 William_2, Sector contact, fredwilliam_2@agindustries.org.uk',
          'Regulator England',
          'Notice of intent to terminate agreement.pdf',
        ],
      ],
    ]);
  });
});
