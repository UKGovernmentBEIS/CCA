import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';

import { TASK_STATUS_TAG_MAP, TaskStatusTagMap } from '@common/request-task/pipes/status-tag/status-tag.providers';
import { screen } from '@testing-library/angular';

import { TaskSection } from '../../../shared/model';
import { RequestTaskStore } from '../../+state';
import { REQUEST_TASK_PAGE_CONTENT } from '../../request-task.providers';
import { RequestTaskPageContentFactory, RequestTaskPageContentFactoryMap } from '../../request-task.types';
import { RequestTaskPageComponent } from './request-task-page.component';

let dynamicSectionsFlag = true;

@Component({
  selector: 'cca-test-content',
  template: `<h1>Test content component</h1>`,
  standalone: true,
})
class TestContentComponent {}

@Component({
  selector: 'cca-test-pre-content',
  template: `<h2>Test pre content</h2>`,
  standalone: true,
})
class TestPreContentComponent {}

@Component({
  selector: 'cca-test-post-content',
  template: `<h2>Test post content</h2>`,
  standalone: true,
})
class TestPostContentComponent {}

@Component({
  selector: 'cca-subtask',
  template: '<h1>SUBTASK COMPONENT</h1>',
  standalone: true,
})
class TestSubtaskComponent {}

const sectionsA: TaskSection[] = [
  {
    title: 'SECTION_A_TITLE',
    tasks: [
      {
        link: 'test-link',
        linkText: 'TEST_SUBTASK_A',
        status: 'COMPLETED',
      },
    ],
  },
];

const sectionsB: TaskSection[] = [
  {
    title: 'SECTION_B_TITLE',
    tasks: [
      {
        link: 'test-link',
        linkText: 'TEST_SUBTASK_B',
        status: 'COMPLETED',
      },
    ],
  },
];

const statusTagMap: TaskStatusTagMap = { COMPLETED: { text: 'COMPLETED', color: 'blue' } };

const contentWithSections: Record<string, RequestTaskPageContentFactory> = {
  TEST_TYPE: () => ({
    header: 'TEST_TYPE_HEADER',
    sections: sectionsA,
  }),
};

const contentWithDynamicSections: RequestTaskPageContentFactoryMap = {
  TEST_TYPE: () => {
    return {
      header: 'TEST_TYPE_HEADER',
      sections: dynamicSectionsFlag ? sectionsA : sectionsB,
    };
  },
};

const contentWithComponent: Record<string, RequestTaskPageContentFactory> = {
  TEST_TYPE: () => ({
    header: 'TEST_TYPE_HEADER',
    contentComponent: TestContentComponent,
    preContentComponent: TestPreContentComponent,
    postContentComponent: TestPostContentComponent,
  }),
};

describe('RequestTaskPageComponent', () => {
  let store: RequestTaskStore;
  let component: RequestTaskPageComponent;
  let harness: RouterTestingHarness;

  async function createModule(content: RequestTaskPageContentFactoryMap) {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          { path: '', component: RequestTaskPageComponent },
          { path: 'subtask', component: TestSubtaskComponent },
        ]),
        { provide: REQUEST_TASK_PAGE_CONTENT, useValue: content },
        { provide: TASK_STATUS_TAG_MAP, useValue: statusTagMap },
      ],
    });

    store = TestBed.inject(RequestTaskStore);
    store.setRequestTaskItem({ requestTask: { type: 'TEST_TYPE' as any } });

    harness = await RouterTestingHarness.create();
    component = await harness.navigateByUrl('/', RequestTaskPageComponent);
    harness.detectChanges();
  }

  afterEach(() => {
    dynamicSectionsFlag = true;
  });

  it('should create', async () => {
    await createModule(contentWithSections);
    expect(component).toBeTruthy();
  });

  it('should show sections provided', async () => {
    await createModule(contentWithSections);
    expect(screen.getByText('SECTION_A_TITLE')).toBeVisible();
  });

  it('should show components provided', async () => {
    await createModule(contentWithComponent);
    expect(screen.getByRole('heading', { name: 'Test content component' })).toBeVisible();
    expect(screen.getByRole('heading', { name: 'Test pre content' })).toBeVisible();
    expect(screen.getByRole('heading', { name: 'Test post content' })).toBeVisible();
  });

  it('should show changed sections for same task type after navigation', async () => {
    await createModule(contentWithDynamicSections);
    await harness.navigateByUrl('subtask', TestSubtaskComponent);
    dynamicSectionsFlag = false;
    await harness.navigateByUrl('', RequestTaskPageComponent);
    expect(screen.getByText('SECTION_B_TITLE')).toBeVisible();
  });
});
