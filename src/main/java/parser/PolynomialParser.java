/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Алена
 */
public class PolynomialParser {
    private PolynomialNode head;

    //элементарное кольцо
    public PolynomialParser() {
        head = new PolynomialNode();
        head.Next(head);
        head.Previous(head);
    }

    //инициализируем строкой
    public PolynomialParser(String PolynomialParserString) {
        head = new PolynomialNode();
        head.Next(head);
        head.Previous(head);
        Parse(PolynomialParserString);
    }

    public void PrintPolynomialParser() {
        PolynomialNode term = head.Next();
        while (term != head)
        {
            System.out.printf("%f %d\n", term.Coefficient(), term.Degree());
            term = term.Next();
        }
    }

    //создание мночлена из строки
    public void Parse(String PolynomialParserString) {
        head.Next(head);
        head.Previous(head);

        Pattern polynomialRegExp = Pattern.compile("^[+-]?\\d*\\.{0,1}\\d*(?:x(?:\\^\\d+)?)?(?:[+-]\\d*\\.{0,1}\\d*(?:x(?:\\^\\d+)?)?)*$");
        Pattern termRegExp = Pattern.compile("[+-]?\\d*\\.{0,1}\\d*(?:x(?:\\^\\d+)?)?");
        Matcher matcher = polynomialRegExp.matcher(PolynomialParserString);
        boolean isMatch = matcher.matches();

        if (isMatch) {
            Matcher m = termRegExp.matcher(PolynomialParserString);
            while(m.find()){
                Insert(m.group());
            }
        }
//            else
//                throw new FormatException();
    }

    //вставка члена
    public void Insert(int degree, double coefficient) {
        if (coefficient != 0) {
            PolynomialNode term = head.Next();
            while ((term != head) && (term.Degree() < degree))
                term = term.Next(); //ищем место вставки

            if (degree != term.Degree() || term == head) { //вставка слева от найденного
                PolynomialNode newTerm = new PolynomialNode(degree, coefficient);
                newTerm.Next(term);
                newTerm.Previous(term.Previous());
                term.Previous().Next(newTerm);
                term.Previous(newTerm);
            }
            else { //если степень уже существует - складываем коэфициенты
                term.Coefficient(term.Coefficient() + coefficient);
                if (term.Coefficient() == 0) //поглощение членов
                    Delete(term.Degree());
            }
        }
    }

    //вставка члена из строки
    public void Insert(String termString) {
        int number = 0;
        double[] coefficients;
        PolynomialNode nd = new PolynomialNode();
        coefficients = nd.Parse(termString);
        for (int i = 0; i < coefficients.length; i++) {
            if (coefficients[i] != 0.0) {
                number = i;
                break;
            }
        }
        if (coefficients[number] != 0)
            Insert(number, coefficients[number]);
    }


    //удаление члена заданной степени
    public void Delete(int degree) {
        PolynomialNode term = head.Next();
        while ((term != head) && (term.Degree() != degree))
            term = term.Next();

        if (term != head) {
            term.Previous().Next(term.Next());
            term.Next().Previous(term.Previous());
        }
//            else
//                throw new IndexOutOfRangeException(); //если не нашли
    }


    //возвращает строковое представление многочлена
    @Override
    public String toString() {
        StringBuilder Expr = new StringBuilder();
        PolynomialNode term = head.Next();
        while (term != head) {
            if ((Expr.length() != 0) && (Expr.charAt(0) != '-'))
                Expr.insert(0, '+'); //знак плюса перед членом
            Expr.insert(0, term.toString());

            term = term.Next();
        }

        return Expr.toString();
    }

    public double[] toDoubleArray() {
        PolynomialNode term = head.Next();
        int maxDegree = term.Degree();
        while (term != head) {
            if (term.Degree() > maxDegree)
                maxDegree = term.Degree();
            term = term.Next();
        }
        double[] polynomialArray = new double[maxDegree + 1];
        term = head.Next();
        while (term != head) {
            polynomialArray[term.Degree()] = term.Coefficient();
            term = term.Next();
        }
        return polynomialArray;
    }

    //сложение
    public PolynomialParser Addition(PolynomialParser polinom) {
        PolynomialParser result = new PolynomialParser(polinom.toString());

        PolynomialNode term = head.Next();
        while (term != head) {
            result.Insert(term.Degree(), term.Coefficient());
            term = term.Next();
        }

        return result;
    }
}