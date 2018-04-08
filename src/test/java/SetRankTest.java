import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import xyz.sethy.permissions.dto.PermissionsUser;

import java.util.UUID;

public class SetRankTest {
    private PermissionsUser permissionsUser;

    @Rule
    public BenchmarkRule methodRule = new BenchmarkRule();

    @Before
    public void before() {
        this.permissionsUser = new PermissionsUser(UUID.randomUUID());
    }

    @BenchmarkOptions(benchmarkRounds = 1000000, warmupRounds = 10)
    @Test
    public void test() {
        this.permissionsUser.getGroup().lazySet("owner");
        this.permissionsUser.getNeedsUpdating().lazySet(true);
    }
}
