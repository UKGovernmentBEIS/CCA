import { UuidFilePair } from '@shared/components';

import { fileUtils } from './files';

describe('fileUtils', () => {
  const pair = (uuid: string, name: string): UuidFilePair => ({ uuid, file: { name } });

  describe('toFiles', () => {
    it('should map file uuids and their attachment names', () => {
      expect(fileUtils.toFiles(['uuid-1', 'uuid-2'], { 'uuid-1': 'first.pdf', 'uuid-2': 'second.pdf' })).toEqual([
        { uuid: 'uuid-1', file: { name: 'first.pdf' } },
        { uuid: 'uuid-2', file: { name: 'second.pdf' } },
      ]);
    });

    it('should fall back to a placeholder name when the attachments map has no entry for the uuid', () => {
      // The attachments map of a payload is not guaranteed to cover every referenced uuid. The name
      // is then empty rather than undefined: `UploadedFileRef.name` is typed as a string, the file
      // list renders `UNKNOWN_FILE_NAME` for it and payloads skip it instead of inventing a name.
      expect(fileUtils.toFiles(['uuid-1'], undefined)).toEqual([{ uuid: 'uuid-1', file: { name: '' } }]);
      expect(fileUtils.toFiles(['uuid-1'], {})).toEqual([{ uuid: 'uuid-1', file: { name: '' } }]);
      expect(fileUtils.toFiles(['uuid-1'], { 'uuid-1': '' })).toEqual([{ uuid: 'uuid-1', file: { name: '' } }]);
    });

    it('should ignore empty uuids and non-list input', () => {
      expect(fileUtils.toFiles([null, 'uuid-1', undefined], { 'uuid-1': 'first.pdf' })).toEqual([
        { uuid: 'uuid-1', file: { name: 'first.pdf' } },
      ]);
      expect(fileUtils.toFiles(null, {})).toEqual([]);
    });
  });

  describe('toUUIDs', () => {
    it('should map files to their uuids', () => {
      expect(fileUtils.toUUIDs([pair('uuid-1', 'first.pdf'), pair('uuid-2', 'second.pdf')])).toEqual([
        'uuid-1',
        'uuid-2',
      ]);
    });

    it('should drop files without a uuid', () => {
      // Files that are still uploading have no uuid, and entries without a file reference may have none.
      expect(fileUtils.toUUIDs([pair('uuid-1', 'first.pdf'), { file: { name: 'in flight.pdf' }, uuid: null }])).toEqual(
        ['uuid-1'],
      );
    });

    it('should ignore empty entries and non-list input', () => {
      expect(fileUtils.toUUIDs([null, pair('uuid-1', 'first.pdf'), undefined])).toEqual(['uuid-1']);
      expect(fileUtils.toUUIDs(null)).toEqual([]);
    });
  });

  describe('toAttachments', () => {
    it('should map uuids to attachment names', () => {
      expect(fileUtils.toAttachments([pair('uuid-1', 'first.pdf'), pair('uuid-2', 'second.pdf')])).toEqual({
        'uuid-1': 'first.pdf',
        'uuid-2': 'second.pdf',
      });
    });

    it('should skip entries without a file or a name', () => {
      expect(
        fileUtils.toAttachments([
          { uuid: 'uuid-1', file: { name: 'first.pdf' } },
          { uuid: 'uuid-2', file: undefined },
          { uuid: 'uuid-3', file: null },
          { uuid: 'uuid-4', file: { name: undefined } },
          { uuid: 'uuid-5', file: { name: '' } },
          { uuid: null, file: { name: 'no uuid.pdf' } },
        ]),
      ).toEqual({ 'uuid-1': 'first.pdf' });
    });

    it('should ignore empty entries and non-list input', () => {
      expect(fileUtils.toAttachments([null, pair('uuid-1', 'first.pdf'), undefined])).toEqual({
        'uuid-1': 'first.pdf',
      });
      expect(fileUtils.toAttachments(null)).toEqual({});
    });
  });

  describe('extractAttachments', () => {
    it('should keep the names of the given uuids only', () => {
      expect(fileUtils.extractAttachments(['uuid-1'], { 'uuid-1': 'first.pdf', 'uuid-2': 'second.pdf' })).toEqual({
        'uuid-1': 'first.pdf',
      });
    });
  });
});
