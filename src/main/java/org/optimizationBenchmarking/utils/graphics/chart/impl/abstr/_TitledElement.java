package org.optimizationBenchmarking.utils.graphics.chart.impl.abstr;

import java.awt.Font;

import org.optimizationBenchmarking.utils.text.TextUtils;

/** The base class for fully constructed titled elements */
class _TitledElement {

  /** the title */
  private final String m_title;

  /** the title font */
  private final Font m_titleFont;

  /**
   * Create a titled element
   * 
   * @param title
   *          the title, or {@code null} if no title is specified
   * @param titleFont
   *          the title font, or {@code null} if no specific font is set
   */
  _TitledElement(final String title, final Font titleFont) {
    super();
    this.m_title = TextUtils.normalize(title);
    if ((titleFont != null) && (this.m_title == null)) {
      throw new IllegalArgumentException(//
          "If title is not specified, a title font cannot be specified either."); //$NON-NLS-1$
    }
    this.m_titleFont = titleFont;
  }

  /**
   * Get the title of this titled element, or {@code null} if no title is
   * set
   * 
   * @return the title of this titled element, or {@code null} if no title
   *         is set
   */
  public final String getTitle() {
    return this.m_title;
  }

  /**
   * Get the title font, i.e., the font used to render the title
   * 
   * @return the title font, or {@code null} if no font is specified for
   *         the title
   */
  public final Font getTitleFont() {
    return this.m_titleFont;
  }
}
