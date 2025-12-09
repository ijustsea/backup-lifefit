package com.kh.lifeFit.domain.sleepManager;

public enum Mood {
    VERY_BAD("매우 나쁨"),
    BAD("나쁨"),
    NORMAL("보통"),
    GOOD("좋음"),
    VERY_GOOD("매우 좋음");

    private final String displayName;

    Mood(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
