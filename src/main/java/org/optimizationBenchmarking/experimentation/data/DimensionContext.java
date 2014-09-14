package org.optimizationBenchmarking.experimentation.data;

import org.optimizationBenchmarking.utils.hierarchy.FSM;
import org.optimizationBenchmarking.utils.hierarchy.HierarchicalFSM;
import org.optimizationBenchmarking.utils.parsers.ClassParser;
import org.optimizationBenchmarking.utils.parsers.DoubleParser;
import org.optimizationBenchmarking.utils.parsers.LongParser;
import org.optimizationBenchmarking.utils.parsers.NumberParser;
import org.optimizationBenchmarking.utils.parsers.StrictDoubleParser;
import org.optimizationBenchmarking.utils.parsers.StrictLongParser;
import org.optimizationBenchmarking.utils.reflection.GetStaticConstantByName;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/** A builder for dimensions. */
public final class DimensionContext extends _NamedContext<Dimension> {

  /** we have a parser */
  private static final int FLAG_HAS_PARSER = (_NamedContext.FLAG_HAS_NAME << 1);
  /** we have a type */
  private static final int FLAG_HAS_TYPE = (DimensionContext.FLAG_HAS_PARSER << 1);
  /** we have a direction */
  private static final int FLAG_HAS_DIRECTION = (DimensionContext.FLAG_HAS_TYPE << 1);

  /** the parsers */
  private volatile NumberParser<Number> m_parser;

  /** the dimension type */
  private volatile EDimensionType m_dimensionType;

  /** the direction of the dimension */
  private volatile EDimensionDirection m_direction;

  /**
   * create
   * 
   * @param element
   *          the owning element
   */
  DimensionContext(final _DimensionSetContext element) {
    super(element);
    this.open();
  }

  /** {@inheritDoc} */
  @Override
  protected void fsmFlagsAppendName(final int flagValue,
      final int flagIndex, final MemoryTextOutput append) {
    switch (flagValue) {
      case FLAG_HAS_PARSER: {
        append.append("parserSet"); //$NON-NLS-1$
        return;
      }
      case FLAG_HAS_TYPE: {
        append.append("typeSet"); //$NON-NLS-1$
        return;
      }
      case FLAG_HAS_DIRECTION: {
        append.append("directionSet"); //$NON-NLS-1$
        return;
      }
      default: {
        super.fsmFlagsAppendName(flagValue, flagIndex, append);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final _DimensionSetContext getOwner() {
    return ((_DimensionSetContext) (super.getOwner()));
  }

  /**
   * Get the experiment set builder owning this dimension context
   * 
   * @return the experiment set builder owning this dimension context
   */
  public final ExperimentSetContext getBuilder() {
    return this.getOwner().getOwner();
  }

  /**
   * Get the parser used for the numbers in this dimension
   * 
   * @return the parser used for the numbers
   */
  public synchronized final NumberParser<Number> getParser() {
    return this.m_parser;
  }

  /**
   * Set the parser used for the numbers in this dimension
   * 
   * @param parser
   *          the parser used for the numbers
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public synchronized final void setParser(final NumberParser<?> parser) {
    if (parser == null) {
      throw new IllegalArgumentException(//
          "Cannot set parser to null."); //$NON-NLS-1$
    }
    if (this.m_parser != parser) {
      this.fsmFlagsAssertAndUpdate(FSM.FLAG_NOTHING,
          DimensionContext.FLAG_HAS_PARSER,
          DimensionContext.FLAG_HAS_PARSER, FSM.FLAG_NOTHING);
      this.fsmStateAssert(_FSM.STATE_OPEN);
      this.m_parser = ((NumberParser) parser);
    }
  }

  /**
   * Try to obtain the parser via reflection. Three formats are allowed:
   * <ol>
   * <li><code>qualified.class.name#publicConstant</code></li>
   * <li><code>qualified.class.name</code> + class has a
   * no-parameter-constructor</li>
   * <li><code>qualified.class.name:lowerBound:upperBound</code> + class
   * has a constructor taking two instances of {@link java.lang.Number}</li>
   * </ol>
   * 
   * @param parserDesc
   *          the parser constant
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public final void setParser(final String parserDesc) {
    final NumberParser<?> parser;
    final int idx1, idx2;

    idx1 = parserDesc.indexOf(':');
    if ((idx1 > 0) && (idx1 < (parserDesc.length() - 2))) {
      idx2 = parserDesc.indexOf(':', (idx1 + 1));
      if ((idx2 > 0) && (idx2 < (parserDesc.length() - 1))) {
        this.setParser(//
            parserDesc.substring(0, idx1),//
            parserDesc.substring(idx1 + 1, idx2),//
            parserDesc.substring(idx2 + 1));//
        return;
      }
    }

    try {
      if (parserDesc.indexOf('#') > 0) {
        parser = new GetStaticConstantByName<>(parserDesc,
            NumberParser.class).call();
      } else {
        parser = ((NumberParser) ((new ClassParser(NumberParser.class)
            .parseString(parserDesc)).newInstance()));
      }

    } catch (final Throwable t) {
      throw new RuntimeException(((("Invalid parser definition: '" + //$NON-NLS-1$
          parserDesc) + '\'') + '.'), t);
    }

    this.setParser(parser);
  }

  /**
   * Try to obtain the parser via reflection.
   * 
   * @param parserClass
   *          the parser class: must take two numbers as parameter
   * @param lowerBound
   *          the lower boundary
   * @param upperBound
   *          the upper boundary
   */
  public final void setParser(
      final Class<? extends NumberParser<?>> parserClass,
      final Number lowerBound, final Number upperBound) {
    final NumberParser<?> parser;

    try {
      parser = parserClass.getConstructor(Number.class, Number.class)
          .newInstance(this.normalize(lowerBound),
              this.normalize(upperBound));
    } catch (final Throwable t) {
      throw new RuntimeException(((((((//
          "Illegal parser class and configuration: " + //$NON-NLS-1$
          TextUtils.className(parserClass)) + ", '") + //$NON-NLS-1$
          lowerBound) + "', '") + upperBound) + '.'), t); //$NON-NLS-1$
    }

    this.setParser(parser);
  }

  /**
   * Try to obtain the parser via reflection.
   * 
   * @param parserClass
   *          the parser class: must take two numbers as parameter
   * @param lowerBound
   *          the lower boundary
   * @param upperBound
   *          the upper boundary
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public final void setParser(final String parserClass,
      final String lowerBound, final String upperBound) {
    Number lower, upper;
    Class<? extends NumberParser<?>> c;
    RuntimeException re;

    try {
      lower = StrictLongParser.STRICT_INSTANCE.parseString(lowerBound);
    } catch (final Throwable t1) {
      try {
        lower = StrictDoubleParser.STRICT_INSTANCE.parseString(lowerBound);
      } catch (final Throwable t2) {
        try {
          lower = LongParser.INSTANCE.parseString(lowerBound);
        } catch (final Throwable t3) {
          try {
            lower = DoubleParser.INSTANCE.parseString(lowerBound);
          } catch (final Throwable t4) {
            re = new RuntimeException(//
                "String '" + lowerBound + //$NON-NLS-1$
                    "' is not a number."); //$NON-NLS-1$
            re.addSuppressed(t1);
            re.addSuppressed(t2);
            re.addSuppressed(t3);
            re.addSuppressed(t4);
            throw re;
          }
        }
      }
    }

    try {
      upper = StrictLongParser.STRICT_INSTANCE.parseString(upperBound);
    } catch (final Throwable t1) {
      try {
        upper = StrictDoubleParser.STRICT_INSTANCE.parseString(upperBound);
      } catch (final Throwable t2) {
        try {
          upper = LongParser.INSTANCE.parseString(upperBound);
        } catch (final Throwable t3) {
          try {
            upper = DoubleParser.INSTANCE.parseString(upperBound);
          } catch (final Throwable t4) {
            re = new RuntimeException(//
                "String '" + upperBound + //$NON-NLS-1$
                    "' is not a number."); //$NON-NLS-1$
            re.addSuppressed(t1);
            re.addSuppressed(t2);
            re.addSuppressed(t3);
            re.addSuppressed(t4);
            throw re;
          }
        }
      }
    }

    try {
      c = new ClassParser(NumberParser.class).parseString(parserClass);
    } catch (final Throwable t) {
      throw new RuntimeException((("Invalid parser class '" + //$NON-NLS-1$
          parserClass) + '\'') + '.');
    }

    this.setParser(c, lower, upper);
  }

  /**
   * Set the type of this dimension
   * 
   * @param type
   *          the dimension type
   */
  public synchronized final void setType(final EDimensionType type) {
    if (type == null) {
      throw new IllegalArgumentException(//
          "Cannot set dimension type to null."); //$NON-NLS-1$
    }
    if (this.m_dimensionType != type) {
      this.fsmFlagsAssertAndUpdate(FSM.FLAG_NOTHING,
          DimensionContext.FLAG_HAS_TYPE, DimensionContext.FLAG_HAS_TYPE,
          FSM.FLAG_NOTHING);
      this.fsmStateAssert(_FSM.STATE_OPEN);
      this.m_dimensionType = type;
    }
  }

  /**
   * Set the type as constant
   * 
   * @param type
   *          the type
   */
  public final void setType(final String type) {
    this.setType(EDimensionType.valueOf(this.normalizeLocal(type)));
  }

  /**
   * Get the type of this dimension
   * 
   * @return the type of this dimension
   */
  public synchronized final EDimensionType getType() {
    return this.m_dimensionType;
  }

  /**
   * Set the direction of this dimension
   * 
   * @param direction
   *          the dimension direction
   */
  public synchronized final void setDirection(
      final EDimensionDirection direction) {
    if (direction == null) {
      throw new IllegalArgumentException(//
          "Cannot set dimension direction to null."); //$NON-NLS-1$
    }
    if (this.m_direction != direction) {
      this.fsmFlagsAssertAndUpdate(FSM.FLAG_NOTHING,
          DimensionContext.FLAG_HAS_DIRECTION,
          DimensionContext.FLAG_HAS_DIRECTION, FSM.FLAG_NOTHING);
      this.fsmStateAssert(_FSM.STATE_OPEN);
      this.m_direction = direction;
    }
  }

  /**
   * Set the direction as constant
   * 
   * @param direction
   *          the direction
   */
  public final void setDirection(final String direction) {
    this.setDirection(EDimensionDirection.valueOf(this
        .normalizeLocal(direction)));
  }

  /**
   * Get the direction of this dimension
   * 
   * @return the direction of this dimension
   */
  public synchronized final EDimensionDirection getDirection() {
    return this.m_direction;
  }

  /** {@inheritDoc} */
  @Override
  protected synchronized final void beforeChildOpens(
      final HierarchicalFSM child, final boolean hasOtherChildren) {
    super.beforeChildOpens(child, hasOtherChildren);
    this.throwChildNotAllowed(child);
  }

  /** {@inheritDoc} */
  @Override
  protected synchronized final void afterChildOpened(
      final HierarchicalFSM child, final boolean hasOtherChildren) {
    super.afterChildOpened(child, hasOtherChildren);
    this.throwChildNotAllowed(child);
  }

  /** {@inheritDoc} */
  @Override
  protected synchronized final void afterChildClosed(
      final HierarchicalFSM child) {
    super.afterChildClosed(child);
    this.throwChildNotAllowed(child);
  }

  /** {@inheritDoc} */
  @Override
  final synchronized Dimension _doCompile(final String name,
      final String description) {
    this.fsmFlagsAssertTrue(DimensionContext.FLAG_HAS_PARSER
        | DimensionContext.FLAG_HAS_TYPE
        | DimensionContext.FLAG_HAS_DIRECTION
        | _NamedContext.FLAG_HAS_NAME);
    return new Dimension(name,//
        description,//
        this.normalize(this.getParser()),//
        (this.getType()),//
        (this.getDirection()));
  }
}
