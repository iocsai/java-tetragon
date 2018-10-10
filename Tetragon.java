/**
 * Challenge #4 
 * Quadrilateral
 * 
 * Let's do some geometry this time. 
 * The challenge is to write a code which takes 4 pairs of XY coordinates 
 * and tell if the quad made by joining them is a square, rect, 
 * parallelogram, rhombus, trapezium, kite or none of them. 
 * You can use any algorithm. 
 * Test cases: 
 * (-1,0),(1,2),(2,1),(0,-1)->Rect 
 * (-1,0),(0,2),(1,0),(0,-2)->Rhombus 
 * (0,1),(1,2),(2,1),(1,0)->Square 
 * (0,0),(0,2),(3,3),(2,0)->Deltoid 
 */
package tetragon;

import java.util.*;

public class Tetragon {
    
    public enum Quad {
        Trapezium, Paralelogram, Rhombus, Deltoid, Rectangle, Square, Regular,
        Convex, Concave;

        @Override
        public String toString() {
            return name();
        }
    }
    
    private final Coordinate peakA;
    private final Coordinate peakB;
    private final Coordinate peakC;
    private final Coordinate peakD;
    private final double sideA;
    private final double sideB;
    private final double sideC;
    private final double sideD;
    private final double alpha;
    private final double beta;
    private final double gamma;
    private final double delta;
    private Quad quadType;

    public static void main(String[] args) {
        Tetragon rect = Tetragon.create(new Coordinate(-1, 0), 
                new Coordinate(1, 2), 
                new Coordinate(2, 1), 
                new Coordinate(0, -1));
        Tetragon rhombus = Tetragon.create(new Coordinate(-1, 0), 
                new Coordinate(0, 2), 
                new Coordinate(1, 0), 
                new Coordinate(0, -2));
        Tetragon square = Tetragon.create(new Coordinate(0, 1), 
                new Coordinate(1, 2), 
                new Coordinate(2, 1), 
                new Coordinate(1, 0));
        Tetragon kite = Tetragon.create(new Coordinate(0, 0), 
                new Coordinate(0, 2), 
                new Coordinate(3, 3), 
                new Coordinate(2, 0));
        Tetragon probe = Tetragon.create(new Coordinate(10, 0), 
                new Coordinate(15, 15), 
                new Coordinate(25, 25), 
                new Coordinate(5, 10));
        System.out.printf("%s%n%s%n%s%n%s%n%s%n", rect.solve(), rhombus.solve(),
                square.solve(), kite.solve(), probe.solve());
    }

    private Tetragon(Coordinate peakA, Coordinate peakB, Coordinate peakC, Coordinate peakD) {
        this.peakA = peakA;
        this.peakB = peakB;
        this.peakC = peakC;
        this.peakD = peakD;
        this.sideA = this.peakA.distance(this.peakB);
        this.sideB = this.peakB.distance(this.peakC);
        this.sideC = this.peakC.distance(this.peakD);
        this.sideD = this.peakD.distance(this.peakA);
        this.alpha = this.peakA.angle(peakB, peakC);
        this.beta = this.peakB.angle(peakA, peakC);
        this.gamma = this.peakC.angle(peakB, peakD);
        this.delta = this.peakD.angle(peakA, peakC);
        this.quadType = this.setType();
    }

    public static Tetragon create(Coordinate peakA, 
            Coordinate peakB, Coordinate peakC, Coordinate peakD) {
        Line ab = new Line(peakA, peakB);
        Line cd = new Line(peakC, peakD);
        Coordinate inter = ab.intersection(cd);
        boolean maxXab = inter.coordinateX <=Math.max(peakA.coordinateX, peakB.coordinateX);
        boolean minXab = inter.coordinateX >= Math.min(peakA.coordinateX, peakB.coordinateX);
        boolean maxXcd = inter.coordinateX <=Math.max(peakC.coordinateX, peakD.coordinateX);
        boolean minXcd = inter.coordinateX >=Math.min(peakC.coordinateX, peakD.coordinateX);
        if (maxXab && maxXcd && minXab && minXcd) {
            return new Tetragon(peakA, peakC, peakB, peakD);
        }
        if ((maxXab && minXab) || (maxXcd && minXcd)) {
            Tetragon result = new Tetragon(peakA, peakB, peakC, peakD);
            result.quadType = Quad.Concave;
            return result;
        }
        return new Tetragon(peakA, peakB, peakC, peakD);
    }
    
    private String solve() {
        return this + " is a " + this.quadType;
    }
    
    private Quad setType() {
        if (this.quadType == Quad.Concave) {
            if (isDeltoid()) {
                return Quad.Deltoid;
            } else return this.quadType;
        }
        if (isSquare()) {
            return Quad.Square;
        } else if (isRhombus()) {
            return Quad.Rhombus;
        } else if (isDeltoid()) {
            return Quad.Deltoid;
        } else if (isRectangle()) {
            return Quad.Rectangle;
        } else if (isParalelogram()) {
            return Quad.Paralelogram;
        } else if (isTrapezium()) {
            return Quad.Trapezium;
        }
        return Quad.Regular;
    }
    
   public boolean isTrapezium() {
        return Math.abs(peakA.slope(peakB)) == Math.abs(peakC.slope(peakD)) ||
                Math.abs(peakC.slope(peakA)) == Math.abs(peakD.slope(peakB));
    }
    
    public boolean isRhombus() {
        return (this.sideA == this.sideB) && (this.sideA == this.sideC) &&
                (this.sideA == this.sideD);
    }
    
    public boolean isDeltoid() {
        return (this.sideA == this.sideB) && (this.sideC == this.sideD) ||
                (this.sideA == this.sideD) && (this.sideB == this.sideC);
    }
    
    public boolean isSquare() {
        return isRhombus() && isRectangle();
    }
    
    public boolean isRectangle() {
        int count90 = 0;
        double[] angles = {this.alpha, this.beta, this.gamma, this.delta};
        for (double angle : angles) {
            if (Math.toDegrees(angle) == 90) {
                count90++;
            }
        }
        return isParalelogram() && count90 > 2;
    }
    
    public boolean isParalelogram() {
        return Math.abs(peakA.slope(peakB)) == Math.abs(peakC.slope(peakD)) &&
                Math.abs(peakD.slope(peakA)) == Math.abs(peakC.slope(peakB));
    }

    public Map<String, Boolean> toMap() {
        Map<String, Boolean> result = new HashMap();
        result.put("Trapezium", isTrapezium() ? Boolean.TRUE : Boolean.FALSE);
        result.put("Rhombus", isRhombus() ? Boolean.TRUE : Boolean.FALSE);
        result.put("Kite", isDeltoid() ? Boolean.TRUE : Boolean.FALSE);
        result.put("Paralelogram", isParalelogram() ? Boolean.TRUE : Boolean.FALSE);
        result.put("Rectangle", isRectangle() ? Boolean.TRUE : Boolean.FALSE);
        result.put("Square", isSquare() ? Boolean.TRUE : Boolean.FALSE);
        return result;
    }

    @Override
    public String toString() {
        return "Quadrilateral A" + peakA + ", B" + peakB + ", C" + peakC + ", D" + peakD;
    }

    public static class Coordinate {
        
        private final double coordinateX;
        private final double coordinateY;

        public Coordinate(double x, double y) {
            this.coordinateX = x;
            this.coordinateY = y;
        }
        
        public double distance(Coordinate other) {
            return Math.sqrt(Math.pow(this.coordinateX - other.coordinateX, 2) 
                    + Math.pow(this.coordinateY - other.coordinateY, 2));
        }
        
        public double angle(Coordinate beta, Coordinate gamma) {
            double slopeA = this.slope(beta);
            double slopeB = this.slope(gamma);
            return Math.abs(Math.atan((slopeB - slopeA) / (1 + slopeA *slopeB)));
        }
        
        public double slope(Coordinate other) {
            if (other.coordinateX == this.coordinateX) {
                return Double.POSITIVE_INFINITY;
            }
            return (other.coordinateY - this.coordinateY) / 
                    (other.coordinateX - this.coordinateX);
        }
        
        public double intercept(Coordinate other) {
            return other.coordinateY - (this.slope(other) * other.coordinateX);
        }

        @Override
        public String toString() {
            Locale loc = Locale.ENGLISH;
            return String.format(loc, "(%.1f, %.1f)", coordinateX, coordinateY);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Coordinate other = (Coordinate) obj;
            return (Double.doubleToLongBits(this.coordinateX) == 
                    Double.doubleToLongBits(other.coordinateX)) &&
                    (Double.doubleToLongBits(this.coordinateY) == 
                    Double.doubleToLongBits(other.coordinateY));
        }
    }
    
    public static class Line {
        
        private final double slope;
        private final double intercept;

        public Line(double slope, double intercept) {
            this.slope = slope;
            this.intercept = intercept;
        }
        
        public Line(Coordinate pointA, Coordinate pointB) {
            this.slope = pointA.slope(pointB);
            this.intercept = pointA.intercept(pointB);
        }
        
        public double[] getLine() {
            return new double[] {this.slope, this.intercept};
        }
        
        public Coordinate intersection(Line other) {
            double x = (other.intercept - this.intercept) /
                    (this.slope - other.slope);
            double y = (this.slope * x) + this.intercept;
            return new Coordinate(x, y);
        }

        @Override
        public String toString() {
            Locale loc = Locale.ENGLISH;
            String sign = intercept >= 0 ? "+" : "-";
            return String.format(loc, "y = %.4fx %s %.4f", slope, sign, 
                    Math.abs(intercept));
        }
    }
}