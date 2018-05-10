package eskimo.backend.entity.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Dashboard {

    private long contestId;
    protected List<DashboardRow> table = new ArrayList<>();
    private Instant lastUpdate;

    public DashboardRow getRow(long userId) {
        for (DashboardRow row : table) {
            if (userId == row.getUserId()) {
                return row;
            }
        }
        DashboardRow dashboardRow = new DashboardRow();
        dashboardRow.setUserId(userId);
        table.add(dashboardRow);
        return table.get(table.size() - 1);
    }

    public void sortTable() {
        table.sort((a, b) -> {
            if (a.getScore() != b.getScore()) {
                return Long.compare(b.getScore(), a.getScore());
            }
            return Long.compare(a.getPenalty(), b.getPenalty());
        });
    }
}
