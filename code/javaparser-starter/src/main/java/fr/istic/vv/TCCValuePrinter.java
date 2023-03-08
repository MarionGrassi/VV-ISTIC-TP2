package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import com.github.javaparser.utils.Pair;

import java.util.*;

public class TCCValuePrinter extends VoidVisitorWithDefaults<Void> {

    private int nbMethods;
    private MethodDeclaration currMethod;
    private Map<String, List<MethodDeclaration>> fieldUsedInMethods = new HashMap<>();

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        nbMethods = 0;
        currMethod = null;
        fieldUsedInMethods.clear();

        // Visit private Fields
        for(FieldDeclaration field : declaration.getFields()) {
            field.accept(this, arg);
        }

        // Visit Methods
        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
        }

        int denominator = nbMethods * (nbMethods - 1) / 2;
        if(denominator == 0) {
            System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous]") + " -- TCC Value = 0");
            return;
        }

        Set<Pair<MethodDeclaration, MethodDeclaration>> methodsPair = new HashSet<>();
        for(List<MethodDeclaration> listMethod : fieldUsedInMethods.values()) {
            for(int i = 0; i < listMethod.size(); i++) {
                for(int j = i + 1; j < listMethod.size(); j++) {
                    methodsPair.add(new Pair<>(listMethod.get(i), listMethod.get(j)));
                }
            }
        }

        int numerator = methodsPair.size();
        double tccValue = (double) numerator / denominator;
        System.out.print(declaration.getFullyQualifiedName().orElse("[Anonymous]") + " -- TCC Value = ");
        System.out.println(numerator + " / " + denominator + " = " + tccValue);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        currMethod = declaration;
        nbMethods++;
        declaration.getBody().ifPresent(body -> body.accept(this, arg));
    }

    @Override
    public void visit(FieldDeclaration declaration, Void arg) {
        for(VariableDeclarator variable : declaration.getVariables()) {
            fieldUsedInMethods.putIfAbsent(variable.getNameAsString(), new ArrayList<>());
        }
    }

    @Override
    public void visit(FieldAccessExpr fieldAccessExpr, Void arg) {
        if(fieldAccessExpr.getScope().toString().equals("this")) {
            List<MethodDeclaration> methods = fieldUsedInMethods.get(fieldAccessExpr.getNameAsString());
            if(methods != null) {
                methods.add(currMethod);
            }
        }
    }

    @Override
    public void visit(NameExpr nameExpr, Void arg) {
        if(fieldUsedInMethods.keySet().contains(nameExpr.getNameAsString())) {
            List<MethodDeclaration> methods = fieldUsedInMethods.get(nameExpr.getNameAsString());
            if(methods != null) {
                methods.add(currMethod);
            }
        }
    }

    @Override
    public void defaultAction(Node n, Void arg) {
        super.defaultAction(n, arg);
        n.getChildNodes().forEach(child -> child.accept(this, arg));
    }
}
