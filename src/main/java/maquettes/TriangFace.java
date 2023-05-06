package maquettes;

/**
 * @author Leonid Ivanovsky
 */

/**
 * class, which realize the model of a storage's order of numbers of generating cover's points.
 * The face definition determines an triangle of generating cover by numbers of generating cover's points.
 * It needs for a comfortable work (output in special ply-file) with generating data.
 */

public class TriangFace {

    private int _firstPoint;
    private int _secondPoint;
    private int _thirdPoint;

    public TriangFace(int fir, int sec, int thr) {
        _firstPoint = fir;
        _secondPoint = sec;
        _thirdPoint = thr;
    }

    public int getFirstPoint() {
        return _firstPoint;
    }

    public int getSecondPoint() {
        return _secondPoint;
    }

    public int getThirdPoint() {
        return _thirdPoint;
    }

}
