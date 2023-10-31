package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

//this is a comment
class Sphere extends BBoxedPrimitive {

  private final Point c;
  private final float r;

  //isse sphere k bounding box bannenge based on radius and centre
  public Sphere(final Point c, final float r) {
    super(BBox.create(c.sub(new Vec3(r, r, r)), c.add(new Vec3(r, r, r))));
    this.c = c;
    this.r = r;
  }

  @Override
  public Hit hitTest(
    final Ray myray,
    final Obj myobj,
    final float tmin,
    final float tmax
  ) {
    return new LazyHitTest(myobj) {
      private Point point = null;
      private float t;

      @Override
      public float getParameter() {
        return t;
      }

      @Override
      public Point getPoint() {
        if (point == null) point =
          myray.eval(t).add(myray.dir().scale(Constants.EPS));
        return point;
      }

      @Override
      protected boolean calculateHit() {
        Vec3 oc = myray.base().sub(c);
        float a = myray.dir().dot(myray.dir());
        float b = 2.0f * oc.dot(myray.dir());
        float c = oc.dot(oc) - r * r;
        float discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
          return false;
        } else {
          t = (-b - (float) Math.sqrt(discriminant)) / (2.0f * a);
          if (t < tmin || t > tmax) return false;
          return true;
        }
      }

      //   @Override
      //   public Vec2 getUV() {
      //     // yahan calculate kro appropriate and create kro approprite coordinates
      //     return new Vec2(0, 0);
      //   }
      @Override
      public Vec2 getUV() {
        Vec3 radial = this.getPoint().sub(c).normalized(); // radial vector from sphere's center
        return Util.computeSphereUV(radial);
      }

      @Override
      public Vec3 getNormal() {
        return getPoint().sub(c).normalized();
      }
    };
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((c == null) ? 0 : c.hashCode());
    result = prime * result + Float.floatToIntBits(r);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Sphere other = (Sphere) obj;
    if (c == null) {
      if (other.c != null) return false;
    } else if (!c.equals(other.c)) return false;
    if (Float.floatToIntBits(r) != Float.floatToIntBits(other.r)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Sphere [c=" + c + ", r=" + r + "]";
  }
}
