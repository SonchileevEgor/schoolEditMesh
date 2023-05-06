package latex;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import static config.Config.MATH_FONT_SIZE;
import net.sourceforge.jeuclid.MathMLParserSupport;
import net.sourceforge.jeuclid.MutableLayoutContext;
import net.sourceforge.jeuclid.context.LayoutContextImpl;
import net.sourceforge.jeuclid.context.Parameter;
import net.sourceforge.jeuclid.converter.Converter;
import org.w3c.dom.Document;
import uk.ac.ed.ph.snuggletex.*;
//import org.scilab.forge.jlatexmath.TeXConstants;
//import org.scilab.forge.jlatexmath.TeXFormula;

import java.io.*;
import java.text.MessageFormat;
import java.util.HashMap;
import javax.swing.ImageIcon;
import opengl.Render;

/**
 * Function creates pictures and opengl textures.
 * <br>Сгенерированные текстуры он кеширует в себе автоматически.
 */
public class ImageGenerator {
  private static HashMap<String, Texture> cashedTextures = new HashMap<>();
  private final static int maxNumCashedTextures = 300;
//  private final static float defaultTextSize = 25f;

  /**
   * Creates an image with a specific text.
   * <br>SIC: Use file name with the extension "png"
   *
   * @param text Text on image (Latex)
   * @param fileName File name of result image
   * @param size Size of text in generated image.
   * @throws java.io.IOException
   */
  static public void generateImageWithTextToFile(String text, float size, String fileName) throws IOException {
    try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
      fileOutputStream.write(generateImageWithText(text, size));
    }
  }

  /**
   * Creates an image with a specific text.
   *
   * @param text Text on image (Latex)
   * @param size Size of text in generated image.
   * @return Image as byteArray in "png" format.
   */
  static public byte[] generateImageWithText(String text, float size) {
    /* Create vanilla SnuggleEngine and new SnuggleSession */
    SnuggleEngine engine = new SnuggleEngine();
    SnuggleSession session = engine.createSession();

    // Replace russian symbols to big integers because snuggletex library don't support russian symbols
    String repText = SymbolReplacer.replaceSymbols(text, false);

    /* Parse some LaTeX input */
    SnuggleInput input = new SnuggleInput("$$ " + fixText(repText) + " $$");

    try {
      session.parseInput(input);
    } catch (IOException ex) {
      System.out.println("Warning: can't parse latex formula");
    }

    /* Specify how we want the resulting XML */
    XMLStringOutputOptions options = new XMLStringOutputOptions();
    options.setSerializationMethod(SerializationMethod.XHTML);
    options.setIndenting(true);
    options.setEncoding("UTF-8");
    options.setAddingMathSourceAnnotations(true);
//    options.setUsingNamedEntities(true); /* (Only used if caller has an XSLT 2.0 processor) */

    /* Convert the results to an XML String, which in this case will
     * be a single MathML <math>...</math> element. */
    String res = session.buildXMLString(options);

    // Restore russian symbols form big integers.
    res = SymbolReplacer.replaceSymbols(res, true);

    ByteArrayOutputStream resultImage = new ByteArrayOutputStream();
    try {
      final Document doc = MathMLParserSupport.parseString(res);
      final MutableLayoutContext params = new LayoutContextImpl(
          LayoutContextImpl.getDefaultLayoutContext());
      params.setParameter(Parameter.MATHSIZE, size);
      params.setParameter(Parameter.ANTIALIAS, true);
      Converter.getInstance().convert(doc, resultImage, "image/png", params);
    } catch (Exception ex) {
      System.out.println("Warning: Can't create png with latex formula");
      System.out.println(ex.getMessage());
    }
    return resultImage.toByteArray();
  }

  static public ImageIcon createImage(String text, float size) {
    return new ImageIcon(generateImageWithText(text, size));
  }
  
  /**
   * Clear pool of cached textures.
   */
  static public void clearCache(){
    cashedTextures.clear();
  }

  /**
   * Escape spaces for latex formulas.
   *
   * @param str Text for formatting.
   * @return Text with escape spaces.
   */
  static public String escapeSpaces(String str) {
    return str.replaceAll(" ", "\\\\ ");
  }

  /**
   * Corrects string for proper display it.
   *
   * @param str String for fixing.
   * @return Fixed string.
   */
  static public String fixText(String str) {
    String resStr = new String(str);
    // По какой-то причине в jlatexmath, если надпись оканчивается на 'F', то она получается обрезанной.
    // Добавление пробела в конце такой строки позволяет избежать данного эффекта.
    if (resStr.charAt(resStr.length() - 1) == 'F') {
      resStr = resStr + " ";
    }
    // Экранируем пробелы в строке, иначе они не засчитываются в jlatexmath.
    resStr = escapeSpaces(resStr);
    return resStr;
  }

  /**
   * Creates a texture containing the specified text (a logical mathematical formula in latex notation).
   *
   * @param ren  Context of opengl.
   * @param text Text on texture (a logical mathematical formula in latex notation).
   * @param size Size of text in generated texture.
   * @return Texture with given text.
   * @throws Exception if can't create texture.
   */
  static public Texture getTextureWithLatexText(Render ren, String text, float size) throws Exception {
    if (cashedTextures.containsKey(text)) {
      return cashedTextures.get(text);
    } else {
      InputStream imageInputStream = new ByteArrayInputStream(generateImageWithText(text, size));
      Texture texture;
      try {
        TextureData data = TextureIO.newTextureData(ren.getDrawable().getGLProfile(), imageInputStream, false, "png");
        texture = TextureIO.newTexture(data);
      } catch (IOException exc) {
        System.out.println(MessageFormat.format("Warning: can't load texture with text {0} in function {1}", text,
            "drawLatexText"));
        throw new Exception("Can't create texture with text " + text);
      }

      // Silly protection from memory overflow
      if (cashedTextures.size() > maxNumCashedTextures)
        cashedTextures.clear();

      cashedTextures.put(text, texture);
      return texture;
    }

  }

  /**
   * The same {@link #getTextureWithLatexText(Render, String, float)} with default font size
   */
  static public Texture getTextureWithLatexText(Render ren, String text) throws Exception {
    return getTextureWithLatexText(ren, text, MATH_FONT_SIZE.value());
  }

  public static void main(String[] args) {
    try {
      ImageGenerator.generateImageWithTextToFile("\\alpha \\beta яя^аа", MATH_FONT_SIZE.value(), "test_image.png");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
