/**
 * @(#)Tree.java	1.30 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package decaf.tree;

import java.util.ArrayList;
import java.util.List;

import decaf.*;
import decaf.tac.Temp;
import decaf.type.*;
import decaf.scope.*;
import decaf.symbol.*;
import decaf.symbol.Class;
import decaf.utils.IndentPrintWriter;
import decaf.utils.MiscUtils;


/**
 * Root class for abstract syntax tree nodes. It provides
 *  definitions for specific tree nodes as subclasses nested inside
 *  There are 40 such subclasses.
 *
 *  Each subclass is highly standardized.  It generally contains only tree
 *  fields for the syntactic subcomponents of the node.  Some classes that
 *  represent identifier uses or definitions also define a
 *  Symbol field that denotes the represented identifier.  Classes
 *  for non-local jumps also carry the jump target as a field.  The root
 *  class Tree itself defines fields for the tree's type and
 *  position.  No other fields are kept in a tree node; instead parameters
 *  are passed to methods accessing the node.
 *
 *  The only method defined in subclasses is `visit' which applies a
 *  given visitor to the tree. The actual tree processing is done by
 *  visitor classes in other packages. The abstract class
 *  Visitor, as well as an Factory interface for trees, are
 *  defined as inner classes in Tree.
 *  @see TreeMaker
 *  @see TreeInfo
 *  @see TreeTranslator
 *  @see Pretty
 */
public abstract class Tree {

    /**
     * Toplevel nodes, of type TopLevel, representing entire source files.
     */
    public static final int TOPLEVEL = 1;

    /**
     * Import clauses, of type Import.
     */
    public static final int IMPORT = TOPLEVEL + 1;

    /**
     * Class definitions, of type ClassDef.
     */
    public static final int CLASSDEF = IMPORT + 1;

    /**
     * Method definitions, of type MethodDef.
     */
    public static final int METHODDEF = CLASSDEF + 1;

    /**
     * Variable definitions, of type VarDef.
     */
    public static final int VARDEF = METHODDEF + 1;

    /**
     * The no-op statement ";", of type Skip
     */
    public static final int SKIP = VARDEF + 1;

    /**
     * Blocks, of type Block.
     */
    public static final int BLOCK = SKIP + 1;

    /**
     * Do-while loops, of type DoLoop.
     */
    public static final int DOLOOP = BLOCK + 1;

    /**
     * While-loops, of type WhileLoop.
     */
    public static final int WHILELOOP = DOLOOP + 1;

    /**
     * For-loops, of type ForLoop.
     */
    public static final int FORLOOP = WHILELOOP + 1;

    /**
     * Labelled statements, of type Labelled.
     */
    public static final int LABELLED = FORLOOP + 1;

    /**
     * Synchronized statements, of type Synchonized.
     */
    public static final int SYNCHRONIZED = LABELLED + 1;

    /**
     * Object copy statements, of type ObjectCopy.
     */
    public static final int OBJECTCOPY = SYNCHRONIZED + 1;

    /**
     * Try statements, of type Try.
     */
    public static final int TRY = OBJECTCOPY + 1;

    /**
     * Catch clauses in try statements, of type Catch.
     */
    public static final int CATCH = TRY + 1;

    /**
     * Conditional expressions, of type Conditional.
     */
    public static final int CONDEXPR = CATCH + 1;

    /**
     * Conditional statements, of type If.
     */
    public static final int IF = CONDEXPR + 1;

    /**
     * Expression statements, of type Exec.
     */
    public static final int EXEC = IF + 1;

    /**
     * Break statements, of type Break.
     */
    public static final int BREAK = EXEC + 1;

    /**
     * Continue statements, of type Continue.
     */
    public static final int CONTINUE = BREAK + 1;

    /**
     * Return statements, of type Return.
     */
    public static final int RETURN = CONTINUE + 1;

    /**
     * Throw statements, of type Throw.
     */
    public static final int THROW = RETURN + 1;

    /**
     * Assert statements, of type Assert.
     */
    public static final int ASSERT = THROW + 1;

    /**
     * Method invocation expressions, of type Apply.
     */
    public static final int APPLY = ASSERT + 1;

    /**
     * Class instance creation expressions, of type NewClass.
     */
    public static final int NEWCLASS = APPLY + 1;

    /**
     * Array creation expressions, of type NewArray.
     */
    public static final int NEWARRAY = NEWCLASS + 1;

    /**
     * Parenthesized subexpressions, of type Parens.
     */
    public static final int PARENS = NEWARRAY + 1;

    /**
     * Assignment expressions, of type Assign.
     */
    public static final int ASSIGN = PARENS + 1;

    /**
     * Type cast expressions, of type TypeCast.
     */
    public static final int TYPECAST = ASSIGN + 1;

    /**
     * Type test expressions, of type TypeTest.
     */
    public static final int TYPETEST = TYPECAST + 1;

    /**
     * Indexed array expressions, of type Indexed.
     */
    public static final int INDEXED = TYPETEST + 1;

    /**
     * Array of constants, of type ArrayConstant
     */
    public static final int ARRAYCONSTANT = INDEXED + 1;

    /**
     * Array range access expressions, of type ArrayRange
     */
    public static final int ARRAYRANGE = ARRAYCONSTANT + 1;

    /**
     * Array access with default value, of type ArrayElement
     */
    public static final int ARRAYELEMENT = ARRAYRANGE + 1;

    /**
     * Array comprehension expressions, of type ArrayComp
     */
    public static final int ARRAYCOMP = ARRAYELEMENT + 1;

    /**
     * Deducted variable expressions, of type DeductedVar.
     */
    public static final int DEDUCTEDVAR = ARRAYCOMP + 1;

    /**
     * Selections, of type Select.
     */
    public static final int SELECT = DEDUCTEDVAR + 1;

    /**
     * Simple identifiers, of type Ident.
     */
    public static final int IDENT = SELECT + 1;

    /**
     * Literals, of type Literal.
     */
    public static final int LITERAL = IDENT + 1;

    /**
     * Basic type identifiers, of type TypeIdent.
     */
    public static final int TYPEIDENT = LITERAL + 1;

    /**
     * Class types, of type TypeClass.
     */
    public static final int TYPECLASS = TYPEIDENT + 1;

    /**
     * Array types, of type TypeArray.
     */
    public static final int TYPEARRAY = TYPECLASS + 1;

    /**
     * Deducted types, of type TypeDeducted.
     */
    public static final int TYPEDEDUCTED = TYPEARRAY + 1;

    /**
     * Parameterized types, of type TypeApply.
     */
    public static final int TYPEAPPLY = TYPEDEDUCTED + 1;

    /**
     * Formal type parameters, of type TypeParameter.
     */
    public static final int TYPEPARAMETER = TYPEAPPLY + 1;

    /**
     * Error trees, of type Erroneous.
     */
    public static final int ERRONEOUS = TYPEPARAMETER + 1;

    /**
     * Unary operators, of type Unary.
     */
    public static final int POS = ERRONEOUS + 1;
    public static final int NEG = POS + 1;
    public static final int NOT = NEG + 1;
    public static final int COMPL = NOT + 1;
    public static final int PREINC = COMPL + 1;
    public static final int PREDEC = PREINC + 1;
    public static final int POSTINC = PREDEC + 1;
    public static final int POSTDEC = POSTINC + 1;

    /**
     * unary operator for null reference checks, only used internally.
     */
    public static final int NULLCHK = POSTDEC + 1;

    /**
     * Binary operators, of type Binary.
     */
    public static final int OR = NULLCHK + 1;
    public static final int AND = OR + 1;
    public static final int BITOR = AND + 1;
    public static final int BITXOR = BITOR + 1;
    public static final int BITAND = BITXOR + 1;
    public static final int EQ = BITAND + 1;
    public static final int NE = EQ + 1;
    public static final int LT = NE + 1;
    public static final int GT = LT + 1;
    public static final int LE = GT + 1;
    public static final int GE = LE + 1;
    public static final int SL = GE + 1;
    public static final int SR = SL + 1;
    public static final int USR = SR + 1;
    public static final int PLUS = USR + 1;
    public static final int MINUS = PLUS + 1;
    public static final int MUL = MINUS + 1;
    public static final int DIV = MUL + 1;
    public static final int MOD = DIV + 1;
    public static final int ARRAYREPEAT = MOD + 1;
    public static final int ARRAYCONCAT = ARRAYREPEAT + 1;

    public static final int NULL = ARRAYREPEAT + 1;
    public static final int CALLEXPR = NULL + 1;
    public static final int THISEXPR = CALLEXPR + 1;
    public static final int READINTEXPR = THISEXPR + 1;
    public static final int READLINEEXPR = READINTEXPR + 1;
    public static final int PRINT = READLINEEXPR + 1;

    /**
     * Tags for Literal and TypeLiteral
     */
    public static final int VOID = 0;
    public static final int INT = VOID + 1;
    public static final int BOOL = INT + 1;
    public static final int STRING = BOOL + 1;


    /**
     * Guarded-if statement, of Type GuardedIf
     */
    public static final int GUARDEDIFSTMT = STRING + 1;

    /**
     * Sub-statement in guarded-if, of type GuardedSub
     */
    public static final int GUARDEDSUBSTMT = GUARDEDIFSTMT + 1;

    /**
     * For-each statement, of type Foreach
     */
    public static final int FOREACH = GUARDEDSUBSTMT + 1;


    public Location loc;
    public Type type;
    public int tag;

    /**
     * Initialize tree with given tag.
     */
    public Tree(int tag, Location loc) {
        super();
        this.tag = tag;
        this.loc = loc;
    }

    public Location getLocation() {
        return loc;
    }

    /**
     * Set type field and return this tree.
     */
    public Tree setType(Type type) {
        this.type = type;
        return this;
    }

    /**
     * Visit this tree with a given visitor.
     */
    public void accept(Visitor v) {
        v.visitTree(this);
    }

    public abstract void printTo(IndentPrintWriter pw);

    public static class TopLevel extends Tree {

        public List<ClassDef> classes;
        public Class main;
        public GlobalScope globalScope;

        public TopLevel(List<ClassDef> classes, Location loc) {
            super(TOPLEVEL, loc);
            this.classes = classes;
        }

        @Override
        public void accept(Visitor v) {
            v.visitTopLevel(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("program");
            pw.incIndent();
            for (ClassDef d : classes) {
                d.printTo(pw);
            }
            pw.decIndent();
        }
    }

    public static class ClassDef extends Tree {

        public String name;
        public String parent;
        public List<Tree> fields;
        public boolean sealed;
        public Class symbol;

        public ClassDef(String name, String parent, List<Tree> fields, boolean sealed,
                        Location loc) {
            super(CLASSDEF, loc);
            this.name = name;
            this.parent = parent;
            this.fields = fields;
            this.sealed = sealed;
        }

        @Override
        public void accept(Visitor v) {
            v.visitClassDef(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            if (sealed) pw.print("sealed ");
            pw.println("class " + name + " "
                    + (parent != null ? parent : "<empty>"));
            pw.incIndent();
            for (Tree f : fields) {
                f.printTo(pw);
            }
            pw.decIndent();
        }
    }

    public static class MethodDef extends Tree {

        public boolean statik;
        public String name;
        public TypeLiteral returnType;
        public List<VarDef> formals;
        public Block body;
        public Function symbol;

        public MethodDef(boolean statik, String name, TypeLiteral returnType,
                         List<VarDef> formals, Block body, Location loc) {
            super(METHODDEF, loc);
            this.statik = statik;
            this.name = name;
            this.returnType = returnType;
            this.formals = formals;
            this.body = body;
        }

        public void accept(Visitor v) {
            v.visitMethodDef(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            if (statik) {
                pw.print("static ");
            }
            pw.print("func " + name + " ");
            returnType.printTo(pw);
            pw.println();
            pw.incIndent();
            pw.println("formals");
            pw.incIndent();
            for (VarDef d : formals) {
                d.printTo(pw);
            }
            pw.decIndent();
            body.printTo(pw);
            pw.decIndent();
        }
    }

    public static class VarDef extends Tree {

        public String name;
        public TypeLiteral type;
        public boolean isBinding = false;
        public Variable symbol;

        public VarDef(String name, TypeLiteral type, Location loc) {
            super(VARDEF, loc);
            this.name = name;
            this.type = type;
        }

        public VarDef(String name, TypeLiteral type, Location loc, boolean bind) {
            super(VARDEF, loc);
            this.name = name;
            this.type = type;
            this.isBinding = bind;
        }

        @Override
        public void accept(Visitor v) {
            v.visitVarDef(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            if (isBinding) {
                pw.print("varbind " + name + " ");
            } else {
                pw.print("vardef " + name + " ");
            }
            type.printTo(pw);
            pw.println();
        }
    }

    /**
     * A no-op statement ";".
     */
    public static class Skip extends Tree {

        public Skip(Location loc) {
            super(SKIP, loc);
        }

        @Override
        public void accept(Visitor v) {
            v.visitSkip(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            //print nothing
        }
    }

    public static class Block extends Tree {

        public List<Tree> block;
        public LocalScope associatedScope;

        public Block(List<Tree> block, Location loc) {
            super(BLOCK, loc);
            this.block = block;
        }

        @Override
        public void accept(Visitor v) {
            v.visitBlock(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("stmtblock");
            pw.incIndent();
            for (Tree s : block) {
                s.printTo(pw);
            }
            pw.decIndent();
        }
    }

    /**
     * A while loop
     */
    public static class WhileLoop extends Tree {

        public Expr condition;
        public Tree loopBody;

        public WhileLoop(Expr condition, Tree loopBody, Location loc) {
            super(WHILELOOP, loc);
            this.condition = condition;
            this.loopBody = loopBody;
        }

        @Override
        public void accept(Visitor v) {
            v.visitWhileLoop(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("while");
            pw.incIndent();
            condition.printTo(pw);
            if (loopBody != null) {
                loopBody.printTo(pw);
            }
            pw.decIndent();
        }
    }

    /**
     * A for loop.
     */
    public static class ForLoop extends Tree {

        public Tree init;
        public Expr condition;
        public Tree update;
        public Tree loopBody;

        public ForLoop(Tree init, Expr condition, Tree update,
                       Tree loopBody, Location loc) {
            super(FORLOOP, loc);
            this.init = init;
            this.condition = condition;
            this.update = update;
            this.loopBody = loopBody;
        }

        @Override
        public void accept(Visitor v) {
            v.visitForLoop(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("for");
            pw.incIndent();
            if (init != null) {
                init.printTo(pw);
            } else {
                pw.println("<emtpy>");
            }
            condition.printTo(pw);
            if (update != null) {
                update.printTo(pw);
            } else {
                pw.println("<empty>");
            }
            if (loopBody != null) {
                loopBody.printTo(pw);
            }
            pw.decIndent();
        }
    }

    /**
     * An "if ( ) { } else { }" block
     */
    public static class If extends Tree {

        public Expr condition;
        public Tree trueBranch;
        public Tree falseBranch;

        public If(Expr condition, Tree trueBranch, Tree falseBranch,
                  Location loc) {
            super(IF, loc);
            this.condition = condition;
            this.trueBranch = trueBranch;
            this.falseBranch = falseBranch;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIf(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("if");
            pw.incIndent();
            condition.printTo(pw);
            if (trueBranch != null) {
                trueBranch.printTo(pw);
            }
            pw.decIndent();
            if (falseBranch != null) {
                pw.println("else");
                pw.incIndent();
                falseBranch.printTo(pw);
                pw.decIndent();
            }
        }
    }

    /**
     * an expression statement
     * @param expr expression structure
     */
    public static class Exec extends Tree {

        public Expr expr;

        public Exec(Expr expr, Location loc) {
            super(EXEC, loc);
            this.expr = expr;
        }

        @Override
        public void accept(Visitor v) {
            v.visitExec(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            expr.printTo(pw);
        }
    }

    /**
     * A break from a loop.
     */
    public static class Break extends Tree {

        public Break(Location loc) {
            super(BREAK, loc);
        }

        @Override
        public void accept(Visitor v) {
            v.visitBreak(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("break");
        }
    }

    /**
     * A return statement.
     */
    public static class Print extends Tree {

        public List<Expr> exprs;

        public Print(List<Expr> exprs, Location loc) {
            super(PRINT, loc);
            this.exprs = exprs;
        }

        @Override
        public void accept(Visitor v) {
            v.visitPrint(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("print");
            pw.incIndent();
            for (Expr e : exprs) {
                e.printTo(pw);
            }
            pw.decIndent();
        }
    }

    /**
     * A return statement.
     */
    public static class Return extends Tree {

        public Expr expr;

        public Return(Expr expr, Location loc) {
            super(RETURN, loc);
            this.expr = expr;
        }

        @Override
        public void accept(Visitor v) {
            v.visitReturn(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("return");
            if (expr != null) {
                pw.incIndent();
                expr.printTo(pw);
                pw.decIndent();
            }
        }
    }

    /**
     * A object copy statement
     */
    public static class ObjectCopy extends Tree {
        public String identifier;
        public Tree.Ident ident;
        public Expr expr;

        public ObjectCopy(Tree.Ident ident, Expr expr, Location loc) {
            super(OBJECTCOPY, loc);
            this.ident = ident;
            this.expr = expr;
            this.identifier = ident.name;
        }

        @Override
        public void accept(Visitor v) {
            v.visitObjectCopy(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("scopy");
            pw.incIndent();
            pw.println(identifier);
            if (expr != null) {
                expr.printTo(pw);
            }
            pw.decIndent();
        }

    }

    public static class Foreach extends Tree {
        public VarDef varDef;
        public Expr source;
        public Expr condition;
        public Tree stmt;
        // wrap single statement in a block
        public Block stmts;
        public LocalScope associatedScope;

        public Foreach(VarDef varDef, Expr source, Expr condition, Tree stmt, Location loc) {
            super(FOREACH, loc);
            this.varDef = varDef;
            this.source = source;
            this.condition = condition;
            this.stmt = stmt;
            if (this.stmt instanceof Block) {
                this.stmts = (Block) this.stmt;
            } else {
                var stmtList = new ArrayList<Tree>();
                stmtList.add(this.stmt);
                this.stmts = new Block(stmtList, loc);
            }
        }

        @Override
        public void accept(Visitor v) {
            v.visitForeach(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("foreach");
            pw.incIndent();
            varDef.printTo(pw);
            source.printTo(pw);
            condition.printTo(pw);
            stmt.printTo(pw);
            pw.decIndent();
        }
    }


    public static class GuardedIf extends Tree {

        public List<Tree.GuardedSub> guards;

        public GuardedIf(List<Tree> guards, Location loc) {
            super(GUARDEDIFSTMT, loc);
            this.guards = new ArrayList<GuardedSub>();
            for (Tree guard : guards) {
                this.guards.add((Tree.GuardedSub)guard);
            }
        }


        @Override
        public void accept(Visitor v) {
            v.visitGuardedIf(this);
        }


        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("guarded");
            pw.incIndent();
            if (guards.isEmpty()) {
                pw.println("<empty>");
            } else {
                for (Tree.GuardedSub guards : guards) {
                    guards.printTo(pw);
                }
            }
            pw.decIndent();
        }
    }


    public static class GuardedSub extends Tree {

        public Expr expr;
        public Tree stmt;

        public GuardedSub(Expr expr, Tree stmt, Location loc) {
            super(GUARDEDSUBSTMT, loc);
            this.expr = expr;
            this.stmt = stmt;
        }

        @Override
        public void accept(Visitor v) {
            v.visitGuardedSub(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("guard");
            pw.incIndent();
            expr.printTo(pw);
            stmt.printTo(pw);
            pw.decIndent();
        }
    }


    public abstract static class Expr extends Tree {

        public Type type;
        public Temp val;
        public boolean isClass;
        public boolean usedForRef;

        public Expr(int tag, Location loc) {
            super(tag, loc);
        }
    }

    /**
     * A method invocation
     */
    public static class Apply extends Expr {

        public Expr receiver;
        public String method;
        public List<Expr> actuals;
        public Function symbol;
        public boolean isArrayLength;

        public Apply(Expr receiver, String method, List<Expr> actuals,
                     Location loc) {
            super(APPLY, loc);
            this.receiver = receiver;
            this.method = method;
            this.actuals = actuals;
        }

        @Override
        public void accept(Visitor v) {
            v.visitApply(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("call " + method);
            pw.incIndent();
            if (receiver != null) {
                receiver.printTo(pw);
            } else {
                pw.println("<empty>");
            }

            for (Expr e : actuals) {
                e.printTo(pw);
            }
            pw.decIndent();
        }
    }

    /**
     * A new(...) operation.
     */
    public static class NewClass extends Expr {

        public String className;
        public Class symbol;

        public NewClass(String className, Location loc) {
            super(NEWCLASS, loc);
            this.className = className;
        }

        @Override
        public void accept(Visitor v) {
            v.visitNewClass(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("newobj " + className);
        }
    }

    /**
     * A new[...] operation.
     */
    public static class NewArray extends Expr {

        public TypeLiteral elementType;
        public Expr length;

        public NewArray(TypeLiteral elementType, Expr length, Location loc) {
            super(NEWARRAY, loc);
            this.elementType = elementType;
            this.length = length;
        }

        @Override
        public void accept(Visitor v) {
            v.visitNewArray(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.print("newarray ");
            elementType.printTo(pw);
            pw.println();
            pw.incIndent();
            length.printTo(pw);
            pw.decIndent();
        }
    }

    public abstract static class LValue extends Expr {

        public enum Kind {
            LOCAL_VAR, PARAM_VAR, MEMBER_VAR, ARRAY_ELEMENT
        }
        public Kind lvKind;

        LValue(int tag, Location loc) {
            super(tag, loc);
        }
    }

    /**
     * A assignment with "=".
     */
    public static class Assign extends Tree {

        public LValue left;
        public Expr expr;

        public Assign(LValue left, Expr expr, Location loc) {
            super(ASSIGN, loc);
            this.left = left;
            this.expr = expr;
        }

        @Override
        public void accept(Visitor v) {
            v.visitAssign(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("assign");
            pw.incIndent();
            left.printTo(pw);
            expr.printTo(pw);
            pw.decIndent();
        }
    }


    /**
     * A unary operation.
     */
    public static class Unary extends Expr {

        public Expr expr;

        public Unary(int kind, Expr expr, Location loc) {
            super(kind, loc);
            this.expr = expr;
        }

        private void unaryOperatorToString(IndentPrintWriter pw, String op) {
            pw.println(op);
            pw.incIndent();
            expr.printTo(pw);
            pw.decIndent();
        }

        @Override
        public void accept(Visitor v) {
            v.visitUnary(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            switch (tag) {
                case NEG:
                    unaryOperatorToString(pw, "neg");
                    break;
                case NOT:
                    unaryOperatorToString(pw, "not");
                    break;
            }
        }
    }

    /**
     * A binary operation.
     */
    public static class Binary extends Expr {

        public Expr left;
        public Expr right;

        public Binary(int kind, Expr left, Expr right, Location loc) {
            super(kind, loc);
            this.left = left;
            this.right = right;
        }

        private void binaryOperatorPrintTo(IndentPrintWriter pw, String op) {
            pw.println(op);
            pw.incIndent();
            left.printTo(pw);
            right.printTo(pw);
            pw.decIndent();
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitBinary(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            switch (tag) {
                case PLUS:
                    binaryOperatorPrintTo(pw, "add");
                    break;
                case MINUS:
                    binaryOperatorPrintTo(pw, "sub");
                    break;
                case MUL:
                    binaryOperatorPrintTo(pw, "mul");
                    break;
                case DIV:
                    binaryOperatorPrintTo(pw, "div");
                    break;
                case MOD:
                    binaryOperatorPrintTo(pw, "mod");
                    break;
                case AND:
                    binaryOperatorPrintTo(pw, "and");
                    break;
                case OR:
                    binaryOperatorPrintTo(pw, "or");
                    break;
                case EQ:
                    binaryOperatorPrintTo(pw, "equ");
                    break;
                case NE:
                    binaryOperatorPrintTo(pw, "neq");
                    break;
                case LT:
                    binaryOperatorPrintTo(pw, "les");
                    break;
                case LE:
                    binaryOperatorPrintTo(pw, "leq");
                    break;
                case GT:
                    binaryOperatorPrintTo(pw, "gtr");
                    break;
                case GE:
                    binaryOperatorPrintTo(pw, "geq");
                    break;
                case ARRAYREPEAT:
                    binaryOperatorPrintTo(pw, "array repeat");
                    break;
                case ARRAYCONCAT:
                    binaryOperatorPrintTo(pw, "array concat");
                    break;
            }
        }
    }

    public static class CallExpr extends Expr {

        public Expr receiver;

        public String method;

        public List<Expr> actuals;

        public Function symbol;

        public boolean isArrayLength;

        public CallExpr(Expr receiver, String method, List<Expr> actuals,
                        Location loc) {
            super(CALLEXPR, loc);
            this.receiver = receiver;
            this.method = method;
            this.actuals = actuals;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitCallExpr(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("call " + method);
            pw.incIndent();
            if (receiver != null) {
                receiver.printTo(pw);
            } else {
                pw.println("<empty>");
            }

            for (Expr e : actuals) {
                e.printTo(pw);
            }
            pw.decIndent();
        }
    }

    public static class ReadIntExpr extends Expr {

        public ReadIntExpr(Location loc) {
            super(READINTEXPR, loc);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitReadIntExpr(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("readint");
        }
    }

    public static class ReadLineExpr extends Expr {

        public ReadLineExpr(Location loc) {
            super(READLINEEXPR, loc);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitReadLineExpr(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("readline");
        }
    }

    public static class ThisExpr extends Expr {

        public ThisExpr(Location loc) {
            super(THISEXPR, loc);
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitThisExpr(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("this");
        }
    }

    /**
     * A type cast.
     */
    public static class TypeCast extends Expr {

        public String className;
        public Expr expr;
        public Class symbol;

        public TypeCast(String className, Expr expr, Location loc) {
            super(TYPECAST, loc);
            this.className = className;
            this.expr = expr;
        }

        @Override
        public void accept(Visitor v) {
            v.visitTypeCast(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("classcast");
            pw.incIndent();
            pw.println(className);
            expr.printTo(pw);
            pw.decIndent();
        }
    }

    /**
     * instanceof expression
     */
    public static class TypeTest extends Expr {

        public Expr instance;
        public String className;
        public Class symbol;

        public TypeTest(Expr instance, String className, Location loc) {
            super(TYPETEST, loc);
            this.instance = instance;
            this.className = className;
        }

        @Override
        public void accept(Visitor v) {
            v.visitTypeTest(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("instanceof");
            pw.incIndent();
            instance.printTo(pw);
            pw.println(className);
            pw.decIndent();
        }
    }


    /**
     * An identifier
     */
    public static class Ident extends LValue {

        public Expr owner;
        public String name;
        public Variable symbol;
        public boolean isDefined;

        public Ident(Expr owner, String name, Location loc) {
            super(IDENT, loc);
            this.owner = owner;
            this.name = name;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIdent(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("varref " + name);
            if (owner != null) {
                pw.incIndent();
                owner.printTo(pw);
                pw.decIndent();
            }
        }
    }

    /**
     * A constant value given literally.
     * @param value value representation
     */
    public static class Literal extends Expr {

        public int typeTag;
        public Object value;

        public Literal(int typeTag, Object value, Location loc) {
            super(LITERAL, loc);
            this.typeTag = typeTag;
            this.value = value;
        }

        @Override
        public void accept(Visitor v) {
            v.visitLiteral(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            switch (typeTag) {
                case INT:
                    pw.println("intconst " + value);
                    break;
                case BOOL:
                    pw.println("boolconst " + value);
                    break;
                default:
                    pw.println("stringconst " + MiscUtils.quote((String)value));
            }
        }
    }
    public static class Null extends Expr {

        public Null(Location loc) {
            super(NULL, loc);
        }

        @Override
        public void accept(Visitor v) {
            v.visitNull(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("null");
        }
    }

    public static class ArrayConstant extends Expr {

        public List<Expr> constants;

        public ArrayConstant(List<Expr> constants, Location loc) {
            super(ARRAYCONSTANT, loc);
            this.constants = constants;
        }

        @Override
        public void accept(Visitor v) {
            v.visitArrayConstant(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("array const");
            pw.incIndent();
            if (constants == null || constants.isEmpty()) {
                pw.println("<empty>");
            } else {
                for (Expr constant : constants) {
                    constant.printTo(pw);
                }
            }
            pw.decIndent();
        }
    }

    /**
     * Access certain range of one array
     */
    public static class ArrayRange extends Expr {
        public Expr array;
        public Expr from;
        public Expr to;

        public ArrayRange(Expr array, Expr from, Expr to, Location loc) {
            super(ARRAYRANGE, loc);
            this.array = array;
            this.from = from;
            this.to = to;
        }

        @Override
        public void accept(Visitor v) {
            v.visitArrayRange(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("arrref");
            pw.incIndent();
            array.printTo(pw);

            pw.println("range");
            pw.incIndent();
            from.printTo(pw);
            to.printTo(pw);
            pw.decIndent();

            pw.decIndent();
        }
    }

    /**
     * Access certain element of one array
     */
    public static class ArrayElement extends Expr {
        public Expr array;
        public Expr index;
        public Expr defaultValue;

        public ArrayElement(Expr array, Expr index, Expr defaultValue, Location loc) {
            super(ARRAYELEMENT, loc);
            this.array = array;
            this.index = index;
            this.defaultValue = defaultValue;
        }

        @Override
        public void accept(Visitor v) {
            v.visitArrayElement(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("arrref");
            pw.incIndent();
            array.printTo(pw);
            index.printTo(pw);

            pw.println("default");
            pw.incIndent();
            defaultValue.printTo(pw);
            pw.decIndent();

            pw.decIndent();
        }
    }

    /**
     * Array comprehension expression
     */
    public static class ArrayComp extends Expr {
        public String varName;
        public Expr source;
        public Expr condition;
        public Expr result;

        public ArrayComp(String varName, Expr source, Expr condition, Expr result, Location loc) {
            super(ARRAYCOMP, loc);
            this.varName = varName;
            this.source = source;
            this.condition = condition;
            this.result = result;
        }

        @Override
        public void accept(Visitor v) {
            v.visitArrayComp(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("array comp");
            pw.incIndent();
            pw.println("varbind " + varName);
            source.printTo(pw);
            condition.printTo(pw);
            result.printTo(pw);
            pw.decIndent();
        }
    }

    /**
     * An array selection
     */
    public static class Indexed extends LValue {

        public Expr array;
        public Expr index;

        public Indexed(Expr array, Expr index, Location loc) {
            super(INDEXED, loc);
            this.array = array;
            this.index = index;
        }

        @Override
        public void accept(Visitor v) {
            v.visitIndexed(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("arrref");
            pw.incIndent();
            array.printTo(pw);
            index.printTo(pw);
            pw.decIndent();
        }
    }


    /**
     * A variable with automatic deducted type
     */
    public static class DeductedVar extends LValue {

        public String name;
        public VarDef def;

        public DeductedVar(String name, Location locName, Location locVar) {
            super(DEDUCTEDVAR, locName);
            this.name = name;
            this.def = new Tree.VarDef(name, new TypeDeducted(locVar), locName);
        }

        @Override
        public void accept(Visitor v) {
            v.visitDeductedVar(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.println("var " + name);
        }
    }


    public static abstract class TypeLiteral extends Tree {

        public Type type;

        public TypeLiteral(int tag, Location loc){
            super(tag, loc);
        }
    }

    /**
     * Identifies a basic type.
     * @param tag the basic type id
     * @see SemanticConstants
     */
    public static class TypeIdent extends TypeLiteral {

        public int typeTag;

        public TypeIdent(int typeTag, Location loc) {
            super(TYPEIDENT, loc);
            this.typeTag = typeTag;
        }

        @Override
        public void accept(Visitor v) {
            v.visitTypeIdent(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            switch (typeTag){
                case INT:
                    pw.print("inttype");
                    break;
                case BOOL:
                    pw.print("booltype");
                    break;
                case VOID:
                    pw.print("voidtype");
                    break;
                default:
                    pw.print("stringtype");
            }
        }
    }

    public static class TypeDeducted extends TypeLiteral {

        public TypeDeducted(Location loc) {
            super(TYPEDEDUCTED, loc);
        }

        @Override
        public void accept(Visitor v) {
            v.visitTypeDeducted(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.print("var");
        }
    }

    public static class TypeClass extends TypeLiteral {

        public String name;

        public TypeClass(String name, Location loc) {
            super(TYPECLASS, loc);
            this.name = name;
        }

        @Override
        public void accept(Visitor visitor) {
            visitor.visitTypeClass(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.print("classtype " + name);
        }
    }

    /**
     * An array type, A[]
     */
    public static class TypeArray extends TypeLiteral {

        public TypeLiteral elementType;

        public TypeArray(TypeLiteral elementType, Location loc) {
            super(TYPEARRAY, loc);
            this.elementType = elementType;
        }

        @Override
        public void accept(Visitor v) {
            v.visitTypeArray(this);
        }

        @Override
        public void printTo(IndentPrintWriter pw) {
            pw.print("arrtype ");
            elementType.printTo(pw);
        }
    }

    /**
     * A generic visitor class for trees.
     */
    public static class Visitor {

        public Visitor() {
            super();
        }

        public void visitTopLevel(TopLevel that) {
            visitTree(that);
        }

        public void visitClassDef(ClassDef that) {
            visitTree(that);
        }

        public void visitMethodDef(MethodDef that) {
            visitTree(that);
        }

        public void visitVarDef(VarDef that) {
            visitTree(that);
        }

        public void visitSkip(Skip that) {
            visitTree(that);
        }

        public void visitBlock(Block that) {
            visitTree(that);
        }

        public void visitWhileLoop(WhileLoop that) {
            visitTree(that);
        }

        public void visitForLoop(ForLoop that) {
            visitTree(that);
        }

        public void visitForeach(Foreach that) {
            visitTree(that);
        }

        public void visitIf(If that) {
            visitTree(that);
        }

        public void visitExec(Exec that) {
            visitTree(that);
        }

        public void visitBreak(Break that) {
            visitTree(that);
        }

        public void visitReturn(Return that) {
            visitTree(that);
        }

        public void visitObjectCopy(ObjectCopy that) {
            visitTree(that);
        }

        public void visitGuardedIf(GuardedIf that) {
            visitTree(that);
        }

        public void visitGuardedSub(GuardedSub that) {
            visitTree(that);
        }

        public void visitApply(Apply that) {
            visitTree(that);
        }

        public void visitNewClass(NewClass that) {
            visitTree(that);
        }

        public void visitNewArray(NewArray that) {
            visitTree(that);
        }

        public void visitAssign(Assign that) {
            visitTree(that);
        }

        public void visitUnary(Unary that) {
            visitTree(that);
        }

        public void visitBinary(Binary that) {
            visitTree(that);
        }

        public void visitCallExpr(CallExpr that) {
            visitTree(that);
        }

        public void visitReadIntExpr(ReadIntExpr that) {
            visitTree(that);
        }

        public void visitReadLineExpr(ReadLineExpr that) {
            visitTree(that);
        }

        public void visitPrint(Print that) {
            visitTree(that);
        }

        public void visitArrayConstant(ArrayConstant that) {
            visitTree(that);
        }

        public void visitArrayRange(ArrayRange that) {
            visitTree(that);
        }

        public void visitArrayElement(ArrayElement that) {
            visitTree(that);
        }

        public void visitArrayComp(ArrayComp that) {
            visitTree(that);
        }

        public void visitThisExpr(ThisExpr that) {
            visitTree(that);
        }

        public void visitLValue(LValue that) {
            visitTree(that);
        }

        public void visitTypeCast(TypeCast that) {
            visitTree(that);
        }

        public void visitTypeTest(TypeTest that) {
            visitTree(that);
        }

        public void visitIndexed(Indexed that) {
            visitTree(that);
        }

        public void visitDeductedVar(DeductedVar that) {
            visitTree(that);
        }

        public void visitIdent(Ident that) {
            visitTree(that);
        }

        public void visitLiteral(Literal that) {
            visitTree(that);
        }

        public void visitNull(Null that) {
            visitTree(that);
        }

        public void visitTypeIdent(TypeIdent that) {
            visitTree(that);
        }

        public void visitTypeClass(TypeClass that) {
            visitTree(that);
        }

        public void visitTypeArray(TypeArray that) {
            visitTree(that);
        }

        public void visitTypeDeducted(TypeDeducted that) {
            visitTree(that);
        }

        public void visitTree(Tree that) {
            assert false;
        }
    }
}
