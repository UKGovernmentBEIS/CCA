import { expect } from 'vitest';

expect.addSnapshotSerializer({
  test: (val: unknown): val is string => typeof val === 'string' && val.includes('_ng'),
  print: (val, _print, _indent, _options, _colors) => {
    const str = val as string;
    if (!str.includes('_ng')) return str;

    return str
      .replace(/\s_ngcontent-[\w-]+=""/g, '')
      .replace(/\s_nghost-[\w-]+=""/g, '')
      .replace(/_ngcontent-[\w-]+/g, '')
      .replace(/_nghost-[\w-]+/g, '');
  },
});
