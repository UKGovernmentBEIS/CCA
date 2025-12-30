import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';

import { RequestNotesService } from 'cca-api';

import { WorkflowAddNoteComponent } from './add-note.component';

describe('WorkflowAddNoteComponent', () => {
  let component: WorkflowAddNoteComponent;
  let fixture: ComponentFixture<WorkflowAddNoteComponent>;
  let requestNotesService: jest.Mocked<RequestNotesService>;
  let router: Router;

  beforeEach(async () => {
    const mockRequestNotesService = mockClass(RequestNotesService);
    mockRequestNotesService.createRequestNote = jest.fn().mockReturnValue(of({}));
    mockRequestNotesService.uploadRequestNoteFile = jest.fn().mockReturnValue(
      of({
        type: 4,
        body: { uuid: 'test-uuid' },
      }),
    );

    const mockActivatedRoute = new ActivatedRouteStub({ workflowId: '456' });

    await TestBed.configureTestingModule({
      imports: [WorkflowAddNoteComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: RequestNotesService, useValue: mockRequestNotesService },
      ],
    }).compileComponents();

    requestNotesService = TestBed.inject(RequestNotesService) as jest.Mocked<RequestNotesService>;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(WorkflowAddNoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have correct downloadUrl', () => {
    expect(component['downloadUrl']).toBe('../file-download/');
  });

  it('should initialize form with note and files controls', () => {
    expect(component['form'].get('note')).toBeTruthy();
    expect(component['form'].get('files')).toBeTruthy();
  });

  it('should not submit if form is invalid', () => {
    component['form'].patchValue({ note: '' });
    component.onSubmit();

    expect(requestNotesService.createRequestNote).not.toHaveBeenCalled();
  });

  it('should submit valid form and navigate back', () => {
    const mockNote = 'Test note content';
    const mockFiles = [{ uuid: 'uuid-1', file: new File(['content'], 'test.pdf') }];

    component['form'].patchValue({
      note: mockNote,
      files: mockFiles as any,
    });

    component.onSubmit();

    expect(requestNotesService.createRequestNote).toHaveBeenCalledWith({
      requestId: '456',
      note: mockNote,
      files: ['uuid-1'],
    });

    expect(router.navigate).toHaveBeenCalledWith(['../../'], {
      relativeTo: expect.anything(),
      fragment: 'notes',
    });
  });

  it('should handle empty files array', () => {
    component['form'].patchValue({
      note: 'Test note',
      files: [],
    });

    component.onSubmit();

    expect(requestNotesService.createRequestNote).toHaveBeenCalledWith(
      expect.objectContaining({
        files: [],
      }),
    );
  });

  it('should have return link to workflow notes', () => {
    const compiled = fixture.nativeElement;
    const returnLink = compiled.querySelector('a[routerLink="../.."]');

    expect(returnLink).toBeTruthy();
    expect(returnLink.textContent).toContain('Return to: Workflow notes');
    expect(returnLink.getAttribute('fragment')).toBe('notes');
  });
});
