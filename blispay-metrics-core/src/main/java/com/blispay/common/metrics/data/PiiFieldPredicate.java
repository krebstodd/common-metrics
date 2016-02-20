package com.blispay.common.metrics.data;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PiiFieldPredicate implements Predicate<String> {

    private static final Set<String> piiFieldNames = new HashSet<>();

    static {

        // Person name
        piiFieldNames.add("first_name");
        piiFieldNames.add("middle_name");
        piiFieldNames.add("last_name");

        // Address - Allow zip, state, country
        piiFieldNames.add("line_1");
        piiFieldNames.add("line_2");
        piiFieldNames.add("line_3");

        // Ssn
        piiFieldNames.add("ssn");
        piiFieldNames.add("social_security");
        piiFieldNames.add("nid");
        piiFieldNames.add("national_id");

        // dob
        piiFieldNames.add("birth");
        piiFieldNames.add("birth");
        piiFieldNames.add("dob");
        piiFieldNames.add("age");

        // card related info
        piiFieldNames.add("cvv");
        piiFieldNames.add("expiration");
        piiFieldNames.add("card_number");
        piiFieldNames.add("pan");
        piiFieldNames.add("primary_account_number");
        piiFieldNames.add("card_holder");

        // blispay tokens
        piiFieldNames.add("blispay-session-token");
        piiFieldNames.add("blispay-agent-token");

        // Add all existing fields both with underscores replaced by dashes and with them replaced by nothing (handles all one word lower, upper, camel cased)
        piiFieldNames.addAll(replaceAll("_", "-", piiFieldNames));
        piiFieldNames.addAll(replaceAll("_", "", piiFieldNames));

    }

    private final Set<String> overrides;

    public PiiFieldPredicate() {
        this(new HashSet<>());
    }

    public PiiFieldPredicate(final Set<String> overrides) {
        this.overrides = overrides;
    }

    private static Set<String> replaceAll(final String replace, final String with, final Set<String> set) {
        return set.stream().map(str -> str.replace(replace, with)).collect(Collectors.toSet());
    }

    @Override
    public boolean test(final String key) {
        return !overrides.contains(key) && piiFieldNames.contains(key.toLowerCase(Locale.ROOT));
    }
}
