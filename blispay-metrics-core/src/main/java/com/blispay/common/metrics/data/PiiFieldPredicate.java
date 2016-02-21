package com.blispay.common.metrics.data;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PiiFieldPredicate implements Predicate<String> {

    private static final Set<String> bannedSubstrings = new HashSet<>();

    static {

        // Person name - Handles person name, user name.
        bannedSubstrings.add("name");
        bannedSubstrings.add("pass_word");

        // Address - Allow state, country
        // http://www.lexisnexis.com/legalnewsroom/top-emerging-trends/b/emerging-trends-law-blog/archive/2011/02/15/california-supreme-court-zip-code-personal-identification-information-bullivant-houser-bailey-song-beverly-credit-card-act.aspx
        bannedSubstrings.add("delivery");
        bannedSubstrings.add("street");
        bannedSubstrings.add("line_1");
        bannedSubstrings.add("line_2");
        bannedSubstrings.add("line_3");
        bannedSubstrings.add("secondary_designator");
        bannedSubstrings.add("addressee");
        bannedSubstrings.add("first_line");
        bannedSubstrings.add("last_line");
        bannedSubstrings.add("zip");
        bannedSubstrings.add("plus_4_code");
        bannedSubstrings.add("latitude");
        bannedSubstrings.add("longitude");
        bannedSubstrings.add("po_box");
        bannedSubstrings.add("post_office");
        bannedSubstrings.add("postal_code");

        // Email
        bannedSubstrings.add("email");

        // Ssn
        bannedSubstrings.add("ssn");
        bannedSubstrings.add("social_security");
        bannedSubstrings.add("nid");
        bannedSubstrings.add("national_id");

        // dob
        bannedSubstrings.add("birth");
        bannedSubstrings.add("dob");
        bannedSubstrings.add("age");
        bannedSubstrings.add("birth_place");

        // phone
        bannedSubstrings.add("phone");
        bannedSubstrings.add("telephone");
        bannedSubstrings.add("phone_number");
        bannedSubstrings.add("telephone_number");

        // card related info
        bannedSubstrings.add("cvv");
        bannedSubstrings.add("expiration");
        bannedSubstrings.add("card_number");
        bannedSubstrings.add("pan");
        bannedSubstrings.add("primary_account_number");
        bannedSubstrings.add("card_holder");

        // blispay tokens
        bannedSubstrings.add("session_token");
        bannedSubstrings.add("agent_token");

        // Miscellaneous
        bannedSubstrings.add("drivers_license");
        bannedSubstrings.add("pass_port");

        // Add all existing fields both with underscores replaced by dashes and with them replaced by nothing (handles all one word lower, upper, camel cased)
        bannedSubstrings.addAll(replaceAll("_", "-", bannedSubstrings));
        bannedSubstrings.addAll(replaceAll("_", "", bannedSubstrings));

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
        return !overrides.contains(key) && isBanned(key.toLowerCase(Locale.ROOT));
    }

    public boolean isBanned(final String key) {
        // Test if the key contains a banned substring.
        return bannedSubstrings.stream().anyMatch(key::contains);
    }
}
