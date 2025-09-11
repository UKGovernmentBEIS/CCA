import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '../../testing';
import { TaskHeaderInfoComponent } from './task-header-info.component';
import { ComponentRef } from '@angular/core';

describe('TaskHeaderInfoComponent', () => {
  let component: TaskHeaderInfoComponent;
  let componentRef: ComponentRef<TaskHeaderInfoComponent>;
  let fixture: ComponentFixture<TaskHeaderInfoComponent>;
  let page: Page;

  class Page extends BasePage<TaskHeaderInfoComponent> {
    get info() {
      return this.queryAll<HTMLParagraphElement>('p');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();
    fixture = TestBed.createComponent(TaskHeaderInfoComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the content', () => {
    componentRef.setInput('assignee', 'Adam Smith');
    componentRef.setInput('daysRemaining', 13);
    fixture.detectChanges();

    expect(page.info.map((el) => el.textContent.trim())).toEqual(['Assigned to: Adam Smith', 'Days Remaining: 13']);
  });

  it('should display the content with no deadline', () => {
    componentRef.setInput('assignee', 'Adam Smith');
    componentRef.setInput('daysRemaining', null);
    fixture.detectChanges();

    expect(page.info.map((el) => el.textContent.trim())).toEqual(['Assigned to: Adam Smith']);
  });

  it('should display the content with no assignee', () => {
    componentRef.setInput('assignee', null);
    componentRef.setInput('daysRemaining', 13);
    fixture.detectChanges();

    expect(page.info.map((el) => el.textContent.trim())).toEqual(['Assigned to:', 'Days Remaining: 13']);
  });
});
