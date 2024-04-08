import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, iif, map, mergeMap, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@common/request-task/+state';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { AuthStore, selectUserState } from '@core/store/auth';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { taskNotFoundError } from '@shared/errors/request-task-error';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';
import { PendingButtonDirective } from '@shared/pending-button.directive';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';

import { GovukComponentsModule, GovukSelectOption, GovukValidators } from 'govuk-components';

import { RequestTaskDTO, TasksAssignmentService, TasksReleaseService, UserStateDTO } from 'cca-api';

interface ViewModel {
  requestTask: RequestTaskDTO;
  showErrorSummary: boolean;
  form: UntypedFormGroup;
  options: GovukSelectOption<string>[];
}

@Component({
  selector: 'cca-change-assignee',
  standalone: true,
  templateUrl: './change-assignee.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe],
  imports: [NgIf, AsyncPipe, ReactiveFormsModule, GovukComponentsModule, PageHeadingComponent, PendingButtonDirective],
})
export class ChangeAssigneeComponent {
  private readonly UNASSIGNED_VALUE = 'unassigned_dummy_value'; // shouldn't be a uuid (uuid represent user ids)
  private readonly showErrorSummary$ = new BehaviorSubject(false);
  private form = this.fb.group({
    assignee: [null, { validators: [GovukValidators.required('Select a person')] }],
  });
  private options: GovukSelectOption[];

  protected vm$: Observable<ViewModel> = combineLatest([
    this.authStore.pipe(selectUserState),
    this.store.rxSelect(requestTaskQuery.selectRequestTaskItem),
    this.showErrorSummary$.asObservable(),
  ]).pipe(
    mergeMap(([userState, { requestTask }, showErrorSummary]) => {
      return this.tasksAssignmentService
        .getCandidateAssigneesByTaskId(requestTask.id)
        .pipe(map((candidates) => ({ userState, candidates, requestTask, showErrorSummary })));
    }),
    map(({ userState, candidates, requestTask, showErrorSummary }) => {
      const options = [
        ...(!!requestTask.assigneeUserId && this.allowReleaseTask(userState.roleType)
          ? [{ text: 'Unassigned', value: this.UNASSIGNED_VALUE }]
          : []),
        ...candidates
          .filter((candidates) => candidates.id !== requestTask?.assigneeUserId)
          .map((candidate) => ({
            text: this.userFullNamePipe.transform(candidate),
            value: candidate.id,
          })),
      ];
      this.options = options;

      return {
        requestTask,
        options,
        form: this.form,
        showErrorSummary,
      };
    }),
  );

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly authStore: AuthStore,
    private readonly store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly userFullNamePipe: UserFullNamePipe,
    private readonly tasksAssignmentService: TasksAssignmentService,
    private readonly tasksReleaseService: TasksReleaseService,
    private readonly pendingRequestService: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  submit(taskId: number, userId: string): void {
    if (!this.form.valid) {
      this.showErrorSummary$.next(true);
    } else {
      this.showErrorSummary$.next(false);
      iif(
        () => userId !== this.UNASSIGNED_VALUE,
        this.tasksAssignmentService.assignTask({ taskId, userId }),
        this.tasksReleaseService.releaseTask(taskId),
      )
        .pipe(
          catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
            this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
          ),
          this.pendingRequestService.trackRequest(),
        )
        .subscribe(() => {
          this.store.setTaskReassignedTo(
            userId === this.UNASSIGNED_VALUE ? null : this.options.find((o) => o.value === userId)?.text ?? null,
          );
          this.router.navigate(['success'], { relativeTo: this.route });
        });
    }
  }

  private allowReleaseTask(role: UserStateDTO['roleType']) {
    return role !== 'OPERATOR';
  }
}
