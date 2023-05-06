/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author alena
 */
public class PolynomialNode {
    
    private int _degree;
    private double _coefficient;
    private PolynomialNode _previous;
    private PolynomialNode _next;
    
    public void Degree(int degreeValue) {
        _degree = degreeValue;
    }
    
    public int Degree() {
        return _degree;
    }
    
    public void Coefficient(double coefficientValue) {
        _coefficient = coefficientValue;
    }
    
    public double Coefficient() {
        return _coefficient;
    }
    
    public void Previous(PolynomialNode previousValue) {
        _previous = previousValue;
    }
    
    public PolynomialNode Previous() {
        return _previous;
    }
    
    public void Next(PolynomialNode nextValue) {
        _next = nextValue;
    }
    
    public PolynomialNode Next() {
        return _next;
    }
    
    public PolynomialNode() {
        
    }
    
    public PolynomialNode(int degreeValue, double coefficientValue) {
        this._degree = degreeValue;
        this._coefficient = coefficientValue;
    }
    
    @Override
    public String toString() {
        StringBuilder expression = new StringBuilder();
        
        if (this._degree == 0)
            expression.insert(0, this._coefficient);
        else
            if (this._degree == 1)
                expression.insert(0, String.format("{0}x", this._coefficient));
        else
                expression.insert(0, String.format("%fx^%d", this._coefficient, this._degree));
        
        return expression.toString();
    }
    
    public double[] Parse(String termString) {
        double[] coefficients;
        int degree = 0;
        double coefficient = 0;
        if (!termString.isEmpty()) {
            try {
                Pattern termRegExp = Pattern.compile("^([+-]?\\d*\\.{0,1}\\d*)(?:(x)(?:\\^(\\d+))?)?$");
                Matcher matcher = termRegExp.matcher(termString);
                boolean isMatch = matcher.matches();

                if (isMatch) {
                    //Matcher matcher = termRegExp.matcher(termString);
                    
                    String coefMatch = matcher.group(1);
                    String xMatch = matcher.group(2);
                    String degreeMatch = matcher.group(3);

                    if (coefMatch.equals("+") || coefMatch.isEmpty())
                        coefficient = 1;
                    else if (coefMatch.equals("-"))
                        coefficient = -1;
                    else
                        coefficient = Double.parseDouble(coefMatch);


                    if (degreeMatch != null && !degreeMatch.isEmpty())
                        degree = Integer.parseInt(degreeMatch);
                    else if (xMatch != null && xMatch.equals("x"))
                        degree = 1;
                    else
                        degree = 0;
                }
            }
            catch (PatternSyntaxException ex) {
                System.out.println("This string could not compile: "+ex.getPattern());
                System.out.println(ex.getMessage());
            }

        }
        coefficients = new double[degree + 1];
        for (int i = 0; i < degree + 1; i++) {
            if (i != degree)
                coefficients[i] = 0;
        }
        coefficients[degree] = coefficient;
        return coefficients;
    }
}