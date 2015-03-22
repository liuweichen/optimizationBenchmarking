package test.junit.org.optimizationBenchmarking.experimentation.dataAndIO;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.optimizationBenchmarking.experimentation.data.DataPoint;
import org.optimizationBenchmarking.experimentation.data.Dimension;
import org.optimizationBenchmarking.experimentation.data.DimensionSet;
import org.optimizationBenchmarking.experimentation.data.Experiment;
import org.optimizationBenchmarking.experimentation.data.ExperimentSet;
import org.optimizationBenchmarking.experimentation.data.ExperimentSetContext;
import org.optimizationBenchmarking.experimentation.data.Feature;
import org.optimizationBenchmarking.experimentation.data.FeatureValue;
import org.optimizationBenchmarking.experimentation.data.Instance;
import org.optimizationBenchmarking.experimentation.data.InstanceRuns;
import org.optimizationBenchmarking.experimentation.data.Parameter;
import org.optimizationBenchmarking.experimentation.data.ParameterValue;
import org.optimizationBenchmarking.experimentation.data.Run;
import org.optimizationBenchmarking.experimentation.io.impl.edi.EDIInput;
import org.optimizationBenchmarking.experimentation.io.impl.edi.EDIOutput;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;

import test.junit.InstanceTest;
import test.junit.org.optimizationBenchmarking.utils.collections.lists.ArraySetViewTestBase;
import examples.org.optimizationBenchmarking.experimentation.dataAndIO.ExperimentSetCreator;

/** A class for creating experiment sets */
@Ignore
public class ExperimentSetTest extends InstanceTest<ExperimentSet> {

  /** the instance */
  private final ExperimentSetCreator m_inst;

  /**
   * create
   * 
   * @param creator
   *          the wrapped creator
   */
  protected ExperimentSetTest(final ExperimentSetCreator creator) {
    super();
    this.m_inst = creator;
  }

  /** {@inheritDoc} */
  @Override
  public final ExperimentSet getInstance() {
    try {
      return this.m_inst.getExperimentSet();
    } catch (final Throwable tt) {
      throw new RuntimeException("Failed to create experiment set.", //$NON-NLS-1$
          tt);
    }
  }

  /**
   * Test the experiment set data
   */
  @Test(timeout = 3600000)
  public final void testExperimentSetData() {
    final ArraySetView<Experiment> d;

    d = this.getInstance().getData();
    Assert.assertNotNull(d);
    Assert.assertFalse(d.isEmpty());
    Assert.assertTrue(d.size() > 0);
  }

  /**
   * Test the experiment dimension data
   */
  @Test(timeout = 3600000)
  public final void testExperimentSetDimensions() {
    final DimensionSet ds;

    ds = this.getInstance().getDimensions();
    Assert.assertNotNull(ds);
  }

  /**
   * Test the run matrix features
   */
  @Test(timeout = 24000000)
  public final void testExperimentRunsMatrix() {
    ArrayListView<InstanceRuns> irSet;
    ArrayListView<Run> rSet;
    final Random rand;
    final HashSet<InstanceRuns> irChoice;
    final HashSet<Run> rChoice;

    rand = new Random();
    irChoice = new HashSet<>();
    rChoice = new HashSet<>();

    for (final Experiment es : this.getInstance().getData()) {
      irSet = es.getData();
      if (irSet.isEmpty()) {
        continue;
      }
      irChoice.clear();
      irChoice.add(irSet.get(0));
      irChoice.add(irSet.get(irSet.size() - 1));
      while ((irChoice.size() < irSet.size()) && (irChoice.size() < 4)) {
        irChoice.add(irSet.get(rand.nextInt(irSet.size())));
      }

      for (final InstanceRuns ir : irChoice) {
        rSet = ir.getData();
        if (irSet.isEmpty()) {
          continue;
        }
        rChoice.clear();
        rChoice.add(rSet.get(0));
        rChoice.add(rSet.get(rSet.size() - 1));
        while ((rChoice.size() < rSet.size()) && (rChoice.size() < 4)) {
          rChoice.add(rSet.get(rand.nextInt(rSet.size())));
        }
        for (final Run r : rChoice) {
          new _MatrixTest<>(null, r, false).validateInstance();
        }
      }
    }
  }

  /**
   * Test the run list features
   */
  @Test(timeout = 134000000)
  public final void testExperimentRunsList() {
    ArrayListView<InstanceRuns> irSet;
    ArrayListView<Run> rSet;
    final Random rand;
    final HashSet<InstanceRuns> irChoice;
    final HashSet<Run> rChoice;

    rand = new Random();
    irChoice = new HashSet<>();
    rChoice = new HashSet<>();

    for (final Experiment es : this.getInstance().getData()) {
      irSet = es.getData();
      if (irSet.isEmpty()) {
        continue;
      }
      irChoice.clear();
      irChoice.add(irSet.get(0));
      irChoice.add(irSet.get(irSet.size() - 1));
      while ((irChoice.size() < irSet.size()) && (irChoice.size() < 4)) {
        irChoice.add(irSet.get(rand.nextInt(irSet.size())));
      }

      for (final InstanceRuns ir : irChoice) {
        rSet = ir.getData();
        if (irSet.isEmpty()) {
          continue;
        }
        rChoice.clear();
        rChoice.add(rSet.get(0));
        rChoice.add(rSet.get(rSet.size() - 1));
        while ((rChoice.size() < rSet.size()) && (rChoice.size() < 4)) {
          rChoice.add(rSet.get(rand.nextInt(rSet.size())));
        }
        for (final Run r : rChoice) {
          new ArraySetViewTestBase<>(null, r.getData(),//
              false).validateInstance();
        }
      }
    }
  }

  /**
   * make more
   * 
   * @param d
   *          the double
   * @return more
   */
  private static final double __more(final double d) {
    double a, b;

    a = Math.nextAfter((d + 1d), Double.POSITIVE_INFINITY);
    if (a >= Double.POSITIVE_INFINITY) {
      return a;
    }
    b = Math.nextAfter((((float) a) + 1f), Double.POSITIVE_INFINITY);
    if (b > a) {
      a = b;
    }

    if ((a >= Long.MIN_VALUE) && (a < Long.MAX_VALUE)) {
      b = (((long) a) + 1L);
    }
    if (b > a) {
      a = b;
    }

    return a;
  }

  /**
   * make less
   * 
   * @param d
   *          the double
   * @return less
   */
  private static final double __less(final double d) {
    double a, b;

    a = Math.nextAfter((d - 1d), Double.NEGATIVE_INFINITY);
    if (a <= Double.NEGATIVE_INFINITY) {
      return a;
    }
    b = Math.nextAfter((((float) a) - 1f), Double.NEGATIVE_INFINITY);
    if (b < a) {
      a = b;
    }
    if ((a > Long.MIN_VALUE) && (a <= Long.MAX_VALUE)) {
      b = (((long) a) - 1L);
    }
    if (b < a) {
      a = b;
    }

    return a;
  }

  /**
   * Test the run find function
   */
  @Test(timeout = 3600000)
  public final void testExperimentRunsFind() {
    ExperimentSet es;
    DimensionSet dims;
    DataPoint dp, found, cur, first, last;
    double o, p;
    ArraySetView<DataPoint> dps;
    int i, j;
    long a, b, c;

    es = this.getInstance();
    dims = es.getDimensions();

    for (final Experiment e : es.getData()) {
      for (final InstanceRuns ir : e.getData()) {
        for (final Run run : ir.getData()) {
          // begin run
          dps = run.getData();

          // check whether find really turns up the earliest data point
          // with a
          // given value
          for (final DataPoint x : dps) {
            for (final Dimension dim : dims.getData()) {

              // do we find the right point?
              if (dim.getDataType().isFloat()) {
                dp = run.find(dim.getIndex(), x.getDouble(dim.getIndex()));
                Assert.assertTrue(//
                    x.getDouble(dim.getIndex()) == dp.getDouble(dim
                        .getIndex()));
              } else {
                dp = run.find(dim.getIndex(), x.getLong(dim.getIndex()));
                Assert.assertEquals(x.getLong(dim.getIndex()),
                    dp.getLong(dim.getIndex()));
              }

              if (dp.compareTo(x) > 0) {
                Assert.fail("dim " + dim.getIndex() + ": " + //$NON-NLS-1$//$NON-NLS-2$
                    dp + " must come before or at " + x); //$NON-NLS-1$
              }
              if (x.compareTo(dp) < 0) {
                Assert.fail("dim " + dim.getIndex() + ": " + //$NON-NLS-1$//$NON-NLS-2$
                    x + " must come after or at " + dp); //$NON-NLS-1$
              }

              // check for the earliest point
              found = null;
              j = dps.size();

              finder: for (i = 0; i < j; i++) {
                cur = dps.get(i);
                if (dim.getDataType().isFloat()) {
                  if (cur.getDouble(dim.getIndex()) == dp.getDouble(dim
                      .getIndex())) {
                    found = cur;
                    break finder;
                  }
                } else {
                  if (cur.getLong(dim.getIndex()) == dp.getLong(dim
                      .getIndex())) {
                    found = cur;
                    break finder;
                  }
                }
              }

              Assert.assertSame(dp, found);
            }
          }

          // now test values which are not in the list
          first = dps.get(0);
          last = dps.get(dps.size() - 1);

          for (final Dimension dim : dims.getData()) {
            if (dim.getDirection().isIncreasing()) {
              o = ExperimentSetTest
                  .__less(first.getDouble(dim.getIndex()));
              p = ExperimentSetTest.__more(last.getDouble(dim.getIndex()));
            } else {
              o = ExperimentSetTest
                  .__more(first.getDouble(dim.getIndex()));
              p = ExperimentSetTest.__less(last.getDouble(dim.getIndex()));
            }

            if (dim.getDimensionType().isSolutionQualityMeasure()) {
              Assert.assertNull(run.find(dim.getIndex(), p));
              Assert.assertEquals(first, run.find(dim.getIndex(), o));
            } else {
              Assert.assertEquals(last, run.find(dim.getIndex(), p));
              Assert.assertNull(run.find(dim.getIndex(), o));
            }
          }

          // now let's test values in-between two points
          for (i = (dps.size() - 1); (--i) > 0;) {
            first = dps.get(i);
            last = dps.get(i + 1);

            for (final Dimension dim : dims.getData()) {
              a = first.getLong(dim.getIndex());
              b = last.getLong(dim.getIndex());
              if (a == b) {
                continue;
              }
              c = ((a + b) / 2L);
              if ((c <= a) || (c >= b)) {
                continue;
              }

              if (dim.getDimensionType().isSolutionQualityMeasure()) {
                Assert.assertSame(last, run.find(dim.getIndex(), c));
              } else {
                Assert.assertSame(first, run.find(dim.getIndex(), c));
              }

            }
          }

          // end run
        }
      }
    }
  }

  /**
   * Test whether the experiment set has a canonical representation under
   * the EDI serialization: An experiment set can be stored to EDI,
   * resulting in string {@code s1}. A new experiment set can then be
   * deserialized from {@code s1}. If we then serialize this new experiment
   * set (again to EDI), this should in a string {@code s2} with
   * {@code s1.equals(s2)}.
   */
  @Test(timeout = 3600000)
  public void testEDISerializationCanonical() {
    final ExperimentSet inst, es1, es2;
    final EDIOutput output;
    final EDIInput input;
    String s1, s2;

    output = EDIOutput.getInstance();
    Assert.assertNotNull(output);
    Assert.assertTrue(output.canUse());
    input = EDIInput.getInstance();
    Assert.assertNotNull(input);
    Assert.assertTrue(input.canUse());

    inst = this.getInstance();
    try {
      try (final StringWriter w = new StringWriter()) {
        output.use().setWriter(w).setSource(inst).create().call();
        s1 = w.toString();
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }

    try {
      try (final StringReader r = new StringReader(s1)) {
        try (final ExperimentSetContext esc = new ExperimentSetContext()) {
          input.use().addReader(r).setDestination(esc).create().call();
          es1 = esc.create();
        }
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    ExperimentSetTest._assertEquals(inst, es1);

    try {
      try (final StringWriter w = new StringWriter()) {
        output.use().setWriter(w).setSource(es1).create().call();
        s2 = w.toString();
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    Assert.assertEquals(s1, s2);
    s1 = null;

    try {
      try (final StringReader r = new StringReader(s2)) {
        s2 = null;
        try (final ExperimentSetContext esc = new ExperimentSetContext()) {
          input.use().addReader(r).setDestination(esc).create().call();
          es2 = esc.create();
        }
      }
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    ExperimentSetTest._assertEquals(inst, es2);
    ExperimentSetTest._assertEquals(es1, es2);

  }

  /**
   * assert that two experiment sets are equal
   * 
   * @param a
   *          set a
   * @param b
   *          set b
   */
  static final void _assertEquals(final ExperimentSet a,
      final ExperimentSet b) {
    int s;
    ArraySetView<Experiment> ae, be;
    ArraySetView<Feature> af, bf;
    ArraySetView<Parameter> ap, bp;
    ArraySetView<Dimension> ad, bd;
    Dimension d1, d2;

    ae = a.getData();
    be = b.getData();

    Assert.assertEquals(s = ae.size(), be.size());
    for (; (--s) >= 0;) {
      ExperimentSetTest._assertEquals(ae.get(s), be.get(s));
    }

    af = a.getFeatures().getData();
    bf = b.getFeatures().getData();
    Assert.assertEquals(s = af.size(), bf.size());
    for (; (--s) >= 0;) {
      ExperimentSetTest._assertEquals(af.get(s), bf.get(s));
    }

    ap = a.getParameters().getData();
    bp = b.getParameters().getData();
    Assert.assertEquals(s = ap.size(), bp.size());
    for (; (--s) >= 0;) {
      ExperimentSetTest._assertEquals(ap.get(s), bp.get(s));
    }

    ad = a.getDimensions().getData();
    bd = b.getDimensions().getData();
    Assert.assertEquals(s = ad.size(), bd.size());
    for (; (--s) >= 0;) {
      d1 = ad.get(s);
      d2 = bd.get(s);
      Assert.assertEquals(d1.getName(), d2.getName());
      Assert.assertEquals(d1.getDescription(), d2.getDescription());
      Assert.assertSame(d1.getDataType(), d2.getDataType());
      Assert.assertSame(d1.getDimensionType(), d2.getDimensionType());
      Assert.assertSame(d1.getDirection(), d2.getDirection());
      Assert.assertSame(d1.getParser().getOutputClass(), d2.getParser()
          .getOutputClass());
    }
  }

  /**
   * assert that parameter values are equal
   * 
   * @param a
   *          the parameter value
   * @param b
   *          the parameter value
   */
  private static final void __assertVEquals(final Object a, final Object b) {
    boolean id1, id2;
    long l1, l2;
    double d1, d2;
    String s1, s2;

    if ((a instanceof Number) && (b instanceof Number)) {

      if ((a instanceof Float) || (a instanceof Double)) {
        d1 = ((Number) a).doubleValue();
        l1 = 0L;
        id1 = true;
      } else {
        d1 = Double.NaN;
        l1 = ((Number) a).longValue();
        id1 = false;
      }

      if ((b instanceof Float) || (b instanceof Double)) {
        d2 = ((Number) b).doubleValue();
        l2 = 0L;
        id2 = true;
      } else {
        d2 = Double.NaN;
        l2 = ((Number) b).longValue();
        id2 = false;
      }

      if (id1 == id2) {
        if (id1) {
          Assert.assertEquals(d1, d2, 1e-14);
        } else {
          Assert.assertEquals(l1, l2);
        }
        return;
      }

    } else {
      checkString: {
        if (a instanceof String) {
          s1 = ((String) a);
        } else {
          if (a instanceof Character) {
            s1 = a.toString();
          } else {
            break checkString;
          }
        }

        if (b instanceof String) {
          s2 = ((String) b);
        } else {
          if (b instanceof Character) {
            s2 = b.toString();
          } else {
            break checkString;
          }
        }

        Assert.assertEquals(s1, s2);
        return;
      }
    }

    Assert.assertEquals(a, b);
  }

  /**
   * assert that two experiment sets are equal
   * 
   * @param a
   *          set a
   * @param b
   *          set b
   */
  static final void _assertEquals(final Experiment a, final Experiment b) {
    Iterator<Map.Entry<Parameter, Object>> x, y;
    Map.Entry<Parameter, Object> xe, ye;
    boolean z;
    ArraySetView<InstanceRuns> ia, ib;
    InstanceRuns iae, ibe;
    int si, sr, sp;
    ArraySetView<Run> ra, rb;
    Run rae, rbe;
    ArraySetView<DataPoint> da, db;

    Assert.assertEquals(a.getName(), b.getName());
    Assert.assertEquals(a.getDescription(), b.getDescription());

    x = a.parameters().entrySet().iterator();
    y = b.parameters().entrySet().iterator();

    outer: for (;;) {
      z = x.hasNext();
      Assert.assertTrue(z == y.hasNext());
      if (!z) {
        break outer;
      }
      xe = x.next();
      ye = y.next();
      ExperimentSetTest.__assertVEquals(xe.getValue(), ye.getValue());
      ExperimentSetTest._assertEquals(xe.getKey(), ye.getKey());
    }

    ia = a.getData();
    ib = b.getData();
    si = ia.size();
    Assert.assertEquals(si, ib.size());
    for (; (--si) >= 0;) {
      iae = ia.get(si);
      ibe = ib.get(si);
      ExperimentSetTest
          ._assertEquals(iae.getInstance(), ibe.getInstance());
      ra = iae.getData();
      rb = ibe.getData();
      sr = ra.size();
      Assert.assertEquals(sr, rb.size());
      for (; (--sr) >= 0;) {
        rae = ra.get(sr);
        rbe = rb.get(sr);

        da = rae.getData();
        db = rbe.getData();
        sp = da.size();
        Assert.assertEquals(sp, db.size());
        for (; (--sp) >= 0;) {
          Assert.assertEquals(da.get(sp), db.get(sp));
        }
      }
    }

  }

  /**
   * assert that two experiment sets are equal
   * 
   * @param a
   *          set a
   * @param b
   *          set b
   */
  static final void _assertEquals(final Parameter a, final Parameter b) {
    ArraySetView<ParameterValue> x, y;
    ParameterValue xe, ye;
    int s;

    Assert.assertEquals(a.getName(), b.getName());
    Assert.assertEquals(a.getDescription(), b.getDescription());

    x = a.getData();
    y = b.getData();
    s = x.size();
    Assert.assertEquals(s, y.size());
    for (; (--s) >= 0;) {
      xe = x.get(s);
      ye = y.get(s);
      Assert.assertEquals(xe.getName(), ye.getName());
      Assert.assertEquals(xe.getDescription(), ye.getDescription());
      ExperimentSetTest.__assertVEquals(xe.getValue(), ye.getValue());
    }
  }

  /**
   * assert that two experiment sets are equal
   * 
   * @param a
   *          set a
   * @param b
   *          set b
   */
  static final void _assertEquals(final Feature a, final Feature b) {
    ArraySetView<FeatureValue> x, y;
    FeatureValue xe, ye;
    int s;

    Assert.assertEquals(a.getName(), b.getName());
    Assert.assertEquals(a.getDescription(), b.getDescription());

    x = a.getData();
    y = b.getData();
    s = x.size();
    Assert.assertEquals(s, y.size());
    for (; (--s) >= 0;) {
      xe = x.get(s);
      ye = y.get(s);
      Assert.assertEquals(xe.getName(), ye.getName());
      Assert.assertEquals(xe.getDescription(), ye.getDescription());
      ExperimentSetTest.__assertVEquals(xe.getValue(), ye.getValue());
    }
  }

  /**
   * assert that two experiment sets are equal
   * 
   * @param a
   *          set a
   * @param b
   *          set b
   */
  static final void _assertEquals(final Instance a, final Instance b) {
    Iterator<Map.Entry<Feature, Object>> x, y;
    Map.Entry<Feature, Object> xe, ye;
    boolean z;

    Assert.assertEquals(a.getName(), b.getName());
    Assert.assertEquals(a.getDescription(), b.getDescription());

    x = a.features().entrySet().iterator();
    y = b.features().entrySet().iterator();

    outer: for (;;) {
      z = x.hasNext();
      Assert.assertTrue(z == y.hasNext());
      if (!z) {
        break outer;
      }
      xe = x.next();
      ye = y.next();
      ExperimentSetTest.__assertVEquals(xe.getValue(), ye.getValue());
      ExperimentSetTest._assertEquals(xe.getKey(), ye.getKey());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void testSerializationAndDeserializationEquals() {
    // ignored: runs are not serializable
  }

  /** {@inheritDoc} */
  @Override
  public void validateInstance() {
    super.validateInstance();
    this.testExperimentRunsMatrix();
    this.testExperimentRunsList();
    this.testExperimentRunsFind();
    this.testEDISerializationCanonical();
  }
}