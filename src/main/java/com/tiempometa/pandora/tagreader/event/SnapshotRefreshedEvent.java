package com.tiempometa.pandora.tagreader.event;

public class SnapshotRefreshedEvent extends DatabaseChangeEvent {

    private final int participantCount;

    public SnapshotRefreshedEvent(int participantCount) {
        this.participantCount = participantCount;
    }

    public int getParticipantCount() {
        return participantCount;
    }
}
