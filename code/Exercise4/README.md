# Code of your exercise

Put here all the code created for this exercise

## Classe qui imprime pour chaque classe les attributs privés qui n'ont pas de getter
```java
package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.sql.SQLOutput;

public class PrivateElementsGetterPrinter extends VoidVisitorWithDefaults<Void> {

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        for(TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        if(!declaration.isPublic()) return;
        for(FieldDeclaration field: declaration.getFields()) {
            if(field.isPrivate()) {
                for (VariableDeclarator variable : field.getVariables()) {
                    String variableName = variable.getNameAsString();
                    String getterName = "get" + variableName.substring(0, 1).toUpperCase() + variableName.substring(1);
                    if(declaration.getMethodsByName(getterName).isEmpty()) {
                        System.out.println(declaration.getFullyQualifiedName().orElse("[Anonymous") + " -- field : " + variableName);
                    }
                }
            }
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }
}
```

## Classe ajoutée pour tester

```java
package fr.istic.vv;

public class Point {
    private double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double dot(Point p) {
        return x * p.x + y * p.y;
    }

    public Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }
}
```

## Resultat obtenu

lancé sur l'ensemble des source

```text
fr.istic.vv.Point -- field : y

Process finished with exit code 0
```