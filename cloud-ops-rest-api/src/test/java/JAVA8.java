import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Nathan on 2017/4/12.
 */
public class JAVA8 {
    @Test
    public void testListToMap() {
        List<User> users = Lists.newArrayList();
        users.add(User.builder("ning", "sheng").age(21).build());
        Map<String, User> result =
                users.stream().collect(Collectors.toMap(User::getFirstName,
                        Function.identity()));
        System.out.printf(result.toString());
    }
}
