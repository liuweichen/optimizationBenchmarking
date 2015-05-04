package org.optimizationBenchmarking.utils.document.impl.xhtml10;

import org.optimizationBenchmarking.utils.document.impl.abstr.BasicMath;
import org.optimizationBenchmarking.utils.document.impl.abstr.MathLd;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** an mathematical ld function in a XHTML document */
final class _XHTML10MathLd extends MathLd {
  /** the operator */
  private final static char[] OP = new char[] { 'l', 'o', 'g', '<', 's',
      'u', 'b', '>', '2', '<', '/', 's', 'u', 'b', '>', };

  /**
   * Create a new mathematical function
   *
   * @param owner
   *          the owning text
   */
  _XHTML10MathLd(final BasicMath owner) {
    super(owner);
    this.open();
  }

  /** {@inheritDoc} */
  @Override
  protected final void render(final ITextOutput out, final char[][] data,
      final int size) {

    out.append(_XHTML10InlineMath.MO_TAB);
    out.append(_XHTML10InlineMath.MO_TR);

    out.append(_XHTML10InlineMath.MO_TD);
    out.append(_XHTML10MathLd.OP);
    out.append(_XHTML10Table.TD_END);

    out.append(_XHTML10InlineMath.MO_TD);
    out.append(data[0]);
    out.append(_XHTML10Table.TD_END);

    out.append(_XHTML10Table.TR_END);
    out.append(_XHTML10Table.TABLE_END);
  }
}
