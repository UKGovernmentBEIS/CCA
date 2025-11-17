import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { TaskSection } from '@netz/common/model';
import { StatusTagColorPipe, StatusTagTextPipe } from '@netz/common/pipes';

@Component({
  selector: 'netz-task-list',
  template: `
    <ul class="govuk-task-list">
      @for (section of sections(); track $index) {
        @if (section.title) {
          <h2 class="govuk-heading-m govuk-!-margin-top-5">{{ section.title }}</h2>
        }

        @for (task of section.tasks; track $index) {
          <li class="govuk-task-list__item govuk-task-list__item--with-link">
            <div class="govuk-task-list__name-and-hint">
              @if (task.link) {
                <a class="govuk-link govuk-task-list__link" [routerLink]="task.link">
                  {{ task.linkText }}
                </a>
              } @else {
                <span>{{ task.linkText }}</span>
              }

              @if (task.hint) {
                <div class="govuk-task-list__hint">
                  {{ task.hint }}
                </div>
              }
            </div>

            @if (task.status) {
              @if (task.status === 'CANNOT_START_YET') {
                <div class="govuk-task-list__status govuk-task-list__status--cannot-start-yet">Cannot start yet</div>
              } @else {
                <div class="govuk-task-list__status">
                  @if (task.status === 'COMPLETED' || task.status === 'UNCHANGED') {
                    {{ task.status | statusTagText }}
                  } @else {
                    <strong class="govuk-tag" [class]="'govuk-tag--' + (task.status | statusTagColor)">
                      {{ task.status | statusTagText }}
                    </strong>
                  }
                </div>
              }
            }
          </li>
        }
      }
    </ul>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterLink, StatusTagColorPipe, StatusTagTextPipe],
})
export class TaskListComponent {
  protected readonly sections = input<TaskSection[]>([]);
}
