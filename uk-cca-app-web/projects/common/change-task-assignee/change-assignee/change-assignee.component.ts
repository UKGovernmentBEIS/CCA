import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, isDevMode } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, EMPTY, filter, iif, map, mergeMap, Observable } from 'rxjs';

import { AuthStore, selectUserState } from '@netz/common/auth';
import { PageHeadingComponent } from '@netz/common/components';
import { PendingButtonDirective } from '@netz/common/directives';
import { UserFullNamePipe } from '@netz/common/pipes';
import { PendingRequestService } from '@netz/common/services';
import { requestTaskQuery, RequestTaskStore } from '@netz/common/store';
import {
  ButtonDirective,
  ErrorSummaryComponent,
  GovukSelectOption,
  GovukValidators,
  SelectComponent,
} from '@netz/govuk-components';

import {
  AssigneeUserInfoDTO,
  RequestTaskDTO,
  TasksAssignmentService,
  TasksReleaseService,
  UserStateDTO,
} from 'cca-api';

interface ViewModel {
  requestTask: RequestTaskDTO;
  showErrorSummary: boolean;
  form: UntypedFormGroup;
  options: GovukSelectOption<string>[];
}

@Component({
  selector: 'netz-change-assignee',
  templateUrl: './change-assignee.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe],
  imports: [
    AsyncPipe,
    ReactiveFormsModule,
    PageHeadingComponent,
    PendingButtonDirective,
    ErrorSummaryComponent,
    SelectComponent,
    ButtonDirective,
  ],
})
export class ChangeAssigneeComponent {
  private readonly fb = inject(UntypedFormBuilder);
  private readonly authStore = inject(AuthStore);
  private readonly store = inject(RequestTaskStore);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly userFullNamePipe = inject(UserFullNamePipe);
  private readonly tasksAssignmentService = inject(TasksAssignmentService);
  private readonly tasksReleaseService = inject(TasksReleaseService);
  private readonly pendingRequestService = inject(PendingRequestService);

  private readonly UNASSIGNED_VALUE = 'unassigned_dummy_value'; // shouldn't be a uuid (uuid represent user ids)
  private readonly showErrorSummary$ = new BehaviorSubject(false);
  private form = this.fb.group({
    assignee: [null, { validators: [GovukValidators.required('Select a person')] }],
  });
  private options!: GovukSelectOption[];

  protected vm$: Observable<ViewModel> = combineLatest([
    this.authStore.rxSelect(selectUserState),
    this.store.rxSelect(requestTaskQuery.selectRequestTaskItem).pipe(filter(Boolean)),
    this.showErrorSummary$.asObservable(),
  ]).pipe(
    mergeMap(([userState, { requestTask }, showErrorSummary]) => {
      if (!requestTask?.id) return EMPTY;
      return this.tasksAssignmentService
        .getCandidateAssigneesByTaskId(requestTask.id)
        .pipe(map((candidates: AssigneeUserInfoDTO[]) => ({ userState, candidates, requestTask, showErrorSummary })));
    }),
    map(({ userState, candidates, requestTask, showErrorSummary }) => {
      const options = [
        ...(!!requestTask.assigneeUserId && !!userState && this.allowReleaseTask(userState.roleType)
          ? [{ text: 'Unassigned', value: this.UNASSIGNED_VALUE }]
          : []),
        ...candidates
          .filter((candidate: AssigneeUserInfoDTO) => candidate.id !== requestTask?.assigneeUserId)
          .map((candidate: AssigneeUserInfoDTO) => ({
            text: this.userFullNamePipe.transform(candidate),
            value: candidate.id ?? '',
          })),
      ];
      this.options = options;

      // Note: this.options is stored as a side-effect for use in submit().
      // A cleaner refactor would pass options directly from the template via submit().

      return {
        requestTask,
        options,
        form: this.form,
        showErrorSummary,
      };
    }),
  );

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
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe({
          next: () => {
            this.store.setTaskReassignedTo(
              userId === this.UNASSIGNED_VALUE ? null : (this.options.find((o) => o.value === userId)?.text ?? null),
            );
            this.router.navigate(['success'], { relativeTo: this.route });
          },
          error: (err: unknown) => {
            if (isDevMode()) console.error('Failed to assign/release task', err);
          },
        });
    }
  }

  private allowReleaseTask(role: UserStateDTO['roleType']) {
    return role !== 'OPERATOR';
  }
}
