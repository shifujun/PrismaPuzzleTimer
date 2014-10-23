package com.puzzletimer.models;

import static com.puzzletimer.Internationalization.translate;

public class PuzzleInfo {
    private final String puzzleId;

    public PuzzleInfo(String puzzleId) {
        this.puzzleId = puzzleId;
    }

    public String getPuzzleId() {
        return this.puzzleId;
    }

    public String getDescription() {
        return translate("puzzle." + this.puzzleId);
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
