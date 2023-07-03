package raytracer.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import raytracer.core.def.Accelerator;
import raytracer.core.def.StandardObj;
import raytracer.geom.GeomFactory;
import raytracer.geom.Primitive;
import raytracer.math.Point;
import raytracer.math.Vec3;

/**
 * Represents a model file reader for the OBJ format
 */
public class OBJReader {

  /**
   * Reads an OBJ file and uses the given shader for all triangles. While
   * loading the triangles they are inserted into the given acceleration
   * structure accelerator.
   *
   * @param filename
   *            The file to read the data from
   * @param accelerator
   *            The target acceleration structure
   * @param shader
   *            The shader which is used by all triangles
   * @param scale
   *            The scale factor which is responsible for scaling the model
   * @param translate
   *            A vector representing the translation coordinate with which
   *            all coordinates have to be translated
   * @throws IllegalArgumentException
   *             If the filename is null or the empty string, the accelerator
   *             is null, the shader is null, the translate vector is null,
   *             the translate vector is not finite or scale does not
   *             represent a legal (finite) floating point number
   */
  public static void read(
    final String filename,
    final Accelerator accelerator,
    final Shader shader,
    final float scale,
    final Vec3 translate
  ) throws FileNotFoundException {
    read(
      new BufferedInputStream(new FileInputStream(filename)),
      accelerator,
      shader,
      scale,
      translate
    );
  }

  /**
   * Reads an OBJ file and uses the given shader for all triangles. While
   * loading the triangles they are inserted into the given acceleration
   * structure accelerator.
   *
   * @param in
   *            The InputStream of the data to be read.
   * @param accelerator
   *            The target acceleration structure
   * @param shader
   *            The shader which is used by all triangles
   * @param scale
   *            The scale factor which is responsible for scaling the model
   * @param translate
   *            A vector representing the translation coordinate with which
   *            all coordinates have to be translated
   * @throws IllegalArgumentException
   *             If the InputStream is null, the accelerator
   *             is null, the shader is null, the translate vector is null,
   *             the translate vector is not finite or scale does not
   *             represent a legal (finite) floating point number
   */
  public static void read(
    final InputStream in,
    final Accelerator accelerator,
    final Shader shader,
    final float scale,
    final Vec3 translate
  ) throws FileNotFoundException {
    if (
      in == null || accelerator == null || shader == null || translate == null
    ) {
      throw new IllegalArgumentException(
        "charo arguments mai se koi to null hai"
      );
    }

    if (!Float.isFinite(scale) || !translate.isFinite()) {
      throw new IllegalArgumentException(
        "scale ya translate vector mai se koi to finite nahi hai"
      );
    }
    // yahan or humne ek vertuces kind if vbana li hai aone traingle ki
    // aur usko scan kraya hai
    ArrayList<Point> apex = new ArrayList<>();
    Scanner myscanner = new Scanner(in);
    myscanner.useLocale(Locale.ENGLISH);

    // is loop se scanner se jo ear ek line aarahi hai usko lo ,a ur appropriate operation perform karo uspr
    while (myscanner.hasNextLine()) {
      String line = myscanner.nextLine();
      if (line.startsWith("v ")) {
        String[] coords = line.split(" ");
        float x = Float.parseFloat(coords[1]) * scale + translate.x();
        float y = Float.parseFloat(coords[2]) * scale + translate.y();
        float z = Float.parseFloat(coords[3]) * scale + translate.z();
        apex.add(new Point(x, y, z));
      } else if (line.startsWith("f ")) {
        String[] indices = line.split(" ");
        Point a = apex.get(Integer.parseInt(indices[1]) - 1);
        Point b = apex.get(Integer.parseInt(indices[2]) - 1);
        Point c = apex.get(Integer.parseInt(indices[3]) - 1);
        Primitive mytriangle = GeomFactory.createTriangle(a, b, c);
        Obj obj = new StandardObj(mytriangle, shader);
        accelerator.add(obj);
      }
    }

    myscanner.close();
  }
}
