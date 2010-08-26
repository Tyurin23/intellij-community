package com.jetbrains.python.codeInsight.intentions;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.python.PyBundle;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * Author: Alexey.Ivanov
 * Date:   10.03.2010
 * Time:   18:52:52
 */
public class PySplitIfIntention extends BaseIntentionAction {
  @NotNull
  public String getFamilyName() {
    return PyBundle.message("INTN.split.if");
  }

  public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
    PsiElement elementAtOffset = file.findElementAt(editor.getCaretModel().getOffset());
    if (elementAtOffset == null || elementAtOffset.getNode() == null) {
      return false;
    }

    // PY-745
    final IElementType elementType = elementAtOffset.getNode().getElementType();
    if (elementType == PyTokenTypes.COLON) {
      elementAtOffset = elementAtOffset.getPrevSibling();
      elementAtOffset = PyUtil.getFirstNonCommentBefore(elementAtOffset);
    }
    else if (elementType == PyTokenTypes.IF_KEYWORD) {
      elementAtOffset = elementAtOffset.getNextSibling();
      elementAtOffset = PyUtil.getFirstNonCommentAfter(elementAtOffset);
    }

    PsiElement element = PsiTreeUtil.getParentOfType(elementAtOffset, PyBinaryExpression.class, false);
    if (element == null) {
      return false;
    }

    while (element.getParent() instanceof PyBinaryExpression) {
      element = element.getParent();
    }
    if (((PyBinaryExpression)element).getOperator() != PyTokenTypes.AND_KEYWORD
        || ((PyBinaryExpression) element).getRightExpression() == null) {
      return false;
    }
    final PsiElement parent = element.getParent();
    if (!(parent instanceof PyIfPart)) {
      return false;
    }
    setText(PyBundle.message("INTN.split.if.text"));
    return true;
  }

  public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
    PsiElement elementAtOffset = file.findElementAt(editor.getCaretModel().getOffset());
    // PY-745
    final IElementType elementType = elementAtOffset.getNode().getElementType();
    if (elementType == PyTokenTypes.COLON) {
      elementAtOffset = elementAtOffset.getPrevSibling();
      elementAtOffset = PyUtil.getFirstNonCommentBefore(elementAtOffset);
    }
    else if (elementType == PyTokenTypes.IF_KEYWORD) {
      elementAtOffset = elementAtOffset.getNextSibling();
      elementAtOffset = PyUtil.getFirstNonCommentAfter(elementAtOffset);
    }

    PyBinaryExpression element = PsiTreeUtil.getParentOfType(elementAtOffset, PyBinaryExpression.class, false);
    while (element.getParent() instanceof PyBinaryExpression) {
      element = (PyBinaryExpression)element.getParent();
    }
    PyIfStatement ifStatement = PsiTreeUtil.getParentOfType(element, PyIfStatement.class);
    PyElementGenerator elementGenerator = PyElementGenerator.getInstance(project);
    
    PyIfStatement subIf = (PyIfStatement) ifStatement.copy();
    subIf.getIfPart().getCondition().replace(element.getRightExpression());
    ifStatement.getIfPart().getCondition().replace(element.getLeftExpression());
    PyStatementList statementList = elementGenerator.createFromText(LanguageLevel.getDefault(), PyIfStatement.class, "if a:\n    a = 1").getIfPart().getStatementList();
    statementList.getStatements()[0].replace(subIf);
    ifStatement.getIfPart().getStatementList().replace(statementList);
  }
}
