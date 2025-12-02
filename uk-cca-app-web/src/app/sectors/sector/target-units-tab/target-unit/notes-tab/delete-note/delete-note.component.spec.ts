import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { Router } from '@angular/router';

import { of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@netz/common/testing';

import { AccountNotesService } from 'cca-api';

import { DeleteNoteComponent } from './delete-note.component';

describe('DeleteNoteComponent', () => {
  let component: DeleteNoteComponent;
  let fixture: ComponentFixture<DeleteNoteComponent>;
  let accountNotesService: jest.Mocked<AccountNotesService>;
  let router: Router;

  beforeEach(async () => {
    const mockAccountNotesService = mockClass(AccountNotesService);
    mockAccountNotesService.deleteAccountNote = jest.fn().mockReturnValue(of({}));

    const mockActivatedRoute = new ActivatedRouteStub({ targetUnitId: '123', noteId: '42' });

    await TestBed.configureTestingModule({
      imports: [DeleteNoteComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: AccountNotesService, useValue: mockAccountNotesService },
      ],
    }).compileComponents();

    accountNotesService = TestBed.inject(AccountNotesService) as jest.Mocked<AccountNotesService>;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate');

    fixture = TestBed.createComponent(DeleteNoteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have correct noteId from route', () => {
    expect(component['noteId']).toBe(42);
  });

  it('should delete note and navigate back on confirmation', () => {
    component.onDelete();

    expect(accountNotesService.deleteAccountNote).toHaveBeenCalledWith(42);
    expect(router.navigate).toHaveBeenCalledWith(['../../../'], {
      relativeTo: expect.anything(),
      fragment: 'notes',
      queryParamsHandling: 'merge',
    });
  });

  it('should display warning message', () => {
    const compiled = fixture.nativeElement;

    expect(compiled.textContent).toContain('Are you sure you want to delete this note?');
    expect(compiled.textContent).toContain('Your note will be deleted permanently');
    expect(compiled.textContent).toContain('You will not be able to undo this action');
  });

  it('should have delete button with warning style', () => {
    const compiled = fixture.nativeElement;
    const deleteButton = compiled.querySelector('button');

    expect(deleteButton).toBeTruthy();
    expect(deleteButton.textContent).toContain('Delete note');
    expect(deleteButton.classList.contains('govuk-button--warning')).toBe(true);
  });

  it('should call onDelete when delete button is clicked', () => {
    jest.spyOn(component, 'onDelete');
    const compiled = fixture.nativeElement;
    const deleteButton = compiled.querySelector('button');

    deleteButton.click();

    expect(component.onDelete).toHaveBeenCalled();
  });
});
