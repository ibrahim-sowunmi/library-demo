package io.pillopl.library.lending.book.new_model;

import io.pillopl.library.lending.librarybranch.model.LibraryBranchId;
import io.pillopl.library.lending.patron.model.PatronId;
import io.pillopl.library.commons.aggregates.Version;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.Instant;

@RequiredArgsConstructor
@Getter
public class OnHoldState implements BookState {
    private final Book book;
    private final LibraryBranchId holdPlacedAt;
    private final PatronId byPatron;
    private final Instant holdTill;

    @Override
    public BookState placeOnHold(PatronId patronId, LibraryBranchId branchId, Instant holdTill) {
        return invalidTransition("Book is already on hold");
    }

    @Override
    public BookState checkout(PatronId patronId, LibraryBranchId branchId) {
        if (!patronId.equals(byPatron)) {
            return invalidTransition("Only the patron who placed the hold can check out the book");
        }
        return new CheckedOutState(book, branchId, patronId);
    }

    @Override
    public BookState returnBook(LibraryBranchId branchId) {
        return invalidTransition("Cannot return a book that is on hold");
    }

    @Override
    public BookState cancelHold() {
        return new AvailableState(book, holdPlacedAt);
    }

    @Override
    public BookState expireHold() {
        if (Instant.now().isAfter(holdTill)) {
            return new AvailableState(book, holdPlacedAt);
        }
        return this;
    }

    @Override
    public boolean canBeCheckedOut(PatronId patronId) {
        return patronId.equals(byPatron);
    }

    @Override
    public boolean canBePutOnHold(PatronId patronId) {
        return false;
    }

    @Override
    public boolean canBeReturned() {
        return false;
    }

    @Override
    public String getStateName() {
        return "ON_HOLD";
    }

    @Override
    public Version getVersion() {
        return book.getVersion();
    }

    @Override
    public LibraryBranchId getCurrentBranch() {
        return holdPlacedAt;
    }

    @Override
    public PatronId getCurrentPatron() {
        return byPatron;
    }
}
