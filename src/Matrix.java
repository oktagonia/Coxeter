package neo;

public class Matrix {
    private double[][] data;
    private int width, height;

    public Matrix(int height, int width) {
        data = new double[height][width];
        this.width = width;
        this.height = height;
    }
    
    public Matrix(double[][] data) {
        this.height = data.length;
        this.width = data[0].length;
        this.data = data;
    }
    
    public Matrix transpose() {
        Matrix out = new Matrix(width, height);
        for (int i = 0; i < width; i++) {
            out.setRow(i, this.getColumn(i));
        }
        return out;
    }
    
    public Matrix plus(Matrix that) {
        checkDimensions(that);
        Matrix out = new Matrix(height, width);
        for (int i = 0; i < height; i++) {
            out.setRow(i, this.getRow(i).plus(that.getRow(i)));
        }
        return out;
    }
    
    public Matrix minus(Matrix that) {
        return this.plus(that.times(-1));
    }
    
    public Matrix times(double scalar) {
        Matrix out = new Matrix(height, width);
        for (int i = 0; i < height; i++) {
            out.setRow(i, this.getRow(i).times(scalar));
        }
        return out;
    }
    
    public Matrix times(Matrix that) {
        if (this.columnDimension() != that.rowDimension()) {
            throw new IllegalArgumentException
                ("Wrong dimensions for matrix multiplication");
        }
        
        Matrix out = new Matrix(this.columnDimension(), that.rowDimension());
        for (int i = 0; i < out.columnDimension(); i++) {
            for (int j = 0; j < out.rowDimension(); j++) {
                out.set(i, j, this.getRow(i).times(that.getColumn(j)));
            }
        }
        
        return out;
    }
    
    public double norm() {
        double out = 0.0;
        for (int i = 0; i < width; i++) {
            Vector column = this.getColumn(i);
            double tmp = 0.0;
            for (int j = 0; j < height; j++) {
                tmp += column.get(j);
            }
            out = Math.max(tmp, out);
        }
        return out;
    }
    
    public Matrix augment(Matrix that) {
        if (that.columnDimension() != height) {
            throw new IllegalArgumentException
                ("Can't augment matrices with different column dimensions");
        }
        
        Matrix out = new Matrix(height, width + that.rowDimension());
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                out.set(i, j, this.get(i, j));
            }
        }
        
        for (int i = 0; i < height; i++) {
            for (int j = width; j < out.rowDimension(); j++) {
                out.set(i, j, that.get(i, j - width));
            }
        }
        
        return out;
    }
    
    public static Matrix identity(int n) {
        Matrix out = new Matrix(n,n);
        for (int i = 0; i < n; i++) {
            out.set(i, i, 1);
        }
        return out;
    }
    
    public Matrix scale(int row, double scalar) {
        Matrix out = this.copy();
        out.setRow(row, out.getRow(row).times(scalar));
        return out;
    }
    
    public Matrix scaleAndAdd(int p, int q, double scalar) {
        Matrix out = this.copy();
        Vector r = out.getRow(p);
        Vector s = out.getRow(q);
        out.setRow(p, r.plus(s.times(scalar)));
        return out;
    }
    
    public Matrix reducedRowForm() {
        Matrix A = this;
        int N = A.columnDimension();
        
        for (int i = 0; i < N; i++) {
            A = A.scale(i, 1 / A.get(i, i));
            for (int j = 0; j < N; j++) {
                if (j != i) {
                    A = A.scaleAndAdd(j, i, -1*A.get(j, i));
                }
            }
        }
        
        return A;
    }
    
    public Matrix solve(Matrix that) {
        Matrix rref = this.augment(that).reducedRowForm();
        int N = this.columnDimension();
        int M = that.rowDimension();
        int O = this.rowDimension();
        
        Matrix solutions = new Matrix(N, M);
        
        for (int i = 0; i < N; i++) {
            for (int j = O; j < M + O; j++) {
                solutions.set(i, j - O, rref.get(i, j));
            }
        }
        
        return solutions;
    }
    
    public Matrix inverse() {
        return this.solve(identity(height));
    }
    
    public Vector getRow(int index) {
        return new Vector(data[index]);
    }
    
    public void setRow(int index, Vector row) {
        data[index] = row.getArray();
    }
    
    public Vector getColumn(int index) {
        Vector out = new Vector(height);
        for (int i = 0; i < height; i++) {
            out.set(i, this.get(i, index));
        }
        return out;
    }
    
    public int rowDimension() {
        return width;
    }
    
    public int columnDimension() {
        return height;
    }
    
    public double get(int i, int j) {
        return data[i][j];
    }
    
    public void set(int i, int j, double val) {
        data[i][j] = val;
    }
    
    public Matrix copy() {
        Matrix out = new Matrix(height, width);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                out.set(i, j, this.get(i, j));
            }
        }
        return out;
    }
    
    private void checkDimensions(Matrix that) {
        if ( (that.rowDimension() != this.height) ||
             (that.columnDimension() != this.width) ) {
            throw new IllegalArgumentException("Matrix dimensions don't match");
        }
    }
    
    public String toString() {
        String out = "";
        for (int i = 0; i < height; i++) {
            String row = this.getRow(i).toString();
            out += " " + row.substring(1, row.length()-1) + "\n";
        }
        return out;
    }
}
