package com.kh.lifeFit.domain.sleepManager;

public enum MorningFeeling {

    VERY_TIRED("매우 피곤"),
    TIRED("피곤"),
    NORMAL("보통"),
    REFRESHED("상쾌함"),
    VERY_REFRESHED("매우 상쾌");

    private final String displayName;

    MorningFeeling(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
