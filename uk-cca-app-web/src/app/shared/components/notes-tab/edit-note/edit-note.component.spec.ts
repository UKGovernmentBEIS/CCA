import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';

import { RequestNoteDto, RequestNotesService } from 'cca-api';

import { WorkflowEditNoteComponent } from './edit-note.component';

describe('WorkflowEditNoteComponent', () => {
  let component: WorkflowEditNoteComponent;
  let fixture: ComponentFixture<WorkflowEditNoteComponent>;
  let requestNotesService: jest.Mocked<RequestNotesService>;
  let router: Router;

  const mockNote: RequestNoteDto = {
    id: 1,
    submitter: 'John Doe',
    lastUpdatedOn: '2024-01-15T10:30:00Z',
    payload: {
      note: 'Existing test note',
      files: {
        'uuid-1': 'document1.pdf',
        'uuid-2': 'document2.pdf',
      },
    },
  };

  beforeEach(async () => {
    const mockRequestNotesService = mockClass(RequestNotesService);
    mockRequestNotesService.updateRequestNote = jest.fn().mockReturnValue(of({}));
    mockRequestNotesService.uploadRequestNoteFile = jest.fn().mockReturnValue(
      of({
        type: 4,
        body: { uuid: 'test-uuid' },
      }),
    );

    const mockActivatedRoute = new ActivatedRouteStub({ workflowId: '456', noteId: '1' });
    mockActivatedRoute.snapshot.data = { note: mockNote };

    await TestBed.configureTestingModule({
      imports: [WorkflowEditNoteComponent],
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

    fixture = TestBed.createComponent(WorkflowEditNoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with existing note data', () => {
    expect(component['form'].get('note').value).toBe('Existing test note');
    expect(component['form'].get('files').value).toHaveLength(2);
  });

  it('should have correct noteId from route', () => {
    expect(component['noteId']).toBe(1);
  });

  it('should not submit if form is invalid', () => {
    component['form'].patchValue({ note: '' });
    component.onSubmit();

    expect(requestNotesService.updateRequestNote).not.toHaveBeenCalled();
  });

  it('should not submit if noteId is missing', () => {
    component['noteId'] = null as any;
    component['form'].patchValue({ note: 'Valid note' });

    component.onSubmit();

    expect(requestNotesService.updateRequestNote).not.toHaveBeenCalled();
  });

  it('should submit valid form and navigate back', () => {
    const updatedNote = 'Updated test note content';
    const mockFiles = [{ uuid: 'uuid-1', file: new File(['content'], 'test.pdf') }];

    component['form'].patchValue({
      note: updatedNote,
      files: mockFiles as any,
    });

    component.onSubmit();

    expect(requestNotesService.updateRequestNote).toHaveBeenCalledWith(1, {
      note: updatedNote,
      files: ['uuid-1'],
    });

    expect(router.navigate).toHaveBeenCalledWith(['../../../'], {
      relativeTo: expect.anything(),
      fragment: 'notes',
      queryParamsHandling: 'merge',
    });
  });

  it('should handle empty files array', () => {
    component['form'].patchValue({
      note: 'Updated note',
      files: [],
    });

    component.onSubmit();

    expect(requestNotesService.updateRequestNote).toHaveBeenCalledWith(
      1,
      expect.objectContaining({
        files: [],
      }),
    );
  });

  it('should handle multiple files', () => {
    const multipleFiles = [
      { uuid: 'uuid-1', file: new File(['content'], 'file1.pdf') },
      { uuid: 'uuid-2', file: new File(['content'], 'file2.pdf') },
      { uuid: 'uuid-3', file: new File(['content'], 'file3.pdf') },
    ];

    component['form'].patchValue({
      note: 'Note with multiple files',
      files: multipleFiles as any,
    });

    component.onSubmit();

    expect(requestNotesService.updateRequestNote).toHaveBeenCalledWith(
      1,
      expect.objectContaining({
        note: 'Note with multiple files',
        files: ['uuid-1', 'uuid-2', 'uuid-3'],
      }),
    );
  });

  it('should have return link to workflow notes', () => {
    const compiled = fixture.nativeElement;
    const returnLink = compiled.querySelector('a[routerLink="../../../"]');

    expect(returnLink).toBeTruthy();
    expect(returnLink.textContent).toContain('Return to: Workflow notes');
    expect(returnLink.getAttribute('fragment')).toBe('notes');
  });
});
