package com.mockdraft.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.mockdraft.core.draft.SnakeOrder;
import com.mockdraft.core.model.Player;
import com.mockdraft.core.model.Position;

class SnakeOrderTest {

    @Test
    void oddRoundsAscendEvenRoundsDescend() {
        assertThat(SnakeOrder.slot(1, 12)).isEqualTo(1);
        assertThat(SnakeOrder.slot(12, 12)).isEqualTo(12);
        assertThat(SnakeOrder.slot(13, 12)).isEqualTo(12);
        assertThat(SnakeOrder.slot(24, 12)).isEqualTo(1);
        assertThat(SnakeOrder.slot(25, 12)).isEqualTo(1);
    }

    @Test
    void overallForIsInverseOfSlot() {
        for (int n = 4; n <= 20; n++) {
            for (int overall = 1; overall <= n * 15; overall++) {
                int round = SnakeOrder.round(overall, n);
                assertThat(SnakeOrder.overallFor(round, SnakeOrder.slot(overall, n), n)).isEqualTo(overall);
            }
        }
    }

    @Test
    void nextOverallForSlotFindsTheTurn() {
        // slot 3 of 10: picks 3, 18, 23, 38...
        assertThat(SnakeOrder.nextOverallForSlot(1, 3, 10, 15)).isEqualTo(3);
        assertThat(SnakeOrder.nextOverallForSlot(4, 3, 10, 15)).isEqualTo(18);
        assertThat(SnakeOrder.nextOverallForSlot(19, 3, 10, 15)).isEqualTo(23);
        assertThat(SnakeOrder.nextOverallForSlot(150, 3, 10, 15)).isEqualTo(-1);
    }

    @Test
    void adpHandlesExactMultiplesOfLeagueSize() {
        // the old code reported rank 24 in a 12-team league as "3.01"
        assertThat(new Player(24, "X Y", Position.WR, 100).adp(12)).isEqualTo("2.12");
        assertThat(new Player(25, "X Y", Position.WR, 100).adp(12)).isEqualTo("3.01");
        assertThat(new Player(1, "X Y", Position.WR, 100).adp(12)).isEqualTo("1.01");
    }
}
