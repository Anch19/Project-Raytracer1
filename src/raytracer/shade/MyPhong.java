// package raytracer.shade;

// import raytracer.core.Hit;
// import raytracer.core.LightSource;
// import raytracer.core.Shader;
// import raytracer.core.Trace;
// import raytracer.math.Color;
// import raytracer.math.Vec3;

// public class MyPhong implements Shader {

//   private final Shader Myinner;
//   private final Color ambient;
//   private final float diffuse;
//   private final float specular;
//   private final float shininess;

//   public MyPhong(
//     final Shader inner,
//     final Color ambientColor,
//     final float diffuseConstant,
//     final float specularConstant,
//     final float glossFactor
//   ) {
//     this.Myinner = inner;
//     this.ambient = ambientColor;
//     this.diffuse = diffuseConstant;
//     this.specular = specularConstant;
//     this.shininess = glossFactor;
//   }

//   @Override
//   public Color shade(final Hit hit, final Trace trace) {
//     // surface colour nikala hit position par
//     Color surfaceColor = Myinner.shade(hit, trace);
//     // ye ambient light hai
//     Color ambientColor = ambient.mul(surfaceColor);

//     Color diffuseColor = Color.BLACK;
//     Vec3 normal = hit.getNormal().normalized();
//     Vec3 viewDirection = trace.getRay().dir().neg().normalized();
//     for (LightSource lightSource : trace.getScene().getLightSources()) {
//       Vec3 lightDirection = lightSource
//         .getLocation()
//         .sub(hit.getPoint())
//         .normalized();
//       float intensity = Math.max(0, normal.dot(lightDirection));
//       diffuseColor =
//         diffuseColor.add(
//           lightSource.getColor().mul(surfaceColor).scale(intensity)
//         );
//     }
//     diffuseColor = diffuseColor.scale(diffuse);

//     Color specularColor = Color.BLACK;
//     for (LightSource lightSource : trace.getScene().getLightSources()) {
//       Vec3 lightDirection = lightSource
//         .getLocation()
//         .sub(hit.getPoint())
//         .normalized();
//       Vec3 reflectionDirection = lightDirection.neg().reflect(normal);

//       float intensity = (float) Math.pow(
//         Math.max(0, reflectionDirection.dot(viewDirection)),
//         shininess
//       );
//       specularColor =
//         specularColor.add(lightSource.getColor().scale(intensity));
//     }
//     specularColor = specularColor.scale(specular);

//     //final phong shading jo hui uska result
//     return ambientColor.add(diffuseColor).add(specularColor);
//   }
// }
package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Vec3;

public class MyPhong implements Shader {

  private final Shader Myinner;
  private final Color ambient;
  private final float diffuse;
  private final float specular;
  private final float shininess;

  public MyPhong(
    final Shader inner,
    final Color ambientColor,
    final float diffuseConstant,
    final float specularConstant,
    final float glossFactor
  ) {
    this.Myinner = inner;
    this.ambient = ambientColor;
    this.diffuse = diffuseConstant;
    this.specular = specularConstant;
    this.shininess = glossFactor;
  }

  @Override
  public Color shade(final Hit hit, final Trace trace) {
    Color surfaceColor = Myinner.shade(hit, trace);
    Color ambientColor = ambient.mul(surfaceColor);

    Color diffuseColor = Color.BLACK;
    Vec3 normal = hit.getNormal().normalized();
    Vec3 viewDirection = trace.getRay().dir().neg().normalized();
    for (LightSource lightSource : trace.getScene().getLightSources()) {
      Vec3 lightDirection = lightSource
        .getLocation()
        .sub(hit.getPoint())
        .normalized();
      float intensity = Math.max(0, normal.dot(lightDirection));
      diffuseColor =
        diffuseColor.add(
          lightSource.getColor().mul(surfaceColor).scale(intensity)
        );
    }
    diffuseColor = diffuseColor.scale(diffuse);

    Color specularColor = Color.BLACK;
    for (LightSource lightSource : trace.getScene().getLightSources()) {
      Vec3 lightDirection = lightSource
        .getLocation()
        .sub(hit.getPoint())
        .normalized();
      Vec3 reflectionDirection = lightDirection.reflect(normal);
      float intensity = (float) Math.pow(
        Math.max(0, reflectionDirection.dot(viewDirection)),
        shininess
      );
      // Use light source color in the specular term as well
      specularColor =
        specularColor.add(lightSource.getColor().scale(intensity));
    }
    specularColor = specularColor.scale(specular);

    return ambientColor.add(diffuseColor).add(specularColor);
  }
}
