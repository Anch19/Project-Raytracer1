package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

class Plane extends BBoxedPrimitive {

  private final Point m;
  private final Vec3 n;

  public Plane(final Point m, final Vec3 n) {
    super(BBox.create(m, m)); //yahan par bpunding box banayenge but the thing is k ye infinite honge to to tutor se confirm krna hai case
    this.m = m;
    this.n = n;
  }

  @Override
  public Hit hitTest(
    final Ray ray,
    final Obj obj,
    final float tmin,
    final float tmax
  ) {
    return new LazyHitTest(obj) {
      private Point point = null;
      private float t;

      @Override
      public float getParameter() {
        return t;
      }

      @Override
      public Point getPoint() {
        if (point == null) point = ray.eval(t).add(n.scale(Constants.EPS));
        return point;
      }

      @Override
      protected boolean calculateHit() {
        final Vec3 dir = ray.dir();
        final float denom = dir.dot(n);

        if (Constants.isZero(denom)) return false;

        t = (m.sub(ray.base()).dot(n)) / denom;

        if (t < tmin || t > tmax) return false;

        return t >= Constants.EPS;
      }

      @Override
      public Vec2 getUV() {
        // yahan calculate kro appropriate and create kro approprite coordinates
        return new Vec2(0, 0);
      }

      @Override
      public Vec3 getNormal() {
        return n;
      }
    };
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((m == null) ? 0 : m.hashCode());
    result = prime * result + ((n == null) ? 0 : n.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Plane other = (Plane) obj;
    if (m == null) {
      if (other.m != null) return false;
    } else if (!m.equals(other.m)) return false;
    if (n == null) {
      if (other.n != null) return false;
    } else if (!n.equals(other.n)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Plane [m=" + m + ", n=" + n + "]";
  }
}
