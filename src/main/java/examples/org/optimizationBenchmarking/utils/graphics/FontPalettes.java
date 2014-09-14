package examples.org.optimizationBenchmarking.utils.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.optimizationBenchmarking.utils.graphics.DoubleDimension;
import org.optimizationBenchmarking.utils.graphics.graphic.Graphic;
import org.optimizationBenchmarking.utils.graphics.graphic.IGraphicDriver;
import org.optimizationBenchmarking.utils.graphics.style.PaletteIODriver;
import org.optimizationBenchmarking.utils.graphics.style.StyleApplication;
import org.optimizationBenchmarking.utils.graphics.style.font.FontPalette;
import org.optimizationBenchmarking.utils.graphics.style.font.FontPaletteBuilder;
import org.optimizationBenchmarking.utils.graphics.style.font.FontStyle;
import org.optimizationBenchmarking.utils.math.units.ELength;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/**
 * An example used to illustrate the available color palettes.
 */
public class FontPalettes {

  /** the graphic driver to use */
  private static final IGraphicDriver[] DRIVERS = GraphicsExample.DRIVERS;

  /** the palettes to print */
  private static final FontPalette[] PALETTES;

  static {
    PALETTES = new FontPalette[1];
    try (final FontPaletteBuilder tb = new FontPaletteBuilder()) {
      PaletteIODriver.INSTANCE.loadResource(tb, FontPalettes.class,
          "examples.font.palette"); //$NON-NLS-1$
      FontPalettes.PALETTES[0] = tb.getResult();
    } catch (final Throwable tt) {
      tt.printStackTrace();
    }
  }

  /**
   * run the example: there are problems with the pdf output
   * 
   * @param args
   *          the arguments
   * @throws IOException
   *           if i/o fails
   */
  public static final void main(final String[] args) throws IOException {
    final Path dir;

    if ((args != null) && (args.length > 0)) {
      dir = Paths.get(args[0]);
    } else {
      dir = Files.createTempDirectory("graphics"); //$NON-NLS-1$
    }

    for (final IGraphicDriver d : FontPalettes.DRIVERS) {
      for (final FontPalette p : FontPalettes.PALETTES) {//
        FontPalettes.__paint(
            dir.resolve(d.getClass().getSimpleName() + '_'
                + p.getClass().getSimpleName()), d, p);
      }
    }
  }

  /**
   * paint the palette
   * 
   * @param fileBlueprint
   *          the file name blueprint
   * @param palette
   *          the palette
   * @param driver
   *          the graphic driver to use
   */
  private static final void __paint(final Path fileBlueprint,
      final IGraphicDriver driver, final FontPalette palette) {
    final Rectangle2D b;
    final int s;
    final double w;
    final ArrayList<FontStyle> styles;
    final MemoryTextOutput mo;
    double h, hh;
    int i;
    Font f;
    String x;
    FontStyle st;
    boolean q;

    styles = new ArrayList<>(palette.size() + 1);
    styles.add(palette.getDefaultFont());
    styles.add(palette.getCodeFont());
    styles.add(palette.getEmphFont());
    styles.addAll(palette);

    mo = new MemoryTextOutput();

    s = styles.size();

    try (final Graphic g = driver.createGraphic(driver
        .createGraphicID(fileBlueprint), new DoubleDimension(160, 160),
        ELength.MM, null)) {

      b = g.getBounds();
      g.setColor(Color.white);
      g.setBackground(Color.white);
      g.fill(b);
      g.setColor(Color.black);

      w = b.getWidth();
      h = 0;
      for (i = 0; i < s; i++) {
        st = styles.get(i);
        f = st.getFont();
        q = st.appendDescription(ETextCase.IN_SENTENCE, mo, true);
        if (q) {
          mo.append(' ');
          mo.append('[');
        }
        st.appendDescription(ETextCase.IN_SENTENCE, mo, false);
        if (q) {
          mo.append(']');
        }
        x = mo.toString();
        mo.clear();
        hh = f.getLineMetrics(x, g.getFontRenderContext()).getHeight();
        h += hh * 1.5d;

        try (final StyleApplication fa = st.applyTo(g)) {
          g.drawString(x, 0.05 * w, h);
          h += (0.4d * hh);
        }
      }

    }
  }

}
