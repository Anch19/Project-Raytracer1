package raytracer.core.def;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.geom.BBox;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec3;

/**
 * Represents a bounding volume hierarchy acceleration structure
 */
public class BVH extends BVHBase {

  private List<Obj> objects = new ArrayList<>();
  private BBox boundingBox = BBox.EMPTY;
  private BVH left, right;

  public BVH() {}

  @Override
  public BBox bbox() {
    return boundingBox;
  }

  /**
   * Adds an object to the acceleration structure
   *
   * @param prim
   *             The object to add
   */

  @Override
  public void add(final Obj prim) {
    objects.add(prim);
    boundingBox = BBox.surround(boundingBox, prim.bbox());
  }

  /**
   * Builds the actual bounding volume hierarchy
   */
  @Override
  public void buildBVH() {
    build(objects.toArray(new Obj[0]), 0, objects.size());
  }

  private void build(Obj[] objects, int start, int end) {
    if (end - start <= THRESHOLD) {
      this.objects = Arrays.asList(Arrays.copyOfRange(objects, start, end));
      return;
    }

    // calculate bounding box
    BBox box = objects[start].bbox();
    for (int i = start + 1; i < end; i++) {
      box = BBox.surround(box, objects[i].bbox());
    }
    this.boundingBox = box;

    Vec3 extent = box.getMax().sub(box.getMin());
    int splitDim = calculateSplitDimension(extent);
    float splitPos = calculateMaxOfMinPoints().get(splitDim);

    Comparator<Obj> comparator = (o1, o2) -> {
      float c1 = o1.bbox().getMin().get(splitDim);
      float c2 = o2.bbox().getMin().get(splitDim);
      return Float.compare(c1, c2);
    };
    Arrays.sort(objects, start, end, comparator);

    int mid = start;
    for (int i = start; i < end; i++) {
      if (objects[i].bbox().getMin().get(splitDim) < splitPos) {
        mid = i;
      } else {
        break;
      }
    }
    left = new BVH();
    right = new BVH();
    left.build(objects, start, mid);
    right.build(objects, mid, end);

    this.objects.clear();
  }

  @Override
  public Point calculateMaxOfMinPoints() {
    Point maxPoint = objects.get(0).bbox().getMin();
    for (Obj obj : objects) {
      Point minPoint = obj.bbox().getMin();
      maxPoint =
        new Point(
          Math.max(maxPoint.x(), minPoint.x()),
          Math.max(maxPoint.y(), minPoint.y()),
          Math.max(maxPoint.z(), minPoint.z())
        );
    }
    return maxPoint;
  }

  @Override
  public int calculateSplitDimension(final Vec3 extent) {
    if (extent.x() >= extent.y() && extent.x() >= extent.z()) {
      return 0;
    } else if (extent.y() >= extent.x() && extent.y() >= extent.z()) {
      return 1;
    } else {
      return 2;
    }
  }

  @Override
  public void distributeObjects(
    final BVHBase a,
    final BVHBase b,
    final int splitDim,
    final float splitPos
  ) {
    // TODO Implement this method
    throw new UnsupportedOperationException(
      "This method has not yet been implemented."
    );
  }

  @Override
  public Hit hit(
    final Ray ray,
    final Obj obj,
    final float tMin,
    final float tMax
  ) {
    // Check intersection with bounding box
    if (!intersect(ray, boundingBox, tMin, tMax)) {
      return Hit.No.get();
    }

    Hit hitLeft = (left != null)
      ? left.hit(ray, obj, tMin, tMax)
      : Hit.No.get();
    Hit hitRight = (right != null)
      ? right.hit(ray, obj, tMin, tMax)
      : Hit.No.get();

    if (hitLeft.hits() && hitRight.hits()) {
      return (hitLeft.getParameter() < hitRight.getParameter())
        ? hitLeft
        : hitRight;
    } else if (hitLeft.hits()) {
      return hitLeft;
    } else {
      return hitRight;
    }
  }

  /**
   * Checks intersection between a ray and a bounding box
   *
   * @param ray   The ray to intersect with the bounding box
   * @param box   The bounding box to check for intersection
   * @param tMin  The minimum distance along the ray
   * @param tMax  The maximum distance along the ray
   * @return True if the ray intersects with the bounding box, false otherwise
   */
  private boolean intersect(Ray ray, BBox box, float tMin, float tMax) {
    float t1 = (box.getMin().x() - ray.base().x()) * ray.invDir().x();
    float t2 = (box.getMax().x() - ray.base().x()) * ray.invDir().x();
    float t3 = (box.getMin().y() - ray.base().y()) * ray.invDir().y();
    float t4 = (box.getMax().y() - ray.base().y()) * ray.invDir().y();
    float t5 = (box.getMin().z() - ray.base().z()) * ray.invDir().z();
    float t6 = (box.getMax().z() - ray.base().z()) * ray.invDir().z();

    float tMinNew = Math.max(
      Math.max(Math.min(t1, t2), Math.min(t3, t4)),
      Math.min(t5, t6)
    );
    float tMaxNew = Math.min(
      Math.min(Math.max(t1, t2), Math.max(t3, t4)),
      Math.max(t5, t6)
    );

    return tMaxNew >= Math.max(tMinNew, tMin) && tMinNew <= tMax;
  }

  @Override
  public List<Obj> getObjects() {
    return objects;
  }
}
// THIS CODE HAS BEEN DONE WITH SOME HELP OF CHATGPT , ESPECIALLY LOCAL INTERSECT METHOD
