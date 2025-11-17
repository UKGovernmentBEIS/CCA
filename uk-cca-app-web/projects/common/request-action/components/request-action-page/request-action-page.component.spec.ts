import { Component, inject } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TASK_STATUS_TAG_MAP, TaskStatusTagMap } from '@netz/common/pipes';
import { requestActionQuery, RequestActionStore } from '@netz/common/store';
import { screen } from '@testing-library/angular';

import { REQUEST_ACTION_PAGE_CONTENT } from '../../request-action.providers';
import { RequestActionPageContentFactoryMap } from '../../request-action.types';
import { RequestActionPageComponent } from './request-action-page.component';

@Component({
  template: `<h1>TEST CONTENT</h1>`,
})
class MockContentComponent {}

const sections = [
  {
    title: 'TEST_SECTION_1',
    tasks: [{ link: '.', linkText: 'TEST_SUBTASK_1_1', status: 'COMPLETED' }],
  },
  {
    title: 'TEST_SECTION_2',
    tasks: [{ link: '.', linkText: 'TEST_SUBTASK_2_1', status: 'COMPLETED' }],
  },
];

function getContent(type: 'component' | 'sections'): RequestActionPageContentFactoryMap {
  return {
    RFI_SUBMITTED: () => {
      const store = inject(RequestActionStore);
      const submitter = store.select(requestActionQuery.selectSubmitter)();
      return {
        header: `Original application submitted by ${submitter}`,
        component: type === 'component' ? MockContentComponent : undefined,
        sections: type === 'sections' ? sections : undefined,
      };
    },
  };
}

describe('RequestActionComponent', () => {
  let component: RequestActionPageComponent;
  let fixture: ComponentFixture<RequestActionPageComponent>;
  let store: RequestActionStore;
  const map: TaskStatusTagMap = { COMPLETED: { text: 'COMPLETED', color: 'blue' } };

  function createTestModule(content: RequestActionPageContentFactoryMap) {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, RequestActionPageComponent],
      providers: [
        { provide: REQUEST_ACTION_PAGE_CONTENT, useValue: content },
        { provide: TASK_STATUS_TAG_MAP, useValue: map },
      ],
    });

    store = TestBed.inject(RequestActionStore);
    store.setAction({
      type: 'RFI_SUBMITTED',
      submitter: 'Darth Vader',
    });

    fixture = TestBed.createComponent(RequestActionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should create', () => {
    createTestModule(getContent('component'));
    expect(component).toBeTruthy();
  });

  it('should show content with custom component', () => {
    createTestModule(getContent('component'));
    expect(screen.getByRole('heading', { name: 'TEST CONTENT' })).toBeVisible();
  });

  it('should show content with sections', () => {
    createTestModule(getContent('sections'));
    expect(screen.getByRole('heading', { name: 'TEST_SECTION_1' })).toBeVisible();
    expect(screen.getByRole('link', { name: 'TEST_SUBTASK_1_1' })).toBeVisible();
    expect(screen.getByRole('heading', { name: 'TEST_SECTION_2' })).toBeVisible();
    expect(screen.getByRole('link', { name: 'TEST_SUBTASK_2_1' })).toBeVisible();
  });
});
