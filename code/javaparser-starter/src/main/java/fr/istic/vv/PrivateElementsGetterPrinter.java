package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

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
