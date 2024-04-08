import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestTaskStore } from '../../../request-task/+state';
import { AssignmentSuccessComponent } from './assignment-success.component';

describe('AssignmentConfirmationComponent', () => {
  let component: AssignmentSuccessComponent;
  let fixture: ComponentFixture<AssignmentSuccessComponent>;
  let store: RequestTaskStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssignmentSuccessComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignmentSuccessComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(RequestTaskStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the unassigned message if user not provided', () => {
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLHeadingElement>('.govuk-panel__title').innerHTML.trim(),
    ).toEqual('This task has been unassigned');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLDivElement>('.govuk-panel__body').innerHTML.trim(),
    ).toEqual('');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLParagraphElement>('.govuk-body').innerHTML.trim(),
    ).toEqual('The task will appear in the unassigned tab of your dashboard');
  });

  it('should render the assignee if provided', () => {
    store.setTaskReassignedTo('Test User');
    fixture.detectChanges();

    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLHeadingElement>('.govuk-panel__title').innerHTML.trim(),
    ).toEqual('The task has been reassigned to');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLDivElement>('.govuk-panel__body').innerHTML.trim(),
    ).toEqual('Test User');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector<HTMLParagraphElement>('.govuk-body').innerHTML.trim(),
    ).toEqual('The task will appear in the dashboard of the person it has been assigned to');
  });
});
