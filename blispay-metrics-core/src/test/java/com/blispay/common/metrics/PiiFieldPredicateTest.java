package com.blispay.common.metrics;

import com.blispay.common.metrics.data.PiiFieldPredicate;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PiiFieldPredicateTest {

    @Test
    public void testMapAllVersionsOfKey() {

        final List<String> expectedBanned = Arrays.asList(
                "user_name",
                "USER_NAME",
                "user-name",
                "USER-NAME",
                "username",
                "userName",
                "USERNAME",
                "jays_user_name",
                "user_name_");

        assertTrue(expectedBanned.stream().allMatch(new PiiFieldPredicate()));

    }

    @Test
    public void testOverrideBannedKey() {

        // All of these should still be banned, only allow exact match overrides.
        final List<String> expectedBanned = Arrays.asList(
                "USER_NAME",
                "user-name",
                "USER-NAME",
                "username",
                "userName",
                "USERNAME",
                "jays_user_name",
                "user_name_");

        final Set<String> overrides = new HashSet<>();
        overrides.add("user_name");

        final PiiFieldPredicate custom = new PiiFieldPredicate(overrides);

        assertTrue(expectedBanned.stream().allMatch(custom));
        assertFalse(custom.test("user_name"));

    }

}
