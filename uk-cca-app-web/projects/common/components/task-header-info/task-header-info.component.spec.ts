import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BasePage } from '../../testing';
import { TaskHeaderInfoComponent } from './task-header-info.component';

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
    expect(page.info.every((el) => el.classList.contains('govuk-body'))).toBeTruthy();
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

  it('should display the ETS reporting details', () => {
    componentRef.setInput('requestType', 'EMISSIONS_TRADING_SCHEME_DATA');
    componentRef.setInput('assignee', 'Sector user');
    componentRef.setInput('startDate', '2027-05-05');
    componentRef.setInput('payload', { targetYear: 2026, uketsId: 'UK-E-IN-0000' });
    fixture.detectChanges();

    expect(page.info.map((el) => el.textContent.trim())).toEqual([
      'Assigned to: Sector user',
      'Date initiated: 05/05/2027',
      'Year: 2026',
      'Participates in UK ETS: Yes, UK-E-IN-0000',
    ]);
  });

  it('should display non-participation for an ETS task without a permit ID', () => {
    componentRef.setInput('requestType', 'EMISSIONS_TRADING_SCHEME_DATA');
    componentRef.setInput('payload', { targetYear: 2026, uketsId: null });
    fixture.detectChanges();

    expect(page.info.map((el) => el.textContent.trim())).toContain('Participates in UK ETS: No');
  });
});
