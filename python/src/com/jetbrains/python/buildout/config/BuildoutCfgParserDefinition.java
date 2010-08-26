package com.jetbrains.python.buildout.config;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.jetbrains.python.buildout.config.lexer.BuildoutCfgFlexLexer;
import com.jetbrains.python.buildout.config.psi.impl.BuildoutCfgPsiElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.impl.*;

/**
 * @author traff
 */
public class BuildoutCfgParserDefinition implements ParserDefinition, BuildoutCfgElementTypes, BuildoutCfgTokenTypes {

  @NotNull
  public Lexer createLexer(final Project project) {
    return new BuildoutCfgFlexLexer();
  }

  @Nullable
  public PsiParser createParser(final Project project) {
    return new BuildoutCfgParser();
  }

  public IFileElementType getFileNodeType() {
    return FILE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens() {
    return TokenSet.create(WHITESPACE);
  }

  @NotNull
  public TokenSet getCommentTokens() {
    return TokenSet.create(COMMENT);
  }

  @NotNull
  public TokenSet getStringLiteralElements() {
    return TokenSet.create(TEXT);
  }

  @NotNull
  public PsiElement createElement(final ASTNode node) {
    final IElementType type = node.getElementType();
    
    return new BuildoutCfgPsiElementImpl(node);
  }

  public PsiFile createFile(final FileViewProvider viewProvider) {
    return new BuildoutCfgFileImpl(viewProvider);
  }

  public SpaceRequirements spaceExistanceTypeBetweenTokens(final ASTNode left, final ASTNode right) {
    return SpaceRequirements.MAY;
  }
}
