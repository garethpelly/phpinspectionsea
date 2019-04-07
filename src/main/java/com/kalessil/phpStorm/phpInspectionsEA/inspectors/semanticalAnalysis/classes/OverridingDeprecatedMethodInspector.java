package com.kalessil.phpStorm.phpInspectionsEA.inspectors.semanticalAnalysis.classes;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpElementVisitor;
import com.kalessil.phpStorm.phpInspectionsEA.openApi.BasePhpInspection;
import com.kalessil.phpStorm.phpInspectionsEA.utils.NamedElementUtil;
import com.kalessil.phpStorm.phpInspectionsEA.utils.OpenapiResolveUtil;
import org.jetbrains.annotations.NotNull;

/*
 * This file is part of the Php Inspections (EA Extended) package.
 *
 * (c) Vladimir Reznichenko <kalessil@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

public class OverridingDeprecatedMethodInspector extends BasePhpInspection {
    private static final String patternNeedsDeprecation = "'%m%' overrides/implements a deprecated method. Consider refactoring or deprecate it as well.";
    private static final String patternDeprecateParent  = "Parent '%m%' probably needs to be deprecated as well.";

    @NotNull
    public String getShortName() {
        return "OverridingDeprecatedMethodInspection";
    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BasePhpElementVisitor() {
            @Override
            public void visitPhpMethod(@NotNull Method method) {
                /* do not process un-reportable classes and interfaces - we are searching real tech. debt here */
                final PhpClass clazz        = method.getContainingClass();
                final PsiElement methodName = NamedElementUtil.getNameIdentifier(method);
                if (methodName != null && clazz != null) {
                    /* search for deprecated parent methods */
                    final String searchMethodName = method.getName();
                    final PhpClass parent         = OpenapiResolveUtil.resolveSuperClass(clazz);
                    final Method parentMethod     = parent == null ? null : OpenapiResolveUtil.resolveMethod(parent, searchMethodName);
                    if (parentMethod != null) {
                        final boolean isDeprecated = method.isDeprecated();
                        if (!isDeprecated && parentMethod.isDeprecated()) {
                            final String message = patternNeedsDeprecation.replace("%m%", searchMethodName);
                            holder.registerProblem(methodName, message, ProblemHighlightType.LIKE_DEPRECATED);
                        } else if (isDeprecated && !parentMethod.isDeprecated()) {
                            final String message = patternDeprecateParent.replace("%m%", searchMethodName);
                            holder.registerProblem(methodName, message, ProblemHighlightType.WEAK_WARNING);
                        }
                    }
                }
            }
        };
    }
}
