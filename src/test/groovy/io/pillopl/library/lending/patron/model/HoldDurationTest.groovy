package io.pillopl.library.lending.patron.model

import spock.lang.Specification
import java.time.Instant

class HoldDurationTest extends Specification {

    def 'should create open-ended duration with no end date'() {
        given:
            Instant from = Instant.now()
        when:
            HoldDuration duration = HoldDuration.openEnded(from)
        then:
            duration.from == from
            duration.isOpenEnded()
            duration.getTo().isEmpty()
    }

    def 'should create closed-ended duration with specific end date'() {
        given:
            Instant from = Instant.now()
            NumberOfDays days = NumberOfDays.of(7)
        when:
            HoldDuration duration = HoldDuration.closeEnded(from, days)
        then:
            duration.from == from
            !duration.isOpenEnded()
            duration.getTo().isDefined()
    }

    def 'should create open-ended duration with current time when no start time provided'() {
        when:
            HoldDuration duration = HoldDuration.openEnded()
        then:
            duration.isOpenEnded()
            duration.getTo().isEmpty()
            duration.from != null
    }

    def 'should create closed-ended duration with current time when no start time provided'() {
        given:
            int days = 5
        when:
            HoldDuration duration = HoldDuration.closeEnded(days)
        then:
            !duration.isOpenEnded()
            duration.getTo().isDefined()
            duration.from != null
    }

    def 'should handle same-day duration correctly'() {
        given:
            Instant from = Instant.now()
            NumberOfDays oneDay = NumberOfDays.of(1)
        when:
            HoldDuration duration = HoldDuration.closeEnded(from, oneDay)
        then:
            !duration.isOpenEnded()
            duration.getTo().isDefined()
    }

    def 'should handle boundary conditions for NumberOfDays'() {
        given:
            Instant from = Instant.now()
        when:
            HoldDuration duration = HoldDuration.closeEnded(from, NumberOfDays.of(1))
        then:
            !duration.isOpenEnded()
            duration.getTo().isDefined()
    }

    def 'should create closed-ended duration using NumberOfDays factory method'() {
        given:
            NumberOfDays days = NumberOfDays.of(14)
        when:
            HoldDuration duration = HoldDuration.closeEnded(days)
        then:
            !duration.isOpenEnded()
            duration.getTo().isDefined()
            duration.from != null
    }

    def 'should handle edge case with minimum valid NumberOfDays'() {
        given:
            Instant from = Instant.now()
            NumberOfDays minDays = NumberOfDays.of(1)
        when:
            HoldDuration duration = HoldDuration.closeEnded(from, minDays)
        then:
            !duration.isOpenEnded()
            duration.getTo().isDefined()
    }

    def 'should handle large NumberOfDays values'() {
        given:
            Instant from = Instant.now()
            NumberOfDays largeDays = NumberOfDays.of(365)
        when:
            HoldDuration duration = HoldDuration.closeEnded(from, largeDays)
        then:
            !duration.isOpenEnded()
            duration.getTo().isDefined()
    }

    def 'should validate NumberOfDays creation with invalid values'() {
        when:
            NumberOfDays.of(0)
        then:
            thrown(IllegalArgumentException)
        
        when:
            NumberOfDays.of(-1)
        then:
            thrown(IllegalArgumentException)
    }

    def 'should test NumberOfDays isGreaterThan method'() {
        given:
            NumberOfDays fiveDays = NumberOfDays.of(5)
        when:
            boolean result1 = fiveDays.isGreaterThan(3)
            boolean result2 = fiveDays.isGreaterThan(5)
            boolean result3 = fiveDays.isGreaterThan(7)
        then:
            result1 == true
            result2 == false
            result3 == false
    }
}
