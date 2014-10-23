package org.optimizationBenchmarking.utils.graphics.chart.impl.abstr;

import java.awt.Color;
import java.awt.Stroke;

import org.optimizationBenchmarking.utils.graphics.chart.spec.IDataSeries;
import org.optimizationBenchmarking.utils.hierarchy.FSM;
import org.optimizationBenchmarking.utils.math.matrix.IMatrix;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/**
 * The base class for all data series
 */
class _DataSeriesBuilder extends _TitledElementBuilder implements
    IDataSeries {

  /** the color has been set */
  static final int FLAG_HAS_COLOR = (_TitledElementBuilder.FLAG_TITLED_ELEMENT_BUILDER_MAX << 1);
  /** the stroke has been set */
  static final int FLAG_HAS_STROKE = (_DataSeriesBuilder.FLAG_HAS_COLOR << 1);
  /** the data has been set */
  static final int FLAG_HAS_DATA = (_DataSeriesBuilder.FLAG_HAS_STROKE << 1);

  /** the unique identifier of the series */
  final int m_id;

  /** the color of this element */
  Color m_color;

  /** the stroke of this element */
  Stroke m_stroke;

  /** the data matrix */
  IMatrix m_data;

  /**
   * create the chart item
   * 
   * @param owner
   *          the owner
   * @param id
   *          the id
   */
  _DataSeriesBuilder(final _ChartElementBuilder owner, final int id) {
    super(owner);
    this.m_id = id;
  }

  /** {@inheritDoc} */
  @Override
  protected void fsmFlagsAppendName(final int flagValue,
      final int flagIndex, final MemoryTextOutput append) {
    switch (flagValue) {
      case FLAG_HAS_COLOR: {
        append.append("colorSet");break;} //$NON-NLS-1$
      case FLAG_HAS_STROKE: {
        append.append("strokeSet");break;} //$NON-NLS-1$
      case FLAG_HAS_DATA: {
        append.append("dataSet");break;} //$NON-NLS-1$      
      default: {
        super.fsmFlagsAppendName(flagValue, flagIndex, append);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setColor(final Color color) {
    this.fsmStateAssert(_ChartElementBuilder.STATE_ALIVE);
    this.fsmFlagsAssertAndUpdate(FSM.FLAG_NOTHING,
        _DataSeriesBuilder.FLAG_HAS_COLOR,
        _DataSeriesBuilder.FLAG_HAS_COLOR, FSM.FLAG_NOTHING);
    if (color == null) {
      throw new IllegalArgumentException(//
          "Cannot set color to null, if you dont want to set it, don't set it at all."); //$NON-NLS-1$
    }
    this.m_color = color;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setStroke(final Stroke stroke) {
    this.fsmStateAssert(_ChartElementBuilder.STATE_ALIVE);
    this.fsmFlagsAssertAndUpdate(FSM.FLAG_NOTHING,
        _DataSeriesBuilder.FLAG_HAS_STROKE,
        _DataSeriesBuilder.FLAG_HAS_STROKE, FSM.FLAG_NOTHING);
    if (stroke == null) {
      throw new IllegalArgumentException(//
          "Cannot set stroke to null, if you dont want to set it, don't set it at all."); //$NON-NLS-1$
    }
    this.m_stroke = stroke;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setData(final IMatrix matrix) {
    this.fsmStateAssert(_ChartElementBuilder.STATE_ALIVE);
    this.fsmFlagsAssertAndUpdate(FSM.FLAG_NOTHING,
        _DataSeriesBuilder.FLAG_HAS_DATA,
        _DataSeriesBuilder.FLAG_HAS_DATA, FSM.FLAG_NOTHING);
    this._checkMatrix(matrix);
    this.m_data = matrix;
  }

  /**
   * check the matrix
   * 
   * @param matrix
   *          the matrix
   */
  void _checkMatrix(final IMatrix matrix) {
    _DataSeries._checkMatrix(matrix);
  }

}