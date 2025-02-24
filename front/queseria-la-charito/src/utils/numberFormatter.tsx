export class NumberFormatter {
  private locale: string;
  private integerFormat: Intl.NumberFormat;
  private floatFormat: Intl.NumberFormat;
  private currencyFormat: Intl.NumberFormat;

  constructor(locale: string = 'es-ES') {
    this.locale = locale;
    this.integerFormat = new Intl.NumberFormat(this.locale, {
      style: 'decimal',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
      useGrouping: true
    });
    this.floatFormat = new Intl.NumberFormat(this.locale, {
      style: 'decimal',
      minimumFractionDigits: 0,
      maximumFractionDigits: 2,
      useGrouping: true
    });
    this.currencyFormat = new Intl.NumberFormat(this.locale, {
      style: 'currency',
      currency: 'ARS',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  formatNumber(value: number | undefined, preserveDecimals: boolean = false): string {
    if (value == undefined) return '';

    if (preserveDecimals) {
      return this.floatFormat.format(value);
    }
    return this.integerFormat.format(value);
  }

  formatCurrency(value: number, currency: string = 'ARS'): string {
    if (currency !== 'ARS') {
      return new Intl.NumberFormat(this.locale, {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(value);
    }
    return this.currencyFormat.format(value);
  }
}