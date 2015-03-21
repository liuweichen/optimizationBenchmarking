package org.optimizationBenchmarking.experimentation.io.impl.edi;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.experimentation.data.DimensionContext;
import org.optimizationBenchmarking.experimentation.data.ExperimentContext;
import org.optimizationBenchmarking.experimentation.data.ExperimentSetContext;
import org.optimizationBenchmarking.experimentation.data.InstanceContext;
import org.optimizationBenchmarking.experimentation.data.InstanceRunsContext;
import org.optimizationBenchmarking.experimentation.data.RunContext;
import org.optimizationBenchmarking.utils.hierarchy.HierarchicalFSM;
import org.optimizationBenchmarking.utils.io.structured.impl.abstr.IOJob;
import org.optimizationBenchmarking.utils.io.structured.impl.abstr.IOTool;
import org.optimizationBenchmarking.utils.io.xml.DelegatingHandler;
import org.optimizationBenchmarking.utils.parsers.DoubleParser;
import org.optimizationBenchmarking.utils.parsers.LongParser;
import org.optimizationBenchmarking.utils.parsers.NumberParser;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** the internal content handler */
final class _EDIContentHandler extends DelegatingHandler {

  /** the hierarchical fsm stack */
  private final ArrayList<HierarchicalFSM> m_stack;

  /** the internal string builder */
  private final MemoryTextOutput m_sb;

  /** the id counter */
  private volatile int m_id;

  /** are we inside a point */
  private int m_inPoint;

  /** the job */
  private final IOJob m_job;
  /** the logger */
  private final Logger m_logger;

  /**
   * create
   * 
   * @param owner
   *          the owning handler
   * @param esb
   *          the experiment set builder
   * @param job
   *          the job
   */
  public _EDIContentHandler(final DelegatingHandler owner,
      final ExperimentSetContext esb, final IOJob job) {
    super(owner);
    this.m_stack = new ArrayList<>();
    this.m_stack.add(esb);
    this.m_sb = new MemoryTextOutput();
    this.m_job = job;
    this.m_logger = job.getLogger();
  }

  /** {@inheritDoc} */
  @Override
  public final void doWarning(final SAXParseException e)
      throws SAXException {

    if ((this.m_logger != null) && //
        (this.m_logger.isLoggable(Level.WARNING))) {
      this.m_logger.log(Level.WARNING,
          "Warning during XMLFileType parsing.", //$NON-NLS-1$
          e);
    }

  }

  /** {@inheritDoc} */
  @Override
  public final void doError(final SAXParseException e) throws SAXException {
    try {
      this.m_job.handleError(e, "Error during XMLFileType parsing."); //$NON-NLS-1$
    } catch (final Exception ioe) {
      throw new SAXException(ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void doFatalError(final SAXParseException e)
      throws SAXException {
    try {
      this.m_job.handleError(e, "Fatal error during XMLFileType parsing."); //$NON-NLS-1$
    } catch (final Exception ioe) {
      throw new SAXException(ioe);
    }
  }

  /**
   * start the bounds element
   * 
   * @param atts
   *          the attributes
   */
  private final void __startBounds(final Attributes atts) {
    final String dim;
    final ArrayList<HierarchicalFSM> stack;
    final InstanceContext c;
    String lb, ub;

    stack = this.m_stack;
    c = ((InstanceContext) (stack.get(stack.size() - 1)));

    dim = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
        EDI.ATTRIBUTE_DIMENSION);
    if (dim != null) {

      lb = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
          EDI.ATTRIBUTE_FLOAT_LOWER_BOUND);
      if (lb == null) {
        lb = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_INTEGER_LOWER_BOUND);
      }

      ub = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
          EDI.ATTRIBUTE_FLOAT_UPPER_BOUND);
      if (ub == null) {
        ub = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_INTEGER_UPPER_BOUND);
      }

      if ((lb != null) || (ub != null)) {
        if (lb != null) {
          c.setLowerBound(dim, lb);
        }
        if (ub != null) {
          c.setUpperBound(dim, ub);
        }
      }
    }
  }

  /**
   * start the dimension
   * 
   * @param atts
   *          the attributes
   */
  @SuppressWarnings("resource")
  private final void __startDimension(final Attributes atts) {
    final ArrayList<HierarchicalFSM> stack;
    final DimensionContext d;
    final HierarchicalFSM fsm;
    EPrimitiveType pt;
    String s;
    final Number lb, ub;

    stack = this.m_stack;
    fsm = stack.get(stack.size() - 1);
    if (fsm instanceof DimensionContext) {
      d = ((DimensionContext) fsm);
    } else {
      d = this.__pop(ExperimentSetContext.class).createDimension();
      stack.add(d);
    }

    s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
        EDI.ATTRIBUTE_NAME);
    d.setName(s);

    if ((this.m_logger != null) && //
        (this.m_logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      this.m_logger.log(IOTool.FINE_LOG_LEVEL,
          ("Begin of dimension '" + s + '\'')); //$NON-NLS-1$
    }

    s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
        EDI.ATTRIBUTE_DESCRIPTION);
    if (s != null) {
      d.setDescription(s);
    }

    d.setType(EDI._parseDimensionType(//
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_DIMENSION_TYPE)));

    d.setDirection(EDI._parseDimensionDirection(//
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_DIMENSION_DIRECTION)));

    pt = EDI._parseDataType(DelegatingHandler.getAttributeNormalized(atts,
        EDI.NAMESPACE, EDI.ATTRIBUTE_DIMENSION_DATA_TYPE));

    s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
        EDI.ATTRIBUTE_INTEGER_LOWER_BOUND);
    if (s != null) {
      lb = LongParser.INSTANCE.parseString(s);
    } else {
      s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
          EDI.ATTRIBUTE_FLOAT_LOWER_BOUND);
      if (s != null) {
        lb = DoubleParser.INSTANCE.parseString(s);
      } else {
        lb = null;
      }
    }

    s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
        EDI.ATTRIBUTE_INTEGER_UPPER_BOUND);
    if (s != null) {
      ub = LongParser.INSTANCE.parseString(s);
    } else {
      s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
          EDI.ATTRIBUTE_FLOAT_UPPER_BOUND);
      if (s != null) {
        ub = DoubleParser.INSTANCE.parseString(s);
      } else {
        ub = null;
      }
    }

    d.setParser(NumberParser.createNumberParser(pt, lb, ub));
  }

  /**
   * pop all elements until finding one of the given class, then return
   * this element
   * 
   * @param clazz
   *          the class
   * @return the element (unclosed)
   * @param <T>
   *          the element type
   */
  @SuppressWarnings("resource")
  private final <T extends HierarchicalFSM> T __pop(final Class<T> clazz) {
    final ArrayList<HierarchicalFSM> stack;
    HierarchicalFSM x;
    int i;

    stack = this.m_stack;
    for (i = stack.size(); (--i) >= 0;) {
      x = stack.get(i);
      if (clazz.isInstance(x)) {
        return clazz.cast(x);
      }
      stack.remove(i);
      x.close();
    }

    throw new IllegalStateException("Did not find element of class " + //$NON-NLS-1$
        TextUtils.className(clazz));
  }

  /**
   * close all elements until and including finding one of the given class
   * 
   * @param clazz
   *          the class
   */
  @SuppressWarnings("resource")
  private final void __close(final Class<? extends HierarchicalFSM> clazz) {
    final ArrayList<HierarchicalFSM> stack;
    HierarchicalFSM x;
    int i;

    stack = this.m_stack;
    for (i = stack.size(); (--i) >= 0;) {
      x = stack.remove(i);
      x.close();
      if (clazz.isInstance(x)) {
        return;
      }
    }

    throw new IllegalStateException(
        "Could not find and close element of class " + //$NON-NLS-1$
            TextUtils.className(clazz));
  }

  /** end the dimension */
  private final void __endDimension() {
    this.__close(DimensionContext.class);
  }

  /**
   * start the experiment
   * 
   * @param atts
   *          the attributes
   */
  @SuppressWarnings("resource")
  private final void __startExperiment(final Attributes atts) {
    final ArrayList<HierarchicalFSM> stack;
    final ExperimentContext d;
    final HierarchicalFSM fsm;
    String s;

    stack = this.m_stack;
    fsm = stack.get(stack.size() - 1);

    if (fsm instanceof ExperimentContext) {
      d = ((ExperimentContext) fsm);
    } else {
      d = this.__pop(ExperimentSetContext.class).createExperiment();
      stack.add(d);
    }

    s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
        EDI.ATTRIBUTE_NAME);
    d.setName(s);

    if ((this.m_logger != null) && //
        (this.m_logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      this.m_logger.log(IOTool.FINE_LOG_LEVEL,
          ("Begin of experiment '" + s + '\'')); //$NON-NLS-1$
    }

    s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
        EDI.ATTRIBUTE_DESCRIPTION);
    if (s != null) {
      d.setDescription(s);
    }
  }

  /**
   * end the experiment
   */
  private final void __endExperiment() {
    this.__close(ExperimentContext.class);
  }

  /**
   * start the feature
   * 
   * @param atts
   *          the attributes
   */
  @SuppressWarnings("resource")
  private final void __startFeature(final Attributes atts) {
    final ArrayList<HierarchicalFSM> stack;
    final InstanceContext d;
    final HierarchicalFSM fsm;

    stack = this.m_stack;
    fsm = stack.get(stack.size() - 1);

    if (fsm instanceof InstanceContext) {
      d = ((InstanceContext) fsm);
    } else {
      d = this.__pop(ExperimentSetContext.class).createInstance();
      stack.add(d);
      d.setName(String.valueOf(this.m_id++));
    }

    d.setFeatureValue(
    //
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_NAME),//
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_FEATURE_DESCRIPTION),//
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_FEATURE_VALUE),//
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_FEATURE_VALUE_DESCRIPTION));
  }

  /**
   * start the instance
   * 
   * @param atts
   *          the attributes
   */
  @SuppressWarnings("resource")
  private final void __startInstance(final Attributes atts) {
    final ArrayList<HierarchicalFSM> stack;
    final InstanceContext d;
    final HierarchicalFSM fsm;
    String s;

    stack = this.m_stack;
    fsm = stack.get(stack.size() - 1);

    if (fsm instanceof InstanceContext) {
      d = ((InstanceContext) fsm);
    } else {
      d = this.__pop(ExperimentSetContext.class).createInstance();
      stack.add(d);
    }

    s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
        EDI.ATTRIBUTE_NAME);
    d.setName(s);

    if ((this.m_logger != null) && //
        (this.m_logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      this.m_logger.log(IOTool.FINE_LOG_LEVEL,
          ("Begin of instance '" + s + '\'')); //$NON-NLS-1$
    }

    s = DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
        EDI.ATTRIBUTE_DESCRIPTION);
    if (s != null) {
      d.setDescription(s);
    }
  }

  /** end the instance */
  private final void __endInstance() {
    this.__close(InstanceContext.class);
  }

  /**
   * start the instance runs
   * 
   * @param atts
   *          the attributes
   */
  @SuppressWarnings("resource")
  private final void __startInstanceRuns(final Attributes atts) {
    final ArrayList<HierarchicalFSM> stack;
    final ExperimentContext c;
    final InstanceRunsContext d;
    final HierarchicalFSM fsm;

    stack = this.m_stack;
    fsm = stack.get(stack.size() - 1);

    if (fsm instanceof ExperimentContext) {
      c = ((ExperimentContext) fsm);
    } else {
      c = this.__pop(ExperimentSetContext.class).createExperiment();
      stack.add(c);
      c.setName(String.valueOf(this.m_id++));
    }
    d = c.createInstanceRuns();
    stack.add(d);
    d.setInstance(DelegatingHandler.getAttributeNormalized(atts,
        EDI.NAMESPACE, EDI.ATTRIBUTE_INSTANCE));
  }

  /** end the instance runs */
  private final void __endInstanceRuns() {
    this.__close(InstanceRunsContext.class);
  }

  /** start a run */
  private final void __startRun() {
    this.m_stack.add(//
        this.__pop(InstanceRunsContext.class).createRun());
  }

  /** end the run */
  private final void __endRun() {
    this.__close(RunContext.class);
  }

  /** start a point */
  @SuppressWarnings("resource")
  private final void __startPoint() {
    final ArrayList<HierarchicalFSM> stack;
    final HierarchicalFSM fsm;

    stack = this.m_stack;
    fsm = stack.get(stack.size() - 1);

    checker: {
      if (fsm instanceof RunContext) {
        break checker;
      }
      stack.add(((InstanceRunsContext) fsm).createRun());
    }

    if ((this.m_inPoint++) == 0) {
      this.m_sb.clear();
    }
  }

  /** end the point */
  private final void __endPoint() {
    final ArrayList<HierarchicalFSM> stack;
    try {
      if ((--this.m_inPoint) == 0) {
        stack = this.m_stack;
        ((RunContext) (stack.get(stack.size() - 1))).addDataPoint(//
            TextUtils.normalize(this.m_sb.toString()));
      }
    } finally {
      this.m_sb.clear();
    }
  }

  /** start a point value */
  private final void __startVal() {
    if (this.m_inPoint > 0) {
      if (this.m_sb.length() > 0) {
        this.m_sb.append(' ');
      }
    }
  }

  /**
   * start the parameter
   * 
   * @param atts
   *          the attributes
   */
  @SuppressWarnings("resource")
  private final void __startParameter(final Attributes atts) {
    final ArrayList<HierarchicalFSM> stack;
    final ExperimentContext d;
    final HierarchicalFSM fsm;

    stack = this.m_stack;
    fsm = stack.get(stack.size() - 1);

    if (fsm instanceof ExperimentContext) {
      d = ((ExperimentContext) fsm);
    } else {
      d = this.__pop(ExperimentSetContext.class).createExperiment();
      stack.add(d);
      d.setName(String.valueOf(this.m_id++));
    }

    d.setParameterValue(
    //
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_NAME),//
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_PARAMETER_DESCRIPTION),//
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_PARAMETER_VALUE),//
        DelegatingHandler.getAttributeNormalized(atts, EDI.NAMESPACE,
            EDI.ATTRIBUTE_PARAMETER_VALUE_DESCRIPTION));
  }

  /** {@inheritDoc} */
  @Override
  protected final void doStartElement(final String uri,
      final String localName, final String qName,
      final Attributes attributes) throws SAXException {

    if (((this.m_logger != null) && //
    (this.m_logger.isLoggable(IOTool.FINER_LOG_LEVEL)))) {
      this.m_logger.log(IOTool.FINER_LOG_LEVEL, ("Start of element <" + //$NON-NLS-1$
          uri + ':' + localName + '>'));
    }

    if ((uri == null) || EDI.NAMESPACE.equalsIgnoreCase(uri)) {

      if (EDI.ELEMENT_BOUNDS.equalsIgnoreCase(localName)) {
        this.__startBounds(attributes);
        return;
      }
      if (EDI.ELEMENT_DIMENSION.equalsIgnoreCase(localName)) {
        this.__startDimension(attributes);
        return;
      }

      if (EDI.ELEMENT_EXPERIMENT.equalsIgnoreCase(localName)) {
        this.__startExperiment(attributes);
        return;
      }

      if (EDI.ELEMENT_FEATURE.equalsIgnoreCase(localName)) {
        this.__startFeature(attributes);
        return;
      }
      if (EDI.ELEMENT_FLOAT.equalsIgnoreCase(localName)) {
        this.__startVal();
        return;
      }
      if (EDI.ELEMENT_INSTANCE.equalsIgnoreCase(localName)) {
        this.__startInstance(attributes);
        return;
      }
      if (EDI.ELEMENT_INSTANCE_RUNS.equalsIgnoreCase(localName)) {
        this.__startInstanceRuns(attributes);
        return;
      }
      if (EDI.ELEMENT_INT.equalsIgnoreCase(localName)) {
        this.__startVal();
        return;
      }
      if (EDI.ELEMENT_PARAMETER.equalsIgnoreCase(localName)) {
        this.__startParameter(attributes);
        return;
      }
      if (EDI.ELEMENT_POINT.equalsIgnoreCase(localName)) {
        this.__startPoint();
        return;
      }
      if (EDI.ELEMENT_RUN.equalsIgnoreCase(localName)) {
        this.__startRun();
        return;
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  protected final void doEndElement(final String uri,
      final String localName, final String qName) throws SAXException {

    if (((this.m_logger != null) && //
    (this.m_logger.isLoggable(IOTool.FINER_LOG_LEVEL)))) {
      this.m_logger.log(IOTool.FINER_LOG_LEVEL, ("End of element </" + //$NON-NLS-1$
          uri + ':' + localName + '>'));
    }

    if ((uri == null) || EDI.NAMESPACE.equalsIgnoreCase(uri)) {

      if (EDI.ELEMENT_DIMENSION.equalsIgnoreCase(localName)) {
        this.__endDimension();
      }

      if (EDI.ELEMENT_EXPERIMENT.equalsIgnoreCase(localName)) {
        this.__endExperiment();
        return;
      }

      if (EDI.ELEMENT_INSTANCE.equalsIgnoreCase(localName)) {
        this.__endInstance();
        return;
      }

      if (EDI.ELEMENT_INSTANCE_RUNS.equalsIgnoreCase(localName)) {
        this.__endInstanceRuns();
        return;
      }

      if (EDI.ELEMENT_POINT.equalsIgnoreCase(localName)) {
        this.__endPoint();
        return;
      }

      if (EDI.ELEMENT_RUN.equalsIgnoreCase(localName)) {
        this.__endRun();
        return;
      }

      if (EDI.ELEMENT_EXPERIMENT_DATA.equalsIgnoreCase(localName)) {
        this.close();
        return;
      }
    }

  }

  /** {@inheritDoc} */
  @Override
  public final void characters(final char ch[], final int start,
      final int length) throws SAXException {
    try {
      if (this.m_inPoint > 0) {
        this.m_sb.append(ch, start, (start + length));
      }
    } finally {
      super.characters(ch, start, (start + length));
    }
  }

}
