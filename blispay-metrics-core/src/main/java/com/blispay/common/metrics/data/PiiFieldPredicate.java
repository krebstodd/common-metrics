package com.blispay.common.metrics.data;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PiiFieldPredicate implements Predicate<String> {

    private static final String UNDERSCORE = "_";
    private static final String HYPHEN = "-";

    private static final Set<String> blacklist = new HashSet<>();
    private static final Set<String> whitelist = new HashSet<>();

    static {

        // Person name - Handles person name, user name.
        blacklist.add("first_name");
        blacklist.add("last_name");
        blacklist.add("middle_name");
        blacklist.add("user_name");
        blacklist.add("login");
        blacklist.add("full_name");
        blacklist.add("pass_word");

        // Address - Allow state, country
        // http://www.lexisnexis.com/legalnewsroom/top-emerging-trends/b/emerging-trends-law-blog/archive/2011/02/15/california-supreme-court-zip-code-personal-identification-information-bullivant-houser-bailey-song-beverly-credit-card-act.aspx
        blacklist.add("delivery");
        blacklist.add("street");
        blacklist.add("line_1");
        blacklist.add("line_2");
        blacklist.add("line_3");
        blacklist.add("secondary_designator");
        blacklist.add("addressee");
        blacklist.add("first_line");
        blacklist.add("last_line");
        blacklist.add("latitude");
        blacklist.add("longitude");
        blacklist.add("po_box");
        blacklist.add("post_office");

        // Email
        blacklist.add("email");

        // Ssn
        blacklist.add("ssn");
        blacklist.add("social_security");
        blacklist.add("nid");
        blacklist.add("national_id");

        // dob
        blacklist.add("birth");
        blacklist.add("dob");
        blacklist.add("user_age");
        blacklist.add("customer_age");
        blacklist.add("applicant_age");
        blacklist.add("birth_place");

        // phone
        blacklist.add("phone");
        blacklist.add("telephone");
        blacklist.add("phone_number");
        blacklist.add("telephone_number");

        // card related info
        blacklist.add("cvv");
        blacklist.add("expiration");
        blacklist.add("card_number");
        blacklist.add("pan");
        blacklist.add("primary_account_number");
        blacklist.add("account_number");
        blacklist.add("card_holder");

        // blispay tokens
        blacklist.add("session_token");
        blacklist.add("agent_token");

        // Miscellaneous
        blacklist.add("drivers_license");
        blacklist.add("pass_port");

        // Add all existing fields both with underscores replaced by dashes and with them replaced by nothing (handles all one word lower, upper, camel cased)
        blacklist.addAll(replaceAll(UNDERSCORE, HYPHEN, blacklist));
        blacklist.addAll(replaceAll(UNDERSCORE, "", blacklist));

        whitelist.add("userAgent");
    }

    public PiiFieldPredicate() {
    }

    public PiiFieldPredicate(final Set<String> overrides) {
        whitelist.addAll(overrides);
    }

    private static Set<String> replaceAll(final String replace, final String with, final Set<String> set) {
        return set.stream().map(str -> str.replace(replace, with)).collect(Collectors.toSet());
    }

    @Override
    public boolean test(final String key) {
        return !whitelist.contains(key) && isBanned(key.toLowerCase(Locale.ROOT));
    }

    private boolean isBanned(final String key) {
        // Test if the key contains a banned substring.
        return blacklist.stream().anyMatch(key::contains);
    }
}
