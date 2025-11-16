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
package org.jeasy.rules.jexl;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.assertj.core.api.Assertions;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.support.composite.UnitRuleGroup;
import org.jeasy.rules.support.reader.JsonRuleDefinitionReader;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Lauri Kimmel
 * @author Mahmoud Ben Hassine
 */
public class JexlRuleFactoryTest {

    public static Collection<Object[]> parameters() {
        Map<String, Object> namespaces = new HashMap<>();
        namespaces.put("sout", System.out);
        JexlEngine jexlEngine = new JexlBuilder()
                .namespaces(namespaces)
                .strict(false)
                .create();
        return Arrays.asList(new Object[][]{
                {new JexlRuleFactory(new YamlRuleDefinitionReader(), jexlEngine), "yml"},
                {new JexlRuleFactory(new JsonRuleDefinitionReader(), jexlEngine), "json"},
        });
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRulesCreation(JexlRuleFactory factory, String ext) throws Exception {
        // given
        File rulesDescriptor = new File("src/test/resources/rules." + ext);

        // when
        Rules rules = factory.createRules(new FileReader(rulesDescriptor));

        // then
        assertThat(rules).hasSize(2);
        Iterator<Rule> iterator = rules.iterator();

        Rule rule = iterator.next();
        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("adult rule");
        assertThat(rule.getDescription()).isEqualTo("when age is greater than 18, then mark as adult");
        assertThat(rule.getPriority()).isEqualTo(1);

        rule = iterator.next();
        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("weather rule");
        assertThat(rule.getDescription()).isEqualTo("when it rains, then take an umbrella");
        assertThat(rule.getPriority()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRuleCreationFromFileReader(JexlRuleFactory factory, String ext) throws Exception {
        // given
        Reader adultRuleDescriptorAsReader = new FileReader("src/test/resources/adult-rule." + ext);

        // when
        Rule adultRule = factory.createRule(adultRuleDescriptorAsReader);

        // then
        assertThat(adultRule.getName()).isEqualTo("adult rule");
        assertThat(adultRule.getDescription()).isEqualTo("when age is greater than 18, then mark as adult");
        assertThat(adultRule.getPriority()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRuleCreationFromStringReader(JexlRuleFactory factory, String ext) throws Exception {
        // given
        Reader adultRuleDescriptorAsReader = new StringReader(new String(Files.readAllBytes(Paths.get("src/test/resources/adult-rule." + ext))));

        // when
        Rule adultRule = factory.createRule(adultRuleDescriptorAsReader);

        // then
        assertThat(adultRule.getName()).isEqualTo("adult rule");
        assertThat(adultRule.getDescription()).isEqualTo("when age is greater than 18, then mark as adult");
        assertThat(adultRule.getPriority()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRuleCreationFromFileReader_withCompositeRules(JexlRuleFactory factory, String ext) throws Exception {
        // given
        File rulesDescriptor = new File("src/test/resources/composite-rules." + ext);

        // when
        Rules rules = factory.createRules(new FileReader(rulesDescriptor));

        // then
        assertThat(rules).hasSize(2);
        Iterator<Rule> iterator = rules.iterator();

        Rule rule = iterator.next();
        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("Movie id rule");
        assertThat(rule.getDescription()).isEqualTo("description");
        assertThat(rule.getPriority()).isEqualTo(1);
        assertThat(rule).isInstanceOf(UnitRuleGroup.class);

        rule = iterator.next();
        assertThat(rule).isNotNull();
        assertThat(rule.getName()).isEqualTo("weather rule");
        assertThat(rule.getDescription()).isEqualTo("when it rains, then take an umbrella");
        assertThat(rule.getPriority()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRuleCreationFromFileReader_withInvalidCompositeRuleType(JexlRuleFactory factory, String ext) {
        // given
        File rulesDescriptor = new File("src/test/resources/composite-rule-invalid-composite-rule-type." + ext);

        // when
        Assertions.assertThatThrownBy(() -> factory.createRule(new FileReader(rulesDescriptor)))
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessage("Invalid composite rule type, must be one of [UnitRuleGroup, ConditionalRuleGroup, ActivationRuleGroup]");

        // then
        // expected exception
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRuleCreationFromFileReader_withEmptyComposingRules(JexlRuleFactory factory, String ext) {
        // given
        File rulesDescriptor = new File("src/test/resources/composite-rule-invalid-empty-composing-rules." + ext);

        // when
        Assertions.assertThatThrownBy(() -> factory.createRule(new FileReader(rulesDescriptor)))
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessage("Composite rules must have composing rules specified");

        // then
        // expected exception
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRuleCreationFromFileReader_withNonCompositeRuleDeclaresComposingRules(JexlRuleFactory factory, String ext) {
        // given
        File rulesDescriptor = new File("src/test/resources/non-composite-rule-with-composing-rules." + ext);

        // when
        Assertions.assertThatThrownBy(() -> factory.createRule(new FileReader(rulesDescriptor)))
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessage("Non-composite rules cannot have composing rules");

        // then
        // expected exception
    }
}
