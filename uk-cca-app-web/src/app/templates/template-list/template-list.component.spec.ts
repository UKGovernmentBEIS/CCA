import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { activatedRouteMock, mockNotificationTemplateSearchResults } from '../testing/mock-data';
import { TemplateListComponent } from './template-list.component';

describe('TemplateListComponent', () => {
  let component: TemplateListComponent;
  let componentRef: ComponentRef<TemplateListComponent>;
  let fixture: ComponentFixture<TemplateListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TemplateListComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TemplateListComponent);
    component = fixture.componentInstance;

    componentRef = fixture.componentRef;
    componentRef.setInput('templates', mockNotificationTemplateSearchResults.templates);
    componentRef.setInput('templateType', 'email');

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a table with the correct number of data rows', () => {
    const rows = document.querySelectorAll('tr');
    expect(rows.length).toBe(mockNotificationTemplateSearchResults.templates.length + 1); // +1 for the header row
  });

  it('should render table headers correctly', () => {
    const headers = document.querySelectorAll('th');
    expect((headers[0] as HTMLElement | null)?.textContent ?? '').toContain('Template name');
    expect((headers[1] as HTMLElement | null)?.textContent ?? '').toContain('Workflow');
  });
});
