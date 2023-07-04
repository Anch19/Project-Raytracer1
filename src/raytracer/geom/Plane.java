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
  private final float d;

  public Plane(final Point m, final Vec3 n) {
    super(BBox.create(m, m)); // Adjust as per the tutor's instruction for infinite planes
    this.m = m;
    this.n = n.normalized(); // Normalize the normal vector here
    this.d = m.dot(this.n); // Calculate the distance from the plane to the origin
  }

  public Plane(final Point a, final Point b, final Point c) {
    super(BBox.INF);
    this.m = a; // You can choose any point as the reference point 'm'
    this.n = b.sub(a).cross(c.sub(a)).normalized();
    this.d = m.dot(this.n);
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
        Vec3 dir = ray.dir();
        float numerator = d - ray.base().dot(n);
        float denominator = dir.dot(n);

        if (denominator == 0) {
          // Ray and plane are parallel, no intersection
          return false;
        }

        t = numerator / denominator;

        if (t < tmin || t > tmax) {
          // Intersection point is outside the allowed hit distance range
          return false;
        }

        return true;
      }

      @Override
      public Vec2 getUV() {
        return Util.computePlaneUV(n, m, getPoint());
      }

      @Override
      public Vec3 getNormal() {
        return n; // As it's already normalized in the constructor, directly return it here
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
