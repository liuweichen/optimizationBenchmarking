package org.optimizationBenchmarking.utils.math.matrix.impl;

import org.optimizationBenchmarking.utils.math.matrix.AbstractMatrix;
import org.optimizationBenchmarking.utils.math.matrix.IMatrix;

/**
 * A two-dimensional matrix composed of {@code short} values
 */
public class ShortMatrix1D extends AbstractMatrix {

  /** the m */
  private final int m_m;

  /** the n */
  private final int m_n;

  /** the data */
  private final short[] m_data;

  /**
   * create the matrix
   *
   * @param data
   *          the data
   * @param m
   *          the m
   * @param n
   *          the n
   */
  public ShortMatrix1D(final short[] data, final int m, final int n) {
    super();

    if ((data == null) || (data.length <= 0)) {
      throw new IllegalArgumentException(//
          "Matrix data must not be null and must have at least one row."); //$NON-NLS-1$
    }

    if (data.length != (m * n)) {
      throw new IllegalArgumentException(//
          ((("Matrix data must contain exactly " + (m * n) + //$NON-NLS-1$
              " elements to facilitate an " + m) + '*') + n + //$NON-NLS-1$
              " matrix, but contains " + data.length) + '.'); //$NON-NLS-1$
    }

    this.m_data = data;
    this.m_m = m;
    this.m_n = n;
  }

  /** {@inheritDoc} */
  @Override
  public final int m() {
    return this.m_m;
  }

  /** {@inheritDoc} */
  @Override
  public final int n() {
    return this.m_n;
  }

  /** {@inheritDoc} */
  @Override
  public final double getDouble(final int row, final int column) {
    if ((row >= 0) && (row < this.m_m) && (column >= 0)
        && (column < this.m_n)) {
      return this.m_data[(row * this.m_n) + column];
    }
    return super.getDouble(row, column);// throw IndexOutOfBoundsException
  }

  /** {@inheritDoc} */
  @Override
  public final long getLong(final int row, final int column) {

    if ((row >= 0) && (row < this.m_m) && (column >= 0)
        && (column < this.m_n)) {
      return this.m_data[(row * this.m_n) + column];
    }
    return super.getLong(row, column);// throw IndexOutOfBoundsException
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isIntegerMatrix() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public IMatrix copy() {
    return this;
  }
}
