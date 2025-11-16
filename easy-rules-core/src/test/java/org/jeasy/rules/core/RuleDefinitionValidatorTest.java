/*
 * The MIT License
 *
 *  Copyright (c) 2025, Philip Hackl (philip.hackl90@gmail.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.core;


import org.assertj.core.api.Assertions;
import org.jeasy.rules.annotation.AnnotatedRuleWithActionMethodHavingMoreThanOneArgumentOfTypeFacts;
import org.jeasy.rules.annotation.AnnotatedRuleWithActionMethodHavingOneArgumentNotOfTypeFacts;
import org.jeasy.rules.annotation.AnnotatedRuleWithActionMethodHavingOneArgumentOfTypeFacts;
import org.jeasy.rules.annotation.AnnotatedRuleWithActionMethodThatReturnsNonVoidType;
import org.jeasy.rules.annotation.AnnotatedRuleWithConditionMethodHavingNonBooleanReturnType;
import org.jeasy.rules.annotation.AnnotatedRuleWithConditionMethodHavingOneArgumentNotOfTypeFacts;
import org.jeasy.rules.annotation.AnnotatedRuleWithMetaRuleAnnotation;
import org.jeasy.rules.annotation.AnnotatedRuleWithMoreThanOnePriorityMethod;
import org.jeasy.rules.annotation.AnnotatedRuleWithMultipleAnnotatedParametersAndOneParameterOfSubTypeFacts;
import org.jeasy.rules.annotation.AnnotatedRuleWithMultipleAnnotatedParametersAndOneParameterOfTypeFacts;
import org.jeasy.rules.annotation.AnnotatedRuleWithNonPublicActionMethod;
import org.jeasy.rules.annotation.AnnotatedRuleWithNonPublicConditionMethod;
import org.jeasy.rules.annotation.AnnotatedRuleWithNonPublicPriorityMethod;
import org.jeasy.rules.annotation.AnnotatedRuleWithOneParameterNotAnnotatedWithFactAndNotOfTypeFacts;
import org.jeasy.rules.annotation.AnnotatedRuleWithPriorityMethodHavingArguments;
import org.jeasy.rules.annotation.AnnotatedRuleWithPriorityMethodHavingNonIntegerReturnType;
import org.jeasy.rules.annotation.AnnotatedRuleWithoutActionMethod;
import org.jeasy.rules.annotation.AnnotatedRuleWithoutConditionMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RuleDefinitionValidatorTest {

    private RuleDefinitionValidator ruleDefinitionValidator;

    @BeforeEach
    public void setup() {
        ruleDefinitionValidator = new RuleDefinitionValidator();
    }

    /*
     * Rule annotation test
     */
    @Test
    public void notAnnotatedRuleMustNotBeAccepted() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new Object()));
    }

    @Test
    public void withCustomAnnotationThatIsItselfAnnotatedWithTheRuleAnnotation() {
        ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithMetaRuleAnnotation());
    }

    /*
     * Conditions methods tests
     */
    @Test
    public void conditionMethodMustBeDefined() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithoutConditionMethod()));
    }

    @Test
    public void conditionMethodMustBePublic() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithNonPublicConditionMethod()));
    }

    @Test
    public void whenConditionMethodHasOneNonAnnotatedParameter_thenThisParameterMustBeOfTypeFacts() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithConditionMethodHavingOneArgumentNotOfTypeFacts()));
    }

    @Test
    public void conditionMethodMustReturnBooleanType() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithConditionMethodHavingNonBooleanReturnType()));
    }

    @Test
    public void conditionMethodParametersShouldAllBeAnnotatedWithFactUnlessExactlyOneOfThemIsOfTypeFacts() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithOneParameterNotAnnotatedWithFactAndNotOfTypeFacts()));
    }

    /*
     * Action method tests
     */
    @Test
    public void actionMethodMustBeDefined() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithoutActionMethod()));
    }

    @Test
    public void actionMethodMustBePublic() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithNonPublicActionMethod()));
    }

    @Test
    public void actionMethodMustHaveAtMostOneArgumentOfTypeFacts() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithActionMethodHavingOneArgumentNotOfTypeFacts()));
    }

    @Test
    public void actionMethodMustHaveExactlyOneArgumentOfTypeFactsIfAny() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithActionMethodHavingMoreThanOneArgumentOfTypeFacts()));
    }

    @Test
    public void actionMethodMustReturnVoid() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithActionMethodThatReturnsNonVoidType()));
    }

    @Test
    public void actionMethodParametersShouldAllBeAnnotatedWithFactUnlessExactlyOneOfThemIsOfTypeFacts() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithOneParameterNotAnnotatedWithFactAndNotOfTypeFacts()));
    }

    /*
     * Priority method tests
     */

    @Test
    public void priorityMethodMustBeUnique() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithMoreThanOnePriorityMethod()));
    }

    @Test
    public void priorityMethodMustBePublic() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithNonPublicPriorityMethod()));
    }

    @Test
    public void priorityMethodMustHaveNoArguments() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithPriorityMethodHavingArguments()));
    }

    @Test
    public void priorityMethodReturnTypeMustBeInteger() {
        assertThrows(IllegalArgumentException.class, () -> ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithPriorityMethodHavingNonIntegerReturnType()));
    }

    /*
     * Valid definition tests
     */
    @Test
    public void validAnnotationsShouldBeAccepted() {
        try {
            ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithMultipleAnnotatedParametersAndOneParameterOfTypeFacts());
            ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithMultipleAnnotatedParametersAndOneParameterOfSubTypeFacts());
            ruleDefinitionValidator.validateRuleDefinition(new AnnotatedRuleWithActionMethodHavingOneArgumentOfTypeFacts());
        } catch (Throwable throwable) {
            Assertions.fail("Should not throw exception for valid rule definitions");
        }
    }
}
