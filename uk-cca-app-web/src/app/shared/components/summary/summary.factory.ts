import { DownloadableFile } from '@shared/utils';

import { SummaryData, SummarySection } from './type';

export class SummaryFactory {
  private readonly _data: SummaryData = [];

  addSection(header: string, changeLink = '', opts: SummaryData[number]['opts'] = {}) {
    this._data.push({ data: [], header, changeLink, opts });
    return this;
  }
  /**
   *
   * @param key
   * @param value - Value is the actual display value of the summary row.
   * **Remember that this ALWAYS has to be nullable due to readonly summary requirements**
   *
   * Example:
   *
   *  Incorrect pass of value - eligibility.authorisationNumber
   *
   * Correct pass of value - eligibility?.authorisationNumber. Notice the question mark.
   * @param opts
   * @returns
   */
  addRow(key: SummarySection['key'], value: SummarySection['value'], opts: Omit<SummarySection, 'key' | 'value'> = {}) {
    value = value instanceof Array ? value : [value];
    this._data[this._data.length - 1].data.push({ key, value, ...opts });
    return this;
  }

  addChangeRow(
    key: SummarySection['key'],
    value: SummarySection['value'],
    opts: Omit<SummarySection, 'key' | 'value' | 'change'> = {},
  ) {
    return this.addRow(key, value, { change: true, ...opts });
  }

  addFileListRow(
    key: SummarySection['key'],
    value: DownloadableFile[],
    opts: Omit<SummarySection, 'key' | 'value'> = {},
  ) {
    return this.addRow(key, value, { isFileList: true, ...opts });
  }

  create() {
    return this._data as SummaryData;
  }
}
