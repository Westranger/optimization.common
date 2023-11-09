package test.de.westranger.optimization.common.util;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.westranger.optimization.common.util.ProgressTracker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class ProgressTrackerTest {

  private ProgressTracker tracker;

  @BeforeEach
  void setUp() {
    tracker = new ProgressTracker(1000);
  }

  @Test
  void testInitialProgress() {
    assertEquals(0.0, tracker.getProgressPercentage(), "Initial progress should be 0");
  }

  @Test
  void testProgressAfterOneRound() {
    tracker.nextRound(100);
    assertEquals(10.0, tracker.getProgressPercentage(), "Progress after one round");
  }

  @Test
  void testProgressWithInvalidValue() {
    assertThrows(IllegalArgumentException.class, () -> tracker.nextRound(-1),
        "progress is negative this is not allowed");
  }

  @Test
  void testProgressAfterMultipleRounds() {
    // Simulate time passing and multiple calls to nextRound
    tracker.nextRound(100);
    sleep(100);
    tracker.nextRound(200);
    sleep(100);
    tracker.nextRound(300);
    assertEquals(30.0, tracker.getProgressPercentage(), "Progress after multiple rounds");
  }

  @Test
  void testEstimatedCompletionDate() {
    tracker.nextRound(500);
    sleep(100);
    tracker.nextRound(750);
    long estimatedCompletionDate = tracker.getEstimatedCompletionDate();
    assertTrue(estimatedCompletionDate > System.currentTimeMillis(),
        "Estimated completion date should be in the future");
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}

