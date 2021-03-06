/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.psi;

import com.intellij.JavaTestUtil;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.PsiTestCase;
import com.intellij.usageView.UsageViewLongNameLocation;
import gnu.trove.THashSet;
import gnu.trove.TObjectHashingStrategy;

import java.util.Set;

public class ClsDuplicatesTest extends PsiTestCase {
  @Override
  protected Sdk getTestProjectJdk() {
    return JavaTestUtil.getTestJdk();
  }

  public void testDuplicates() throws Exception {
    final PsiPackage rootPackage = JavaPsiFacade.getInstance(getProject()).findPackage("");
    assert rootPackage != null;
    final GlobalSearchScope scope = GlobalSearchScope.allScope(getProject());
    JavaRecursiveElementVisitor visitor = new JavaRecursiveElementVisitor() {
      @Override
      public void visitPackage(PsiPackage aPackage) {
//        System.out.println(aPackage.getQualifiedName());

        visit(aPackage);
        for (PsiPackage subPackage : aPackage.getSubPackages(scope)) {
          visitPackage(subPackage);
        }
        for (PsiClass aClass : aPackage.getClasses(scope)) {
          visitClass(aClass);
        }
      }

      @Override
      public void visitElement(PsiElement element) {
        if (element instanceof PsiNamedElement) {
          visit((PsiNamedElement)element);
        }
        super.visitElement(element);
      }

      @Override
      public void visitClass(PsiClass aClass) {
        if ("com.sun.xml.internal.bind.v2.runtime.unmarshaller.DomLoader.State".equals(aClass.getQualifiedName())) {
          int i =0;
        }
        super.visitClass(aClass);
        PsiElement parent = aClass.getParent();
        if (parent instanceof PsiFile){
          uniques.clear();
        }
      }
    };
    rootPackage.accept(visitor);
  }

  private final Set<PsiNamedElement> uniques = new THashSet<PsiNamedElement>(new TObjectHashingStrategy<PsiNamedElement>() {
    @Override
    public int computeHashCode(PsiNamedElement object) {
      String name = object.getName();
      return name == null ? 0 : name.hashCode();
    }

    @Override
    public boolean equals(PsiNamedElement o1, PsiNamedElement o2) {
      boolean eq = o1.getParent() == o2.getParent() && StringUtil.equals(o1.getName(), o2.getName()) && o1.getClass() == o2.getClass()
                  && StringUtil.equals(o1.getText(), o2.getText());

      if (eq) {
        return true;
      }
      else {
        return false;
      }
    }
  });

  private void visit(PsiNamedElement element) {
    if (!uniques.add(element)) {
      fail("Duplicate Element: " +
           ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE) +
           ": " +
           element.getText());
    }
  }
}
