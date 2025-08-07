package io.pillopl.library.lending.patron.model

import spock.lang.Specification

import static io.pillopl.library.lending.patron.model.PlacingOnHoldPolicy.*

class PlacingOnHoldPolicyTest extends Specification {

    def 'allCurrentPolicies should return all four policies'() {
        when:
            def policies = PlacingOnHoldPolicy.allCurrentPolicies()
        then:
            policies.size() == 4
            policies.contains(onlyResearcherPatronsCanHoldRestrictedBooksPolicy)
            policies.contains(overdueCheckoutsRejectionPolicy)
            policies.contains(regularPatronMaximumNumberOfHoldsPolicy)
            policies.contains(onlyResearcherPatronsCanPlaceOpenEndedHolds)
    }

    def 'policies should be accessible as static fields'() {
        expect:
            onlyResearcherPatronsCanHoldRestrictedBooksPolicy != null
            overdueCheckoutsRejectionPolicy != null
            regularPatronMaximumNumberOfHoldsPolicy != null
            onlyResearcherPatronsCanPlaceOpenEndedHolds != null
    }

    def 'policies should implement Function3 interface'() {
        expect:
            onlyResearcherPatronsCanHoldRestrictedBooksPolicy instanceof io.vavr.Function3
            overdueCheckoutsRejectionPolicy instanceof io.vavr.Function3
            regularPatronMaximumNumberOfHoldsPolicy instanceof io.vavr.Function3
            onlyResearcherPatronsCanPlaceOpenEndedHolds instanceof io.vavr.Function3
    }

    def 'Allowance class should exist and be accessible'() {
        when:
            def allowance = new PlacingOnHoldPolicy.Allowance()
        then:
            allowance != null
    }

    def 'Rejection class should exist and be accessible'() {
        when:
            def reason = new Rejection.Reason("test reason")
            def rejection = new PlacingOnHoldPolicy.Rejection(reason)
        then:
            rejection != null
            rejection.reason != null
            rejection.reason.reason == "test reason"
    }

    def 'Rejection.Reason should store reason text'() {
        given:
            String reasonText = "Cannot place hold due to policy violation"
        when:
            def reason = new Rejection.Reason(reasonText)
        then:
            reason.reason == reasonText
    }

    def 'should be able to create HoldDuration for policy testing'() {
        when:
            HoldDuration openEnded = HoldDuration.openEnded()
            HoldDuration closedEnded = HoldDuration.closeEnded(7)
        then:
            openEnded.isOpenEnded()
            !closedEnded.isOpenEnded()
    }
}
