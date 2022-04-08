package td;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class SrcDstMapping {

    /* 
    Can you write a function that receives a list that contains child lists
    which have 2 elements like
    ```
    # [[src, dst], [src, dst], ...]
    [[3, 2], [1, 3], [3, 0], [1, 3]]
    ```
    
    a child list consists of `src` and `dst`.
    
    The responsibility of the function is to convert the nested list into a map
     or dictionary like
    ```
    {
        # src => dst list
        1 => [3],
        3 => [0, 2],
    }
    ```
    
    The key of the map is `src`. and the value of the map is the list of destinations.
    The destination list should be de-duplicated.
    */

    @Nonnull
    public Map<Integer, List<Integer>> getMapping(@Nonnull List<List<Integer>> list) {
        Map<Integer, List<Integer>> result = new HashMap<>();

        for (List<Integer> e : list) {
            assert (e.size() == 2);

            Integer src = e.get(0);
            Integer dst = e.get(1);

            List<Integer> values = result.get(src);
            if (values == null) {
                values = new ArrayList<>();
                result.put(src, values);
            }
            if (!values.contains(dst)) {
                values.add(dst);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        List<List<Integer>> input = asList(asList(3, 2), asList(1, 3), asList(3, 0), asList(1, 3));
        SrcDstMapping solution = new SrcDstMapping();
        Map<Integer, List<Integer>> result = solution.getMapping(input);
        System.out.println(result);
    }


}
