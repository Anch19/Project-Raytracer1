package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Vec2;

public class MyCheckerboard implements Shader {

  private final Shader shader1;
  private final Shader shader2;
  private final float s;

  public MyCheckerboard(Shader shader1, Shader shader2, float s) {
    this.shader1 = shader1;
    this.shader2 = shader2;
    this.s = s;
  }

  @Override
  public Color shade(Hit hit, Trace trace) {
    // Get the texture coordinates from the hit
    Vec2 uv = hit.getUV();
    System.out.println("UV: " + uv); // For debugging

    int x = (int) ((Math.floor(uv.x() / s)) + (Math.floor(uv.y() / s)));

    // Determine which shader to use based on the checkerboard pattern
    if (x % 2 == 0) {
      return shader1.shade(hit, trace);
    } else {
      return shader2.shade(hit, trace);
    }
  }
}
