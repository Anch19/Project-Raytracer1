package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.*;

public class Plane extends BBoxedPrimitive {

  private final Point mypoint;
  private final Vec3 newvector;

  public Plane(final Point a, final Point b, final Point c) {
    this.mypoint = a;
    Vec3 Mya = b.sub(a);
    Vec3 Myb = c.sub(a);
    this.newvector = Mya.cross(Myb).normalized();
  }

  public Plane(final Point a, final Vec3 u) {
    this.mypoint = a;
    this.newvector = u.normalized();
  }

  @Override
  public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {
    return new LazyHitTest(obj) {
      private Point point = null;
      private float t;

      @Override
      protected boolean calculateHit() {
        final Vec3 dir = ray.dir().normalized();
        final float det = dir.dot(newvector);

        if (Constants.isZero(det)) {
          return false;
        }

        t = mypoint.sub(ray.base()).dot(newvector) / det;
        return !(t < tmin) && !(t > tmax);
      }

      @Override
      public float getParameter() {
        return t;
      }

      @Override
      public Point getPoint() {
        if (point == null) point = ray.eval(t).add(newvector.scale(0.0001f));
        return point;
      }

      @Override
      public Vec3 getNormal() {
        return newvector;
      }

      @Override
      public Vec2 getUV() {
        return Util.computePlaneUV(newvector, mypoint, getPoint());
      }
    };
  }

  @Override
  public int hashCode() {
    return mypoint.hashCode() ^ newvector.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof Plane) {
      Plane p = (Plane) other;
      return mypoint.equals(p.mypoint) && newvector.equals(p.newvector);
    }
    return false;
  }
}
//i was having some problem in the constructor , so THAT HAS BEEN DONE WITH THE HELP OF CHATGPT
