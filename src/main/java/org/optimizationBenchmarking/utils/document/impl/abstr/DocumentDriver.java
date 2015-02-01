package org.optimizationBenchmarking.utils.document.impl.abstr;

import org.optimizationBenchmarking.utils.bibliography.data.BibRecord;
import org.optimizationBenchmarking.utils.comparison.EComparison;
import org.optimizationBenchmarking.utils.document.spec.ECitationMode;
import org.optimizationBenchmarking.utils.document.spec.EFigureSize;
import org.optimizationBenchmarking.utils.document.spec.ELabelType;
import org.optimizationBenchmarking.utils.document.spec.IDocumentDriver;
import org.optimizationBenchmarking.utils.document.spec.ILabel;
import org.optimizationBenchmarking.utils.document.spec.TableCellDef;
import org.optimizationBenchmarking.utils.graphics.graphic.EGraphicFormat;
import org.optimizationBenchmarking.utils.graphics.graphic.spec.IGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.style.IStyle;
import org.optimizationBenchmarking.utils.graphics.style.color.ColorStyle;
import org.optimizationBenchmarking.utils.graphics.style.font.FontStyle;
import org.optimizationBenchmarking.utils.text.ESequenceMode;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;
import org.optimizationBenchmarking.utils.tools.impl.abstr.DocumentProducerTool;

/** A document driver. */
public abstract class DocumentDriver extends DocumentProducerTool
    implements IDocumentDriver {

  /** create the document driver */
  protected DocumentDriver() {
    super();
  }

  /**
   * Obtain the default graphics driver for this document type
   * 
   * @return the default graphics driver for this document type
   */
  protected IGraphicDriver getDefaultGraphicDriver() {
    return EGraphicFormat.NULL.getDefaultDriver();
  }

  /**
   * Encode a text output
   * 
   * @param raw
   *          the raw text output
   * @return the encoded version
   */
  protected ITextOutput encode(final ITextOutput raw) {
    return raw;
  }

  /**
   * Create a document header
   * 
   * @param owner
   *          the owning document
   * @return the document header
   */
  protected DocumentHeader createDocumentHeader(final Document owner) {
    return new DocumentHeader(owner);
  }

  /**
   * Create a document body
   * 
   * @param owner
   *          the owning document
   * @return the document body
   */
  protected DocumentBody createDocumentBody(final Document owner) {
    return new DocumentBody(owner);
  }

  /**
   * Create a document footer
   * 
   * @param owner
   *          the owning document
   * @return the document footer
   */
  protected DocumentFooter createDocumentFooter(final Document owner) {
    return new DocumentFooter(owner);
  }

  /**
   * Create an in-braces object
   * 
   * @param owner
   *          the owning text
   * @return the in-braces object
   */
  protected InBraces createInBraces(final PlainText owner) {
    return new InBraces(owner);
  }

  /**
   * Create an in-quotes object
   * 
   * @param owner
   *          the owning text
   * @return the in-quotes object
   */
  protected InQuotes createInQuotes(final PlainText owner) {
    return new InQuotes(owner);
  }

  /**
   * check a given style for use in a text
   * 
   * @param style
   *          the style to be checked
   */
  protected void checkStyleForText(final IStyle style) {
    if (style == null) {
      throw new IllegalArgumentException("Style most not be null."); //$NON-NLS-1$
    }
    if (style instanceof FontStyle) {
      return;
    }
    if (style instanceof ColorStyle) {
      if ((((ColorStyle) style).getRGB() & 0xffffff) != 0xffffff) {
        return;
      }
    }

    StyledText._forbid(style, true);
  }

  /**
   * Create a styled text.
   * 
   * @param owner
   *          the owning text
   * @param style
   *          the style to use
   * @return the styled text
   */
  protected StyledText createStyledText(final ComplexText owner,
      final IStyle style) {
    return new StyledText(owner, style);
  }

  /**
   * Create the in-line code
   * 
   * @param owner
   *          the owner
   * @return the in-line code
   */
  protected InlineCode createInlineCode(final ComplexText owner) {
    return new InlineCode(owner);
  }

  /**
   * Create the emphasize
   * 
   * @param owner
   *          the owner
   * @return the emphasize
   */
  protected Emphasize createEmphasize(final ComplexText owner) {
    return new Emphasize(owner);
  }

  /**
   * Create the sub-script text
   * 
   * @param owner
   *          the owner
   * @return the sub-script text
   */
  protected Subscript createSubscript(final Text owner) {
    return new Subscript(owner);
  }

  /**
   * Create the super-script text
   * 
   * @param owner
   *          the owner
   * @return the super-script text
   */
  protected Superscript createSuperscript(final Text owner) {
    return new Superscript(owner);
  }

  /**
   * Create an inline math object
   * 
   * @param owner
   *          the owner
   * @return the inline math context
   */
  protected InlineMath createInlineMath(final ComplexText owner) {
    return new InlineMath(owner);
  }

  /**
   * Create the bibliographic record
   * 
   * @param item
   *          the bibliographic record
   * @param mode
   *          the citation mode
   * @return the citation item
   */
  protected CitationItem createCitationItem(final BibRecord item,
      final ECitationMode mode) {
    return new CitationItem(item, mode);
  }

  /**
   * Create the reference item
   * 
   * @param type
   *          the type
   * @param seq
   *          the sequence mode
   * @param data
   *          the labels
   * @return the reference run
   */
  protected ReferenceRun createReferenceRun(final String type,
      final ESequenceMode seq, final Label[] data) {
    return new ReferenceRun(type, seq, data);
  }

  /**
   * create a label
   * 
   * @param owner
   *          the label owner
   * @param type
   *          the label type
   * @param mark
   *          the label mark
   * @param refText
   *          the reference text
   * @return the label
   */
  protected Label createLabel(final Document owner, final ELabelType type,
      final String mark, final String refText) {
    return new Label(owner, type, mark, refText);

  }

  /**
   * Create the title writer
   * 
   * @param owner
   *          the owner
   * @return the title writer
   */
  protected DocumentTitle createDocumentTitle(final DocumentHeader owner) {
    return new DocumentTitle(owner);
  }

  /**
   * Create the summary writer
   * 
   * @param owner
   *          the owner
   * @return the summary writer
   */
  protected DocumentSummary createDocumentSummary(
      final DocumentHeader owner) {
    return new DocumentSummary(owner);
  }

  /**
   * Create the section
   * 
   * @param owner
   *          the owner
   * @param useLabel
   *          the label to use
   * @param index
   *          the index
   * @return the section
   */
  protected Section createSection(final DocumentPart owner,
      final ILabel useLabel, final int index) {
    return new Section(owner, useLabel, index);
  }

  /**
   * Create the section title
   * 
   * @param owner
   *          the owner
   * @return the section title
   */
  protected SectionTitle createSectionTitle(final Section owner) {
    return new SectionTitle(owner);
  }

  /**
   * Create the section body
   * 
   * @param owner
   *          the owner
   * @return the section body
   */
  protected SectionBody createSectionBody(final Section owner) {
    return new SectionBody(owner);
  }

  /**
   * Create an enumeration
   * 
   * @param owner
   *          the owner
   * @return an enumeration
   */
  protected Enumeration createEnumeration(final StructuredText owner) {
    return new Enumeration(owner);
  }

  /**
   * Create an itemization
   * 
   * @param owner
   *          the owner
   * @return an itemization
   */
  protected Itemization createItemization(final StructuredText owner) {
    return new Itemization(owner);
  }

  /**
   * Create a table
   * 
   * @param owner
   *          the owner
   * @param useLabel
   *          the label to use
   * @param spansAllColumns
   *          does the table span all columns?
   * @param index
   *          the table's index
   * @param cells
   *          the cell definition
   * @return the table
   */
  protected Table createTable(final SectionBody owner,
      final ILabel useLabel, final boolean spansAllColumns,
      final int index, final TableCellDef... cells) {
    return new Table(owner, useLabel, spansAllColumns, index, cells);
  }

  /**
   * create a new figure
   * 
   * @param owner
   *          the owner
   * @param useLabel
   *          the label to use
   * @param size
   *          the figure size
   * @param path
   *          a path where the figure should be stored
   * @param index
   *          the figure's index within the section
   * @return the figure
   */
  protected Figure createFigure(final SectionBody owner,
      final ILabel useLabel, final EFigureSize size, final String path,
      final int index) {
    return new Figure(owner, useLabel, size, path, index);
  }

  /**
   * Create a figure series
   * 
   * @param owner
   *          the owner
   * @param useLabel
   *          the label to use, or {@code null} if none is needed
   * @param size
   *          the size for the figures
   * @param path
   *          the relative path, or {@code null} to use an automatically
   *          chosen path
   * @param index
   *          the figure series' index
   * @return the figure series
   */
  protected FigureSeries createFigureSeries(final SectionBody owner,
      final ILabel useLabel, final EFigureSize size, final String path,
      final int index) {
    return new FigureSeries(owner, useLabel, size, path, index);
  }

  /**
   * Create a code block
   * 
   * @param owner
   *          the owner
   * @param useLabel
   *          the label to use, or {@code null} if none is needed
   * @param spansAllColumns
   *          does the table span all columns?
   * @param index
   *          the index of the code block
   * @return the code block
   */
  protected Code createCode(final SectionBody owner,
      final ILabel useLabel, final boolean spansAllColumns, final int index) {
    return new Code(owner, useLabel, spansAllColumns, index);
  }

  /**
   * Create a equation
   * 
   * @param owner
   *          the owner
   * @param useLabel
   *          the label to use, or {@code null} if none is needed
   * @param index
   *          the index of the equation
   * @return the equation
   */
  protected Equation createEquation(final SectionBody owner,
      final ILabel useLabel, final int index) {
    return new Equation(owner, useLabel, index);
  }

  /**
   * Create an enumeration item
   * 
   * @param owner
   *          the owner
   * @return the item
   */
  protected EnumerationItem createEnumerationItem(final Enumeration owner) {
    return new EnumerationItem(owner);
  }

  /**
   * Create an itemization item
   * 
   * @param owner
   *          the owner
   * @return the item
   */
  protected ItemizationItem createItemizationItem(final Itemization owner) {
    return new ItemizationItem(owner);
  }

  /**
   * Create a mathematical add
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathAdd createMathAdd(final BasicMath owner) {
    return new MathAdd(owner);
  }

  /**
   * Create a mathematical sub
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathSub createMathSub(final BasicMath owner) {
    return new MathSub(owner);
  }

  /**
   * Create a mathematical mul
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathMul createMathMul(final BasicMath owner) {
    return new MathMul(owner);
  }

  /**
   * Create a mathematical division
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathDiv createMathDiv(final BasicMath owner) {
    return new MathDiv(owner);
  }

  /**
   * Create an inline mathematical division
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathDivInline createMathDivInline(final BasicMath owner) {
    return new MathDivInline(owner);
  }

  /**
   * Create a mathematical ln
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathLn createMathLn(final BasicMath owner) {
    return new MathLn(owner);
  }

  /**
   * Create a mathematical lg
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathLg createMathLg(final BasicMath owner) {
    return new MathLg(owner);
  }

  /**
   * Create a mathematical ld
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathLd createMathLd(final BasicMath owner) {
    return new MathLd(owner);
  }

  /**
   * Create a mathematical log
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathLog createMathLog(final BasicMath owner) {
    return new MathLog(owner);
  }

  /**
   * Create a mathematical mod
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathMod createMathMod(final BasicMath owner) {
    return new MathMod(owner);
  }

  /**
   * Create a mathematical pow
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathPow createMathPow(final BasicMath owner) {
    return new MathPow(owner);
  }

  /**
   * Create a mathematical root
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathRoot createMathRoot(final BasicMath owner) {
    return new MathRoot(owner);
  }

  /**
   * Create a mathematical square root
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathSqrt createMathSqrt(final BasicMath owner) {
    return new MathSqrt(owner);
  }

  /**
   * Create a mathematical comparison
   * 
   * @param owner
   *          the owner
   * @param comp
   *          the comparison
   * @return the function
   */
  protected MathCompare createMathCompare(final BasicMath owner,
      final EComparison comp) {
    return new MathCompare(owner, comp);
  }

  /**
   * Create a mathematical negate
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathNegate createMathNegate(final BasicMath owner) {
    return new MathNegate(owner);
  }

  /**
   * Create a mathematical absolute
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathAbs createMathAbsolute(final BasicMath owner) {
    return new MathAbs(owner);
  }

  /**
   * Create a mathematical factorial
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathFactorial createMathFactorial(final BasicMath owner) {
    return new MathFactorial(owner);
  }

  /**
   * Create a mathematical sine
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathSin createMathSin(final BasicMath owner) {
    return new MathSin(owner);
  }

  /**
   * Create a mathematical cos
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathCos createMathCos(final BasicMath owner) {
    return new MathCos(owner);
  }

  /**
   * Create a mathematical tan
   * 
   * @param owner
   *          the owner
   * @return the function
   */
  protected MathTan createMathTan(final BasicMath owner) {
    return new MathTan(owner);
  }

  /**
   * Create an in-braces object for mathematics
   * 
   * @param owner
   *          the owner
   * @return the in-braces object
   */
  protected MathInBraces createMathInBraces(final BasicMath owner) {
    return new MathInBraces(owner);
  }

  /**
   * Create a mathematics number
   * 
   * @param owner
   *          the owner
   * @return the number object
   */
  protected MathNumber createMathNumber(final BasicMath owner) {
    return new MathNumber(owner);
  }

  /**
   * Create a mathematics name
   * 
   * @param owner
   *          the owner
   * @return the name object
   */
  protected MathName createMathName(final BasicMath owner) {
    return new MathName(owner);
  }

  /**
   * Create a mathematics text
   * 
   * @param owner
   *          the owner
   * @return the text object
   */
  protected MathText createMathText(final BasicMath owner) {
    return new MathText(owner);
  }

  /**
   * Create a figure's caption
   * 
   * @param owner
   *          the owner
   * @return the caption
   */
  protected FigureCaption createFigureCaption(final Figure owner) {
    return new FigureCaption(owner);
  }

  /**
   * Create the code caption
   * 
   * @param owner
   *          the owner
   * @return the table caption
   */
  protected CodeCaption createCodeCaption(final Code owner) {
    return new CodeCaption(owner);
  }

  /**
   * Create the code body
   * 
   * @param owner
   *          the owner
   * @return the code body
   */
  protected CodeBody createCodeBody(final Code owner) {
    return new CodeBody(owner);
  }

  /**
   * Create the figure caption
   * 
   * @param owner
   *          the owning text
   * @return the figure caption
   */
  protected FigureSeriesCaption createFigureSeriesCaption(
      final FigureSeries owner) {
    return new FigureSeriesCaption(owner);
  }

  /**
   * Create a sub-figure
   * 
   * @param owner
   *          the owner
   * @param useLabel
   *          the label to use, or {@code null} if none is needed
   * @param path
   *          relative path
   * @return the new sub-figure
   */
  protected SubFigure createSubFigure(final FigureSeries owner,
      final ILabel useLabel, final String path) {
    return new SubFigure(owner, useLabel, path);
  }

  /**
   * Create a sub-figure caption
   * 
   * @param owner
   *          the owner
   * @return the new sub-figure caption
   */
  protected SubFigureCaption createSubFigureCaption(final SubFigure owner) {
    return new SubFigureCaption(owner);
  }

  /**
   * Create the table caption
   * 
   * @param owner
   *          the owner
   * @return the table caption
   */
  protected TableCaption createTableCaption(final Table owner) {
    return new TableCaption(owner);
  }

  /**
   * Create the table header
   * 
   * @param owner
   *          the owner
   * @return the table header
   */
  protected TableHeader createTableHeader(final Table owner) {
    return new TableHeader(owner);
  }

  /**
   * Create the table body
   * 
   * @param owner
   *          the owner
   * @return the table body
   */
  protected TableBody createTableBody(final Table owner) {
    return new TableBody(owner);
  }

  /**
   * Create the table footer
   * 
   * @param owner
   *          the owner
   * @return the table footer
   */
  protected TableFooter createTableFooter(final Table owner) {
    return new TableFooter(owner);
  }

  /**
   * Create the body row
   * 
   * @param owner
   *          the owner
   * @return the table body row
   */
  protected TableBodyRow createTableBodyRow(final TableBody owner) {
    return new TableBodyRow(owner);
  }

  /**
   * Create the footer row
   * 
   * @param owner
   *          the owner
   * @return the table footer row
   */
  protected TableFooterRow createTableFooterRow(final TableFooter owner) {
    return new TableFooterRow(owner);
  }

  /**
   * Create the header row
   * 
   * @param owner
   *          the owner
   * @return the table header row
   */
  protected TableHeaderRow createTableHeaderRow(final TableHeader owner) {
    return new TableHeaderRow(owner);
  }

  /**
   * Create a new table body cell
   * 
   * @param owner
   *          the owner
   * @param rowSpan
   *          the number of rows the cell should span
   * @param colSpan
   *          the number of columns the cell should span
   * @param def
   *          the table cell definition array
   * @return the body cell
   */
  protected TableBodyCell createTableBodyCell(final TableBodyRow owner,
      final int rowSpan, final int colSpan, final TableCellDef[] def) {
    return new TableBodyCell(owner, rowSpan, colSpan, def);
  }

  /**
   * Create a new table header cell
   * 
   * @param owner
   *          the owner
   * @param rowSpan
   *          the number of rows the cell should span
   * @param colSpan
   *          the number of columns the cell should span
   * @param def
   *          the table cell definition array
   * @return the header cell
   */
  protected TableHeaderCell createTableHeaderCell(
      final TableHeaderRow owner, final int rowSpan, final int colSpan,
      final TableCellDef[] def) {
    return new TableHeaderCell(owner, rowSpan, colSpan, def);
  }

  /**
   * Create a new table footer cell
   * 
   * @param owner
   *          the owner
   * @param rowSpan
   *          the number of rows the cell should span
   * @param colSpan
   *          the number of columns the cell should span
   * @param def
   *          the table cell definition array
   * @return the footer cell
   */
  protected TableFooterCell createTableFooterCell(
      final TableFooterRow owner, final int rowSpan, final int colSpan,
      final TableCellDef[] def) {
    return new TableFooterCell(owner, rowSpan, colSpan, def);
  }
}
